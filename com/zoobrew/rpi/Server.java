package com.zoobrew.rpi;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

/* Main Class of the Chat Server */
public class Server {

	private static RandomMessages mRandomMess;
	private static int mMessageCount;
    public static int portNumber;
    public static boolean DEBUGMODE = false;
    public static Hashtable<String, ChatConnection> mConnections =
            new Hashtable<String, ChatConnection>(32);

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: ChatServer <port>");
            System.exit(1);
        }
        if (args.length > 1){
        	for (String arg: args){
        		if (arg.equals("-v")){
        			DEBUGMODE = true;
        		}
        	}
        }
        System.out.println("Started server on port " + args[0]);
        String portNumber = args[0];
        int port = Integer.parseInt(portNumber);
        mRandomMess = new RandomMessages();
        mMessageCount = 0;
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
    
    public static void addMessage(String sender){
    	mRandomMess.addMessage(sender);
    	mMessageCount++;
    	if(mMessageCount > 2){
    		ChatUtils.sendMessageToUser("SYSTEM", mRandomMess.getRandomConnection(), mRandomMess.getRandomMessage());
    		mMessageCount=0;
    	}
    }

}
