package model;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Observer;
import java.util.Random;
import java.util.regex.Pattern;

public class Game {
    public enum Mode{NONE,LAN,HOST,JOIN}
    public enum State{NOTSTARTED,RUNNING,ENDED}

    private State state = State.NOTSTARTED;
    private Mode mode = Mode.NONE;

    private Board board = null;
    private Piece.Color currentMove = Piece.Color.WHITE;
    private Piece.Color playerColor = null;
    private Piece.Color preferredColor = null;
    private Piece.Color opponentPreferredColor = null;
    private Piece.Color winnerColor = null;

    private ServerSocket serverSocket = null;
    private Socket socket = null;
    private final int port = 1234;
    private ConnectionHandler connectionHandler = null;

    private Observer displayGameObserver = null;
    private Observer updateBoardObserver = null;

    private static final String IPV4_REGEX =
            "^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                    "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                    "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                    "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
    private static final Pattern IPv4_PATTERN = Pattern.compile(IPV4_REGEX);

    public Game() {
        this.board = new Board();
        this.mode = Mode.NONE;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
        stopServer();
        disconnect();
        winnerColor = null;
        if(mode!=Mode.LAN){
            state = State.NOTSTARTED;
        }else {
            state = State.RUNNING;
        }
        if(mode!= Mode.NONE)this.currentMove = Piece.Color.WHITE;
        if(mode!= Mode.NONE){
            board.putPiecesOnBoard();
        }
    }

    public Board getBoard(){
        return this.board;
    }

    public void move(Piece piece,int toPosition,boolean sendToAnotherPlayer){
        if(state==State.RUNNING) {
            if (currentMove == piece.getPieceColor()) {
                if (sendToAnotherPlayer && mode != Mode.LAN) {
                    if (currentMove != playerColor) return;
                }
                if (!sendToAnotherPlayer && mode != Mode.LAN) {
                    if (currentMove == playerColor) return;
                }

                int from = piece.getPosition();
                boolean moveWasMade = getBoard().makeMove(piece, toPosition);
                if (moveWasMade) {
                    currentMove = currentMove == Piece.Color.WHITE ? Piece.Color.BLACK : Piece.Color.WHITE;
                    if (sendToAnotherPlayer) {
                        if (mode == Mode.HOST || mode == Mode.JOIN) {
                            if (connectionHandler != null) {
                                if (connectionHandler.getStatus() == ConnectionHandler.Status.CONNECTED) {
                                    connectionHandler.write(createMoveMessage(from, toPosition));
                                }
                            }
                        }
                    }
                    if(board.checkMate(currentMove)){
                        state = State.ENDED;
                        winnerColor = piece.getPieceColor();
                    }
                    if (updateBoardObserver != null) updateBoardObserver.update(null, null);
                }
            }
        }
    }

    public void connect(String ip) throws Exception {

        if(!IPv4_PATTERN.matcher(ip).matches()) throw new Exception("Invalid IP");

        if (socket!= null){
            disconnect();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket(ip,port);
                    if(connectionHandler!=null)connectionHandler.close();
                    connectionHandler = new ConnectionHandler(socket,Game.this);
                    connectionHandler.start();
                    connectionHandler.write(createSetPreferredColorMessage(Game.this.preferredColor));
                    connectionHandler.write(createReadyMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void disconnect(){
        if(connectionHandler!=null){
            connectionHandler.close();
            connectionHandler = null;
        }

        if(socket!=null){
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            socket = null;
        }
    }

    public void startServer(){
        if (serverSocket!=null){
            stopServer();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    serverSocket = new ServerSocket(port);
                    Socket client = serverSocket.accept();
                    if(connectionHandler!=null)connectionHandler.close();
                    connectionHandler = new ConnectionHandler(client,Game.this);
                    new Thread(connectionHandler).start();
                    displayGameObserver.update(null,null);
                } catch (IOException e) {
                    //e.printStackTrace();
                }
            }
        }).start();
    }

    public void stopServer(){
        if (serverSocket!=null){
            try {
                serverSocket.close();
            } catch (IOException e) {
                //e.printStackTrace();
            }
            serverSocket = null;
        }
    }

    public boolean isServerOn(){
        return serverSocket==null? false:true;
    }

