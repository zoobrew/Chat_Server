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
    private String SEND_ALL = "BROADCAST";
    private String HERE = "WHO HERE";
    private String EXIT = "LOGOUT";

	public enum ConnectionType{
        TCP,UDP;
    };

    private String mUserName;
    private Boolean mLoggedIn;
    private Socket mClientSocket;
    PrintWriter mPrinter;
    private BufferedReader bufferIn;
    private SendController mSendController;
    

    ChatConnection(Socket socket){
        mClientSocket = socket;
        mLoggedIn = false;
    }

    @Override
    public void run() {
        try {
        mPrinter = new PrintWriter(mClientSocket.getOutputStream(), true);
        bufferIn = new BufferedReader(new InputStreamReader(mClientSocket.getInputStream()));
        System.out.println("Accepted client socket from " + mClientSocket.getRemoteSocketAddress());

        String inputLine;
        String input;
            while ((inputLine = bufferIn.readLine()) != null) {
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
	    				mSendController = new SendController(this, mPrinter, bufferIn);
	    		}
	    		mSendController.sendStartMessage(input.substring(SEND_MESSAGE.length()));
	    	} else if (input.startsWith(SEND_ALL)){
	    		if(mSendController == null){
    				mSendController = new SendController(this, mPrinter, bufferIn);
	    		}
	    		mSendController.sendBroadcast();
	    	} else if (input.startsWith(HERE)){
	    		userList();
	    	} else if (input.startsWith(EXIT)){
	    		logout();
	    	}
    	} else {
    		return false;
    	}
    	return true;
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
