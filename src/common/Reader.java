package common;

import java.io.InputStream;
import java.net.Socket;

public abstract class Reader implements ChatterboxProtocol, Runnable{
    private Socket socket;
    public abstract void read(String message);

    public Reader(Socket socket){
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public void run() {
        read("");
    }
}
