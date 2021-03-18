package model;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Observer;
import java.util.Random;
import java.util.regex.Pattern;

public class Game {
    public enum Mode{NONE,LAN,HOST,JOIN}

    private Mode mode = Mode.NONE;

    private Board board = null;
    private Piece.Color currentMove = Piece.Color.WHITE;
    private Piece.Color playerColor = null;
    private Piece.Color preferredColor = null;
    private Piece.Color opponentPrefferedColor = null;

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
        if(mode!= Mode.NONE)this.currentMove = Piece.Color.WHITE;
        if(mode!= Mode.NONE){
            board.putPiecesOnBoard();
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
            move(piece,to,false);//TODO for a moment out of if statement
            if(piece!=null){
                if(piece.getPieceColor()!=playerColor){
                }
            }
            updateBoardObserver.update(null,null);

        }else if(message.contains("READY")){
            if(preferredColor == null){
                if (opponentPrefferedColor!=null){
                    playerColor = opponentPrefferedColor == Piece.Color.WHITE? Piece.Color.BLACK:Piece.Color.WHITE;
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

        }else if(message.contains("START")){
            if(displayGameObserver!=null)displayGameObserver.update(null,null);

        }else if (message.startsWith("SETPCOLOR")){
            message = message.replace("SETPCOLOR","");
            if(message == "WHITE"){
                opponentPrefferedColor = Piece.Color.WHITE;
            }else if(message == "BLACK"){
                opponentPrefferedColor = Piece.Color.BLACK;
            }else if(message == "NONE"){
                opponentPrefferedColor = null;
            }

        }else if (message.startsWith("SETCOLOR")){
            if(message.contains("WHITE")){
                playerColor = Piece.Color.WHITE;
            }else if(message.contains("BLACK")){
                playerColor = Piece.Color.BLACK;
            }
        }else if(message.startsWith("SURRENDER")){
            //TODO Surrender
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

    public void move(Piece piece,int toPosition,boolean sendToAnotherPlayer){
        if(currentMove==piece.getPieceColor()) {
            if(sendToAnotherPlayer && mode!=Mode.LAN){
                if(currentMove!=playerColor) return;
            }
            if(!sendToAnotherPlayer && mode!=Mode.LAN){
                if(currentMove==playerColor) return;
            }

            int from = piece.getPosition();
            boolean moveWasMade = getBoard().makeMove(piece, toPosition);
            if (moveWasMade){
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
            }
        }
    }

    public void setDisplayGameObserver(Observer displayGameObserver) {
        this.displayGameObserver = displayGameObserver;
    }

    public void setUpdateBoardObserver(Observer updateBoardObserver){
        this.updateBoardObserver = updateBoardObserver;
    }

    public Board getBoard(){
        return this.board;
    }

    public void setPreferredColor(Piece.Color preferredColor) {
        this.preferredColor = preferredColor;
    }
}
