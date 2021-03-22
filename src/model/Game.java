package model;

import java.io.IOException;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Observer;
import java.util.Random;
import java.util.regex.Pattern;

/**
 *  Class that represents the whole model of the game
 */
public class Game {
    /**
     * Mode of the game (NONE,LAN,HOST,JOIN)
     */
    public enum Mode{NONE,LAN,HOST,JOIN}

    /**
     * State of the game (NOTSTARTED, RUNNING, ENDED)
     */
    public enum State{NOTSTARTED,RUNNING,ENDED}

    private State state = State.NOTSTARTED;
    private Mode mode;

    private Board board = new Board();
    private Piece.Color currentMove = Piece.Color.WHITE;
    private Piece.Color playerColor = null;
    private Piece.Color preferredColor = null;
    private Piece.Color opponentPreferredColor = null;
    private Piece.Color winnerColor = null;

    private ServerSocket serverSocket = null;
    private Socket socket = null;
    private final int port = 1234;
    private ConnectionHandler connectionHandler = null;
    private boolean isConnecting = false;
    private boolean isServerOn = false;

    private Observer displayGameObserver = null;
    private Observer updateBoardObserver = null;
    private Observer connectionObserver = null;

    private static final String IPV4_REGEX =
            "^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                    "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                    "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                    "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
    private static final Pattern IPv4_PATTERN = Pattern.compile(IPV4_REGEX);

    /**
     * Constructor
     */
    public Game() {
        this.mode = Mode.NONE;
    }

    /**
     * The method sets the mode of the app
     * @param mode mode to be set
     */
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

    /**
     * @return Board of the game
     */
    public Board getBoard(){
        return this.board;
    }

    /**
     * A method that makes a move in the game
     * @param piece piece to be moved
     * @param toPosition position where the chosen piece should be moved
     * @param sendToAnotherPlayer Information if the move should be sent to another player(if it's available)
     */
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

    /**
     * Tries to make a connection to another player
     * @param ip Ip address of another player
     * @throws Exception if the passed IP was invalid
     */
    public void connect(String ip) throws Exception {

        if(!IPv4_PATTERN.matcher(ip).matches()) throw new Exception("Invalid IP");

        if (socket!= null){
            disconnect();
        }
        isConnecting = true;
        if(connectionObserver!=null)connectionObserver.update(null,null);
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

                } catch (ConnectException e){
                    isConnecting = false;
                    if(connectionObserver!=null)connectionObserver.update(null,null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Close the connection if there is any
     */
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
        isConnecting = false;
        if(connectionObserver!=null)connectionObserver.update(null,null);
    }

    /**
     * @return If game tires to connect to another player
     */
    public boolean isConnecting() {
        return isConnecting;
    }

    /**
     * Starts the server so it can accept incoming connections.
     */
    public void startServer(){
        if (serverSocket!=null){
            stopServer();
        }
        isServerOn = true;
        if(connectionObserver!=null)connectionObserver.update(null,null);
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

    /**
     * Stops the server.
     */
    public void stopServer(){
        isServerOn = false;
        if(connectionObserver!=null)connectionObserver.update(null,null);
        if (serverSocket!=null){
            try {
                serverSocket.close();
            } catch (IOException e) {
                //e.printStackTrace();
            }
            serverSocket = null;
        }
    }

    /**
     * @return Information if the server is turned ON/OFF
     */
    public boolean getIsServerOn(){
        return isServerOn;
    }

    /**
     * Interpreter of the incoming messages.
     * @param message message to be interpreted
     */
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
            currentMove = Piece.Color.WHITE;
            board.putPiecesOnBoard();
            state = State.RUNNING;
            if(displayGameObserver!=null)displayGameObserver.update(null,null);
            if(updateBoardObserver!=null)updateBoardObserver.update(null,null);
        }
    }

    /**
     * Create message that inform about move.
     * @param from Position on the board
     * @param to Position on the board
     * @return Message
     */
    private String createMoveMessage(int from,int to){
        return "MOV" + Integer.toString(from) +"%" +Integer.toString(to);
    }

    /**
     * @return The message that can inform another player that game has started
     */
    private static String createStartMessage(){
        return "START";
    }

    /**
     * @return The message that can inform another player that you are ready
     */
    private static String createReadyMessage(){
        return "READY";
    }

    /**
     * @param color preferred color
     * @return The message that inform about preferred color
     */
    private String createSetPreferredColorMessage(Piece.Color color){
        String message= "SETPCOLOR";
        if(color == null){
            message+="NONE";
        }else {
            message+=color== Piece.Color.WHITE? "WHITE" : "BLACK";
        }
        return message;
    }

    /**
     * @param color player color
     * @return The message which inform another player about his color
     */
    private String createSetColorMessage(Piece.Color color){
        String message= "SETCOLOR";
        if(color == null){
            throw new NullPointerException("create message - set color param is null");
        }else {
            message+=color== Piece.Color.WHITE? "WHITE" : "BLACK";
        }
        return message;
    }

    /**
     * Surrender
     */
    public void surrender() {
        if (mode == Mode.HOST || mode == Mode.JOIN) {
            if (state == State.RUNNING) {
                state = State.ENDED;
                winnerColor = playerColor == Piece.Color.WHITE ? Piece.Color.BLACK : Piece.Color.WHITE;
                if (connectionHandler != null) connectionHandler.write("SURRENDER");
                if (updateBoardObserver != null) updateBoardObserver.update(null, null);
            }
        }
    }

    /**
     * Restarts the game
     */
    public void restart(){
        if(state == State.ENDED){
            if (connectionHandler != null) connectionHandler.write("RESTART");
            board.putPiecesOnBoard();
            state = State.RUNNING;
            winnerColor = null;
            currentMove = Piece.Color.WHITE;
            if(displayGameObserver!=null)displayGameObserver.update(null,null);
            if(updateBoardObserver!=null)updateBoardObserver.update(null,null);
        }
    }

    /**
     * Sets the observer
     * @param displayGameObserver Observer
     */
    public void setDisplayGameObserver(Observer displayGameObserver) {
        this.displayGameObserver = displayGameObserver;
    }

    /**
     * Sets the observer
     * @param updateBoardObserver Observer
     */
    public void setUpdateBoardObserver(Observer updateBoardObserver){
        this.updateBoardObserver = updateBoardObserver;
    }

    /**
     * Sets the observer
     * @param connectionObserver Observer
     */
    public void setConnectionObserver(Observer connectionObserver) {
        this.connectionObserver = connectionObserver;
    }

    /**
     * Sets the preferred color
     * @param preferredColor Color
     */
    public void setPreferredColor(Piece.Color preferredColor) {
        this.preferredColor = preferredColor;
    }

    /**
     * Sets that winner is not chosen
     */
    public void resetWinner(){
        winnerColor = null;
    }

    /**
     * @return color of player whose move is right now
     */
    public Piece.Color getCurrentMove() {
        return currentMove;
    }

    /**
     * @return Player color
     */
    public Piece.Color getPlayerColor() {
        return playerColor;
    }

    /**
     * @return The state of the game.
     */
    public State getState() {
        return state;
    }

    /**
     * @return The mode of the game.
     */
    public Mode getMode() {
        return mode;
    }

    /**
     * Sets the state of the game
     * @param state State to be set
     */
    public void setState(State state) {
        if(state==null)throw new NullPointerException();
        this.state = state;
    }

    /**
     * @return Who is the winner. Null if no one.
     */
    public Piece.Color getWinnerColor() {
        return winnerColor;
    }

    /**
     * Stuff to be done before closing the application
     * Closing server, connections and waits for it to be done
     */
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
