package server;

import client.ClientHandler;
import common.ChatterboxProtocol;
import common.Reader;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class ThreadedServer extends Reader {
    private HashMap<String,Integer> clients;


    public ThreadedServer(Socket socket) {
        super(socket);
    }

    public static void main(String[] args) throws IOException{
        ServerSocket server = new ServerSocket(5678);
        while (true){
            Socket client = server.accept();
            ClientHandler handler = new ClientHandler(client,server);

        }
    }

    @Override
    public void read(String message) {

    }
}