    public void interpretMessage(String message){
        if(message.startsWith("MOV")){
            message = message.replace("MOV","");
            int from = Integer.parseInt(message.split("%")[0]);
            int to = Integer.parseInt(message.split("%")[1]);
            Piece piece = board.getPiece(from);
            move(piece,to,false);
            updateBoardObserver.update(null,null);
        }else if(message.contains("READY")){
            if(preferredColor == null){
                if (opponentPreferredColor !=null){
                    playerColor = opponentPreferredColor == Piece.Color.WHITE? Piece.Color.BLACK:Piece.Color.WHITE;
                }else {
                    Random random = new Random();
                    playerColor = random.nextBoolean()? Piece.Color.WHITE: Piece.Color.BLACK;
                }
            }else {
                playerColor = preferredColor;
            }

            if(connectionHandler!=null)connectionHandler.write(createSetColorMessage(
                    playerColor == Piece.Color.BLACK? Piece.Color.WHITE : Piece.Color.BLACK));
            if(displayGameObserver!=null)displayGameObserver.update(null,null);
            if(connectionHandler!=null)connectionHandler.write(createStartMessage());

        }else if(message.startsWith("START")){
            if(displayGameObserver!=null)displayGameObserver.update(null,null);

        }else if (message.startsWith("SETPCOLOR")){
            message = message.replace("SETPCOLOR","");
            if(message == "WHITE"){
                opponentPreferredColor = Piece.Color.WHITE;
            }else if(message == "BLACK"){
                opponentPreferredColor = Piece.Color.BLACK;
            }else if(message == "NONE"){
                opponentPreferredColor = null;
            }

        }else if (message.startsWith("SETCOLOR")){
            if(message.contains("WHITE")){
                playerColor = Piece.Color.WHITE;
            }else if(message.contains("BLACK")){
                playerColor = Piece.Color.BLACK;
            }
        }else if(message.contains("SURRENDER")){
            winnerColor = playerColor;
            state = State.ENDED;
            if(updateBoardObserver!=null)updateBoardObserver.update(null,null);
        }else if(message.contains("RESTART")){
            winnerColor = null;
            board.putPiecesOnBoard();
            state = State.RUNNING;
            if(displayGameObserver!=null)displayGameObserver.update(null,null);
            if(updateBoardObserver!=null)updateBoardObserver.update(null,null);
        }
    }

    private String createMoveMessage(int from,int to){
        return "MOV" + Integer.toString(from) +"%" +Integer.toString(to);
    }

    private static String createStartMessage(){
        return "START";
    }

    private static String createReadyMessage(){
        return "READY";
    }

    private String createSetPreferredColorMessage(Piece.Color color){
        String message= "SETPCOLOR";
        if(color == null){
            message+="NONE";
        }else {
            message+=color== Piece.Color.WHITE? "WHITE" : "BLACK";
        }
        return message;
    }

    private String createSetColorMessage(Piece.Color color){
        String message= "SETCOLOR";
        if(color == null){
            throw new NullPointerException("create message - set color param is null");
        }else {
            message+=color== Piece.Color.WHITE? "WHITE" : "BLACK";
        }
        return message;
    }

    public void surrender(){
        if(mode==Mode.HOST || mode == Mode.JOIN) {
            if (state == State.RUNNING) {
                state = State.ENDED;
                winnerColor = playerColor == Piece.Color.WHITE? Piece.Color.BLACK:Piece.Color.WHITE;
                if (connectionHandler != null) connectionHandler.write("SURRENDER");
                if (updateBoardObserver != null) updateBoardObserver.update(null, null);
            }
        }
    }

    public void restart(){
        if(state == State.ENDED){
            if (connectionHandler != null) connectionHandler.write("RESTART");
            board.putPiecesOnBoard();
            state = State.RUNNING;
            winnerColor = null;
            if(displayGameObserver!=null)displayGameObserver.update(null,null);
            if(updateBoardObserver!=null)updateBoardObserver.update(null,null);
        }
    }

    public void setDisplayGameObserver(Observer displayGameObserver) {
        this.displayGameObserver = displayGameObserver;
    }

    public void setUpdateBoardObserver(Observer updateBoardObserver){
        this.updateBoardObserver = updateBoardObserver;
    }

    public void setPreferredColor(Piece.Color preferredColor) {
        this.preferredColor = preferredColor;
    }

    public Piece.Color getCurrentMove() {
        return currentMove;
    }

    public Piece.Color getPlayerColor() {
        return playerColor;
    }

    public State getState() {
        return state;
    }

    public Mode getMode() {
        return mode;
    }

    public void setState(State state) {
        if(state==null)throw new NullPointerException();
        this.state = state;
    }

    public Piece.Color getWinnerColor() {
        return winnerColor;
    }

    public void beforeClosing(){
        if(serverSocket!=null){
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(connectionHandler!=null) {
            try {
                connectionHandler.close();
                connectionHandler.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
