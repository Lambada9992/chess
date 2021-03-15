package model;

import com.sun.org.apache.xpath.internal.operations.Mod;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Game {
    public enum State{RUNNING,NOTRUNNING}
    public enum Mode{NONE,LAN,HOST,JOIN}

    private Mode mode = Mode.NONE;

    private Board board = null;
    private Piece.Color currentMove = Piece.Color.WHITE;
    private Piece.Color playerColor = null;

    private ServerSocket serverSocket = null;
    private Socket socket = null;
    private final int port = 1234;
    private ConnectionHandler connectionHandler = null;

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

    public Board getBoard(){
        return this.board;
    }

    public void move(Piece piece,int toPosition){
        if(currentMove==piece.getPieceColor()){
            getBoard().makeMove(piece,toPosition);
            currentMove = currentMove==Piece.Color.WHITE? Piece.Color.BLACK : Piece.Color.WHITE;
        }
    }

}
