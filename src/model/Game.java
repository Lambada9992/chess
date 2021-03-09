package model;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Game {
    private Board board = null;
    private Thread serverSocketThread = null;
    private ServerSocket serverSocket = null;
    private final int port = 1234;

    public Game() {
        this.board = new Board();
        board.putPiecesOnBoard();
    }

    public void startServer(){
        if (serverSocket!=null || serverSocketThread != null){
            stopServer();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    serverSocket = new ServerSocket(port);
                    while (!serverSocketThread.isInterrupted()){
                        System.out.print("Start");
                        Socket client = serverSocket.accept();
                        //TODO pass client to handler
                    }
                } catch (IOException e) {
                    //e.printStackTrace();
                } finally{
                    System.out.printf("ServerClosed");
                    return;
                }
            }
        }).start();
    }

    public void stopServer(){
        if(serverSocketThread!=null){
            serverSocketThread.interrupt();
            serverSocketThread = null;
        }
        if (serverSocket!=null){
            try {
                serverSocket.close();
            } catch (IOException e) {
                //e.printStackTrace();
            }
            serverSocket = null;
        }
    }

    public Board getBoard(){
        return this.board;
    }

}
