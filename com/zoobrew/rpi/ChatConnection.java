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

    private String mUserName;
    private Boolean mLoggedIn;
    private Socket mClientSocket;
    PrintWriter mPrinter;
    private ChatMode mMode;
    private SendController mSendController;
    

    ChatConnection(Socket socket){
        mClientSocket = socket;
        mLoggedIn = false;
        mMode = new NormalMode();
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
            	if (!parseMessage(input)){
            		mClientSocket.close();
            		break;
            	}
            }
        } catch (IOException e) {
            System.out.println("IOException caught while writing to client");
            System.out.println(e.getMessage());
        }

    }
    
    public void writeToClient(String message){
    	mPrinter.println(message);
    }
    
    public String getUsername(){
    	return mUserName;
    }
    
    private boolean parseMessage(String input){
    	if(input.startsWith(LOGIN)){
    		login(input.substring(LOGIN.length()));
    	} else if (!(mUserName == null)) {
	    	if (input.startsWith(SEND_MESSAGE)){
	    		if(mSendController == null){
	    				mSendController = new SendController(this, mPrinter);
	    		}
	    		sendMessage(input.substring(SEND_MESSAGE.length()));
	    	} else if (input.startsWith(SEND_ALL)){
	    		sendAll(input);
	    	} else if (input.startsWith(HERE)){
	    		userList();
	    	} else if (input.startsWith(EXIT)){
	    		logout();
	    	} else if (mMode.getMode().equals(ChatMode.ConnectionMode.SEND)){
	    		ChatUtils.sendMessageToUser(mUserName, ((SendingMode) mMode).getRecipent(), input);
	    	} else if (mMode.getMode().equals(ChatMode.ConnectionMode.SEND_ALL)){
	    		sendAll(input);
	    	}
    	} else {
    		return false;
    	}
    	return true;
    }
    
    public void setMode(ChatMode mode){
    	mMode = mode;
    }
    
    private void sendAll(String input){
    	mMode = new SendAllMode(mUserName);
    	String message = input.substring(SEND_ALL.length());
    	for (String user : Server.mConnections.keySet()) {
    		if (!(user.equalsIgnoreCase(mUserName))){
    			ChatUtils.sendMessageToUser(mUserName, user, message);
    		}
    	}
    	
    }
    
    private void sendMessage(String input){
    	String sender = ChatUtils.getFirstWord(input).toLowerCase();
    	mPrinter.println("sender is: " + sender + "!");
    	String remaining = input.substring(sender.length()+1);
		if (sender.equalsIgnoreCase(mUserName)){
			String target = ChatUtils.getFirstWord(remaining).toLowerCase();
			mPrinter.println("Receipiant is: " + target + "!");
			remaining = remaining.substring(target.length()+1);
			mMode = new SendingMode(mUserName, target);
			ChatUtils.sendMessageToUser(mUserName, target, remaining);
		} else {
			mPrinter.println("ERROR: cannot send message as another user");
		}
    }
    
    private void userList(){
    	for (String user : Server.mConnections.keySet()) {
    		mPrinter.println(user);
    	}
    }
    
    private void login(String message){
    	if( mLoggedIn){
    		mPrinter.println("Already logged in, log out to log in as another user");
    	}
    	else {
    		String username = ChatUtils.getFirstWord(message).toLowerCase();
			if (Server.mConnections.containsKey(username)){
				mPrinter.println("Username is already in use");
			} else {
				mUserName = username;
				Server.mConnections.put(mUserName, this);
				mLoggedIn = true;
				mPrinter.println("Username is: " + mUserName);
			}
    	}
    }
    
    private void logout(){
    	if (mLoggedIn){
    		Server.mConnections.remove(mUserName);
    		mPrinter.println("User " + mUserName + " has logged out");
    		mUserName = null;
    		mLoggedIn = false;
    	} else{
    		mPrinter.println("ERROR: Not logged is as any user");
    	}
    }
}
