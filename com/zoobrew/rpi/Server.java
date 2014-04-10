package com.zoobrew.rpi;

import java.util.Hashtable;

/* Main Class of the Chat Server */
public class Server implements Runnable{

	private RandomMessages mRandomMess;
	private int mMessageCount;
    public int mPortNumber;
    public boolean DEBUGMODE = true;
    public Hashtable<String, ChatConnection> mConnections;
    public TCPServer mTCPServer;
    public UDPServer mUDPServer;

    public Server(boolean DebugMode, int port){
    	mConnections = new Hashtable<String, ChatConnection>(32);
    	DEBUGMODE = DebugMode;
    	mPortNumber = port;
    	mRandomMess = new RandomMessages();
    	mMessageCount = 0;
    }
    public void run(){
    	mTCPServer = new TCPServer(this);
    	mUDPServer = new UDPServer(this);
        System.out.println("Started server on port " + mPortNumber);
        new Thread(mTCPServer).start();
        new Thread(mUDPServer).start();
    }
    
    public void addMessage(String sender){
    	mRandomMess.addMessage(sender);
    	mMessageCount++;
    	if(mMessageCount > 2){
    		ChatUtils.sendMessageToUser(this, "SYSTEM", mRandomMess.getRandomConnection(), mRandomMess.getRandomMessage());
    		mMessageCount=0;
    	}
    }

}
