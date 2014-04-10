package com.zoobrew.rpi;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

/* Main Class of the Chat Server */
public class Server {

    public static int portNumber;
    public static Hashtable<String, ChatConnection> mConnections =
            new Hashtable<String, ChatConnection>(32);

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: ChatServer <port>");
            System.exit(1);
        }
        System.out.println("Started server on port " + args[0]);
        String portNumber = args[0];
        int port = Integer.parseInt(portNumber);
        Socket clientSocket;
        ChatConnection clientConnection;
        try (
                ServerSocket chatServerSocket = new ServerSocket(port);
            ){
                /* block until a client is connnected */
                while (true) {
                    clientSocket = chatServerSocket.accept();
                    clientConnection = new ChatConnection(clientSocket);
                    new Thread(clientConnection).start();
                }

        } catch (IOException exception) {
            System.out.println("IOException caught while listening on port " + portNumber);
            System.out.println(exception.getMessage());
        }
    }

}
