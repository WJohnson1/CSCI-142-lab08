package client;

import common.ChatterboxProtocol;
import common.Reader;
import server.ThreadedServer;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;
/**
 * The main program for the client handler.
 *
 * It is run on the command line, once the server main method has begun
 *
 * @author William Johnson
 */
public class ClientHandler extends Reader implements Runnable{
    private PrintWriter out;
    private Scanner scanner;
    private ThreadedServer t;
    private boolean isActive;
    private boolean connected;
    private String name;

    /**
     * Constructor for the Client Handler
     * @param socket the socket that the client belongs to
     * @param t the server that the client connects to
     * @throws IOException
     */
    public ClientHandler(Socket socket, ThreadedServer t) throws IOException{
        super(socket);
        this.out = new PrintWriter(socket.getOutputStream(),true);
        this.isActive = true;
        this.connected = false;
        this.scanner = new Scanner(socket.getInputStream());
        this.t = t;
    }

    /**
     * Converts the message from the server into user output
     * @param message Message from the server
     */
    public void write(String message){
        String[] command = message.split(ChatterboxProtocol.SEPARATOR);
        if (command[0].equals(ChatterboxProtocol.CONNECT)){
            out.println(">>"+command[1]+": "+ChatterboxProtocol.CONNECTED);
        }
        else if (command[0].equals(ChatterboxProtocol.SEND_CHAT)) {
            if (this.connected) {
                for (ClientHandler c: this.getT().getClients()){
                    c.out.println(this.name + " said" + command[2]);
                }
                out.println(ChatterboxProtocol.CHAT_RECEIVED + "::" + this.name+"::"+command[2]);
            }
            else {
                out.println(ChatterboxProtocol.FATAL_ERROR +"::initial connect message not received");
            }
        }
        else if (command[0].equals(ChatterboxProtocol.SEND_WHISPER )){
            if (this.connected){

            }
            else {
                out.println(ChatterboxProtocol.FATAL_ERROR +"::initial connect message not received");
            }
        }
        else if (command[0].equals(ChatterboxProtocol.LIST_USERS)){
            if (this.connected) {
                for (ClientHandler c : ThreadedServer.clients) {
                    this.out.println(c.name);
                }
                this.out.println();
            }
            else {
                out.println(ChatterboxProtocol.FATAL_ERROR +"::initial connect message not received");
            }
        }
        else if (command[0].equals(ChatterboxProtocol.DISCONNECT)){
            if (this.connected) {
                this.t.getClients().remove(this.name);
                out.println(">>"+name +": "+ChatterboxProtocol.DISCONNECTED);
                this.isActive = false;

            }
            else {
                out.println(ChatterboxProtocol.FATAL_ERROR +"::initial connect message not received");
            }
        }
        else{
            out.println(ChatterboxProtocol.ERROR +"::Unknown or unexpected protocol message: " + message);
            out.println();
        }
    }

