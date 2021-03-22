package model;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A Class that is handling connection on a new thread
 */
public class ConnectionHandler extends Thread{
    public enum Status{CONNECTED,DISCONNECTED};

    private Socket socket;
    private Game game;
    private Status status = Status.DISCONNECTED;

    private LinkedBlockingQueue<String> out = new LinkedBlockingQueue();

    private BufferedReader inBuffer;
    private BufferedWriter outBuffer;

    Thread writerThread;
    Thread readerThread;

    private Runnable reader = ()->{
        while (!Thread.currentThread().isInterrupted()){
            try {
                String message = inBuffer.readLine().trim();
                game.interpretMessage(message);
                //System.out.println("Reading: " + message);

            } catch (IOException e) {
                break;
            } catch (NullPointerException e){
                break;
            }
        }
        this.close();
    };
    private Runnable writer = ()->{
        while (!Thread.currentThread().isInterrupted()){
            try {
                String message = out.take();
                //System.out.println("Writing: "+message);
                outBuffer.write(message+"\r\n");
                outBuffer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                break;
            } catch (NullPointerException e){
                break;
            }
        }
        this.close();
    };

    /**
     * Constructor
     * @param socket Socket to be handled
     * @param game Reference to the game
     */
    public ConnectionHandler(Socket socket, Game game) {
        this.socket = socket;
        this.game = game;
        try {
            inBuffer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outBuffer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * A method that is closing the connection handler
     */
    public void close(){
        synchronized (this) {
            this.notify();
        }
    }

    @Override
    public void run() {
        try {
            status = Status.CONNECTED;
            writerThread = new Thread(writer);
            readerThread = new Thread(reader);
            writerThread.start();
            readerThread.start();

            synchronized (this) {
                this.wait();
            }
            writerThread.interrupt();
            readerThread.interrupt();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            status = Status.DISCONNECTED;
            try {
                outBuffer.close();
                socket.close();
                inBuffer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                readerThread.join();
                writerThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Write the message on opened socket
     * @param message Message to be send
     */
    public void write(String message){
        try {
            out.put(message);
        } catch (InterruptedException e) {
            //e.printStackTrace();
        }
    }

    /**
     * @return Status of the handled connection
     */
    public Status getStatus() {
        return status;
    }
}
