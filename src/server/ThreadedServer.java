/**
 *
 */
package server;

import client.ClientHandler;

import java.io.*;
import java.net.ServerSocket;
import java.nio.BufferOverflowException;

import java.util.concurrent.ConcurrentLinkedQueue;
/**
 * The main program for the server simulation.
 *
 * @author William Johnson
 */
public class ThreadedServer {
    public static int portNumber = 50000;
    public static ThreadedServer t;

    public static ConcurrentLinkedQueue<ClientHandler> clients = new ConcurrentLinkedQueue<>();;
    private ServerSocket serverSocket;
    public ThreadedServer(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
    }

    /**
     * Getter for the clients on the server
     * @return the clients on the server
     */
    public static ConcurrentLinkedQueue<ClientHandler> getClients() {
        return clients;
    }

    /**
     * Main function for the Server
     * @param args not utilized
     */
    public static void main(String[] args){
        try {
            ThreadedServer threadedServer = new ThreadedServer(new ServerSocket(portNumber));
            ThreadedServer.t = threadedServer;
            System.out.println("Server is waiting for clients");
            while (true) {
                ClientHandler c = new ClientHandler(threadedServer.serverSocket.accept(),threadedServer);
                t.clients.add(c);
                for (ClientHandler o: t.getClients()) {
                    o.setT(t);
                }
                c.start();
                System.out.println("Server is waiting for clients");
                //threadedServer.clients.add(c.getClientName());
                //String line = t.scanner.nextLine();
                //System.out.println(line);
                //while (t.scanner.hasNextLine()) {
                //    t.read();
                //}
            }

        } catch (BufferOverflowException o){

        } catch (IOException e) {
            e.printStackTrace();
        }

        /**
        Socket socket = serverSocket.accept();
        PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        System.out.println("Client connected: " + socket);
        String message = in.readLine();
        System.out.println("Server recieved: " + message);
        out.println("Hello from server land");
        */
         //client.shutdownInput();
        //client.shutdownOutput();
        //server.close();
    }
}
