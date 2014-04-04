package com.zoobrew.rpi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Hashtable;

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
    private String userName;
    private Boolean loggedIn;
    private Socket clientSocket;
    private Hashtable<String, ChatConnection> connectionTable;
    

    ChatConnection(Socket socket, Hashtable<String, ChatConnection> connections){
        clientSocket = socket;
        connectionTable = connections;
        loggedIn = false;
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
    		login(input.substring(LOGIN.length()), write);
    	} else if (input.startsWith(SEND_MESSAGE)){
    		
    	} else if (input.startsWith(SEND_ALL)){
    		
    	} else if (input.startsWith(HERE)){
    		
    	} else if (input.startsWith(EXIT)){
    		logout(write);
    	} else {
            write.println(input);
    	}
    }
    
    private void login(String message, PrintWriter write){
    	if( loggedIn){
    		write.println("Already logged in, log out to log in as another user");
    	}
    	else {
	    	if (message.indexOf('\n') > -1){
				userName = message.substring(0, message.indexOf(' ')).toLowerCase();
			}else if (!(message.indexOf(' ') < 0)){
				userName = message.substring(0, message.indexOf(' ')).toLowerCase();
			} else {
				userName = message.toLowerCase();
			}
			if (connectionTable.containsKey(userName)){
				write.println("Username is already in use");
			} else {
				connectionTable.put(userName, this);
				loggedIn = true;
				write.println("Username is: " + userName);
			}
    	}
    }
    
    private void logout(PrintWriter write){
    	if (loggedIn){
    		connectionTable.remove(userName);
    		loggedIn = false;
    		write.println("User " + userName + " has logged out");
    	} else{
    		write.println("Not logged is as any user");
    	}
    	
    }
}
