package client;

import common.Reader;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler extends Reader{
    private  final Scanner scanner;
    private final PrintStream printer;
    private final ServerSocket serverSocket;

    public ClientHandler(Socket socket, ServerSocket serverSocket) throws IOException{
        super(socket);
        scanner = new Scanner(socket.getInputStream());
        printer = new PrintStream(socket.getOutputStream());
        this.serverSocket = serverSocket;
    }


    @Override
    public void read(String message) {

    }

    public void run() {
        while (scanner.hasNextLine()){
            String line = scanner.nextLine();
            if (line.equals("quit")){
                break;
            }
            else{
                read(line);
            }
        }
        try {
            close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void close() throws IOException {
        super.getSocket().shutdownOutput();
        super.getSocket().shutdownInput();
    }
}
