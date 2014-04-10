package com.zoobrew.rpi;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

/* Main Class of the Chat Server */
public class Server implements Runnable{

	private RandomMessages mRandomMess;
	private int mMessageCount;
    public int mPortNumber;
    public boolean DEBUGMODE = false;
    public Hashtable<String, ChatConnection> mConnections;

    public Server(boolean DebugMode, int port){
    	mConnections = new Hashtable<String, ChatConnection>(32);
    	DEBUGMODE = DebugMode;
    	mPortNumber = port;
    	mRandomMess = new RandomMessages();
    	mMessageCount = 0;
    }
    public void run(){
    	
        System.out.println("Started server on port " + mPortNumber);
        Socket clientSocket;
        ChatConnection clientConnection;
        try (
                ServerSocket chatServerSocket = new ServerSocket(mPortNumber);
            ){
                /* block until a client is connnected */
                while (true) {
                    clientSocket = chatServerSocket.accept();
                    clientConnection = new ChatConnection(this, clientSocket);
                    new Thread(clientConnection).start();
                }

        } catch (IOException exception) {
            System.out.println("IOException caught while listening on port " + mPortNumber);
            System.out.println(exception.getMessage());
        }
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
