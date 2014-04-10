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
    private DebugConnection mInOut;
    private SendController mSendController;
    private Server mServer;
    
    

    ChatConnection(Server server, Socket socket){
    	mServer = server;
        mClientSocket = socket;
        mLoggedIn = false;
    }

    @Override
    public void run() {
        try {
        mInOut = new DebugConnection(mServer, new BufferedReader(new InputStreamReader(mClientSocket.getInputStream())),
        		new PrintWriter(mClientSocket.getOutputStream(), true), mClientSocket.getRemoteSocketAddress().toString());

        String inputLine;
        String input;
            while ((inputLine = mInOut.readIn()) != null) {
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
    	mInOut.printOut(message);
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
	    				mSendController = new SendController(mServer, this, mInOut);
	    		}
	    		mSendController.sendStartMessage(input.substring(SEND_MESSAGE.length()));
	    	} else if (input.startsWith(SEND_ALL)){
	    		if(mSendController == null){
    				mSendController = new SendController(mServer, this, mInOut);
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
    	for (String user : mServer.mConnections.keySet()) {
    		mInOut.printOut(user);
    	}
    }
    
    private void login(String message){
    	if( mLoggedIn){
    		mInOut.printOut("Already logged in, log out to log in as another user");
    	}
    	else {
    		String username = ChatUtils.getFirstWord(message).toLowerCase();
			if (mServer.mConnections.containsKey(username)){
				mInOut.printOut("Username is already in use");
			} else {
				mUserName = username;
				mServer.mConnections.put(mUserName, this);
				mLoggedIn = true;
				mInOut.printOut("USERNAME IS: " + mUserName);
			}
    	}
    }
    
    private void logout(){
    	if (mLoggedIn){
    		mServer.mConnections.remove(mUserName);
    		mInOut.printOut("User " + mUserName + " has logged out");
    		mUserName = null;
    		mLoggedIn = false;
    	} else{
    		mInOut.printOut("ERROR: Not logged is as any user");
    	}
    }
}
