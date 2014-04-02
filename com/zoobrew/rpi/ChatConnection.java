package com.zoobrew.rpi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/* A single connection by a user to the server */
public class ChatConnection implements Runnable{

    public enum ConnectionType{
        TCP,UDP;
    };
    /* Case-insensitive, must be downcased */
    public String userName;
    public int ip;
    Socket clientSocket;

    ChatConnection(Socket socket){
        clientSocket = socket;
    }

    @Override
    public void run() {
        try {
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        System.out.println("Accepted client socket");

        String inputLine;
            while ((inputLine = in.readLine()) != null) {
                out.println(inputLine);
            }
        } catch (IOException e) {
            System.out.println("IOException caught while writing to client");
            System.out.println(e.getMessage());
        }

    }
}
