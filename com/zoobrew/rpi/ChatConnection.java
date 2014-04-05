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
    private String mUserName;
    private Boolean mLoggedIn;
    private Socket mClientSocket;
    PrintWriter mPrinter;
    private Hashtable<String, ChatConnection> mConnectionTable;
    

    ChatConnection(Socket socket, Hashtable<String, ChatConnection> connections){
        mClientSocket = socket;
        mConnectionTable = connections;
        mLoggedIn = false;
    }

    @Override
    public void run() {
        try {
        mPrinter = new PrintWriter(mClientSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(mClientSocket.getInputStream()));
        System.out.println("Accepted client socket from " + mClientSocket.getRemoteSocketAddress());

        String inputLine;
        String input;
            while ((inputLine = in.readLine()) != null) {
            	input = inputLine.trim();
            	parseMessage(inputLine);
            }
        } catch (IOException e) {
            System.out.println("IOException caught while writing to client");
            System.out.println(e.getMessage());
        }

    }
    
    public void writeToClient(String message){
    	mPrinter.println(message);
    }
    
    private void parseMessage(String input){
    	if(input.startsWith(LOGIN)){
    		login(input.substring(LOGIN.length()));
    	} else if (input.startsWith(SEND_MESSAGE)){
    		//sendMessage(input);
    	} else if (input.startsWith(SEND_ALL)){
    		//sendAll(input)
    	} else if (input.startsWith(HERE)){
    		userList();
    	} else if (input.startsWith(EXIT)){
    		logout();
    	}
    }
    private void prepareMessage(String input){
    	if (!(input.indexOf(' ') < 0)){
    		String sender = input.substring(0, input.indexOf(' '));
    		if (sender.equalsIgnoreCase(mUserName)){
    			String remaining = input.substring(sender.length());
    			if (!(remaining.indexOf(' ') < 0)){
    				String target = remaining.substring(0, remaining.indexOf(' ')).toLowerCase();
    				ChatConnection recp = mConnectionTable.get(target);
    				if (recp != null){
    					String message = remaining.substring(target.length());
    					recp.writeToClient(message);
    				}
    			}
    		} else
    			mPrinter.println("ERROR: cannot send message as another user");
    	}
    }
    
    
    private void sendMessage(String target, String input){
    	
    }
    
    private void userList(){
    	for (String user : mConnectionTable.keySet()) {
    		mPrinter.println(user);
    	}
    }
    
    private void login(String message){
    	if( mLoggedIn){
    		mPrinter.println("Already logged in, log out to log in as another user");
    	}
    	else {
	    	if (message.indexOf('\n') > -1){
				mUserName = message.substring(0, message.indexOf(' ')).toLowerCase();
			}else if (!(message.indexOf(' ') < 0)){
				mUserName = message.substring(0, message.indexOf(' ')).toLowerCase();
			} else {
				mUserName = message.toLowerCase();
			}
			if (mConnectionTable.containsKey(mUserName)){
				mPrinter.println("Username is already in use");
			} else {
				mConnectionTable.put(mUserName, this);
				mLoggedIn = true;
				mPrinter.println("Username is: " + mUserName);
			}
    	}
    }
    
    private void logout(){
    	if (mLoggedIn){
    		mConnectionTable.remove(mUserName);
    		mLoggedIn = false;
    		mPrinter.println("User " + mUserName + " has logged out");
    	} else{
    		mPrinter.println("ERROR: Not logged is as any user");
    	}
    }
    
    private String getFirstWord(String sentance){
    	if (!(sentance.indexOf(' ') < 0)){
			return sentance.substring(0, sentance.indexOf(' ')).toLowerCase();
    	} else if (sentance.indexOf("\r\n") > -1){
			return sentance.substring(0, sentance.indexOf("\r\n")).toLowerCase();
		} else if  (sentance.indexOf('\n') > -1){
			return sentance.substring(0, sentance.indexOf('\n')).toLowerCase();
		} else {
			return sentance;
		}
    }
}
