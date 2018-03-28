package common;

import server.ThreadedServer;

import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class Reader extends Thread implements ChatterboxProtocol, Runnable{
    private Socket socket;
    private ThreadedServer threadedServer;
    public abstract String read(String message);

    public Reader(Socket socket){
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }

    public ThreadedServer getThreadedServer() {
        return threadedServer;
    }


    @Override
    public void run() {
        read("");
    }
}
