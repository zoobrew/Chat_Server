package com.zoobrew.rpi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/* A single connection by a user to the server */
public class ChatConnection implements Runnable{

    private String LOGIN = "ME IS ";
    private String SEND_MESSAGE = "SEND ";
    private String SEND_ALL = "BROADCAST ";
    private String HERE = "WHO HERE";
    private String EXIT = "LOGOUT";

	public enum ConnectionType{
        TCP,UDP;
    };
    /* Case-insensitive, must be downcased */
    public String userName;
    public Socket clientSocket;

    ChatConnection(Socket socket){
        clientSocket = socket;
    }

    @Override
    public void run() {
        try {
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        System.out.println("Accepted client socket from " + clientSocket.getRemoteSocketAddress());

        String inputLine;
        String input;
            while ((inputLine = in.readLine()) != null) {
            	input = inputLine.trim();
            	parseMessage(inputLine, out);
            }
        } catch (IOException e) {
            System.out.println("IOException caught while writing to client");
            System.out.println(e.getMessage());
        }

    }
    
    private void parseMessage(String input, PrintWriter write){
    	if(input.startsWith(LOGIN)){
    		System.out.println("Logging in");
    		String message = input.substring(LOGIN.length());
    		String username;
    		if (message.indexOf('\n') > -1){
    			userName = message.substring(0, message.indexOf(' '));
    		}else if (!(message.indexOf(' ') < 0)){
    			userName = message.substring(0, message.indexOf(' '));
    		} else {
    			userName = message;
    		}
    		write.println("Username is: " + userName);
    			
    		//String username = message.substring(0, message.indexOf(' ')
    	} else if (input.startsWith(SEND_MESSAGE)){
    		
    	} else if (input.startsWith(SEND_ALL)){
    		
    	} else if (input.startsWith(HERE)){
    		
    	} else if (input.startsWith(EXIT)){
    		
    	} else {
            write.println(input);
    	}
    }
}