    /**
     * Converts the user input into a format that the server understands
     * @param message the user input
     * @return the user input in a format that the server understands
     */
    public String read(String message) {
        String[] command = message.split(" ");
        if (command[0].equals("/help")) {
            if (this.connected) {
                System.out.print("/help - displays this message\n");
                System.out.print("/quit - quit Chatterbox\n");
                System.out.print("/c <message> - send a message to all currently connected users\n");
                System.out.print("/w <recipient> <message> - send a private message to the recipient\n");
                System.out.print("/list - display a list of currently connected users");
                this.out.println(this.name + "wanted help");

                return "";
            }
            else {
                this.out.println(ChatterboxProtocol.FATAL_ERROR +"::initial connect message not received");
                return (ChatterboxProtocol.FATAL_ERROR +"::initial connect message not received");
            }
        }
        else if (command[0].equals("/c")){
            if (this.connected){
                String s = ChatterboxProtocol.SEND_CHAT + "::"+this.name+"::";
                for(int i = 1;i<command.length;i++){
                    s += (command[i] + " ");
                }
                return s;
            }
            else {
                this.out.println(ChatterboxProtocol.FATAL_ERROR +"::initial connect message not received");
                return (ChatterboxProtocol.FATAL_ERROR +"::initial connect message not received");
            }
        }
        else if (command[0].equals("/w")){
            if (this.connected){
                this.out.println(ChatterboxProtocol.SEND_WHISPER+"::"+command[1]+"::"+command[2]);
                return (ChatterboxProtocol.SEND_WHISPER+"::"+command[1]+"::"+command[2]);
            }
            else {
                this.out.println(ChatterboxProtocol.FATAL_ERROR +"::initial connect message not received");
                return (ChatterboxProtocol.FATAL_ERROR +"::initial connect message not received");
            }        }
        else if (command[0].equals("/list")){
            if (this.connected) {
                System.out.println("The following users are connected");
                this.out.println(ChatterboxProtocol.LIST_USERS);
                return (ChatterboxProtocol.LIST_USERS);
            }
            else {
                this.out.println(ChatterboxProtocol.FATAL_ERROR +"::initial connect message not received");
                return (ChatterboxProtocol.FATAL_ERROR +"::initial connect message not received");
            }
        }

        else if (command[0].equals("/quit")){
            if (this.connected) {
                Scanner prompt = new Scanner(System.in);
                System.out.println("Are you sure (y/n): ");
                String answer = prompt.nextLine();
                answer = answer.toLowerCase();
                if (answer.equals("y")){
                    this.isActive = false;
                    this.out.println(ChatterboxProtocol.DISCONNECT);
                    return (ChatterboxProtocol.DISCONNECT);
                }
                else{
                    return "";
                }
            }
            else {
                this.out.println(ChatterboxProtocol.FATAL_ERROR +"::initial connect message not received");
                return (ChatterboxProtocol.FATAL_ERROR +"::initial connect message not received");
            }
        }
        else{
            this.out.println(ChatterboxProtocol.ERROR +"::Unknown or unexpected protocol message: " + message);
            return (ChatterboxProtocol.ERROR +"::Unknown or unexpected protocol message: " + message);
        }
    }

    /**
     * Connects a client with a name to the server
     * @return the name of tne client
     */
    public String connect(){
        Scanner prompt = new Scanner(System.in);
        System.out.print("Username: ");
        String name = prompt.nextLine();
        this.name = name;
        this.out.print("<<unknown user: ");
        this.out.print(ChatterboxProtocol.CONNECT+"::"+this.name);
        return (ChatterboxProtocol.CONNECT+"::"+this.name);

    }

    /**
     * Sets the value of the Threaded Server
     * @param t the new value for the Threaded Server
     */
    public void setT(ThreadedServer t) {
        this.t = t;
    }

    /**
     * Runs the simulation of a client. It will continuely
     * check for server output and send the server
     * output to a user-friendly message
     */
    public void run() {
        System.out.println("ChatterboxClient connection received from " + this.getSocket().getInetAddress());
        String message = null;
        while (this.isActive){
            if (scanner.hasNextLine()) {
                message = scanner.nextLine();
                this.connected = true;
                this.write(message);
                System.out.println(message);
            }
        }
        try {
            close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the ThreadedServer
     * @return the ThreadedServer
     */
    public ThreadedServer getT() {
        return t;
    }

    /**
     * Closes the socket for the user
     * @throws IOException
     */
    public void close() throws IOException {
        super.getSocket().shutdownInput();
        super.getSocket().shutdownOutput();
    }

    /**
     * The main function for the clientHandler
     * @param args - not utilized
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", ThreadedServer.portNumber);
        Scanner prompt = new Scanner(System.in);
        ClientHandler c = new ClientHandler(socket,ThreadedServer.t);
        if (!c.connected) {
            String connectMessage = c.connect();
            c.write(connectMessage);
            c.connected = true;
        }
        while (c.isActive){
            c.connected = true;
            System.out.println("Enter a command");
            String text = prompt.nextLine();
            //What the server gets
            //printer.println(text);
            String response = c.read(text);
            //What client gets
            System.out.println(response);
            c.write(response);
            //System.out.println(response);

        }
        socket.shutdownOutput();
        socket.shutdownInput();
    }

}
