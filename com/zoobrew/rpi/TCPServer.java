package com.zoobrew.rpi;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPServer implements Runnable{
	
	private Server mServer;

	public TCPServer(Server server) {
		mServer = server;
	}
	
	public void run(){
    	
        System.out.println("Started server on port " + mServer.mPortNumber);
        Socket clientSocket;
        TCPConnection clientConnection;
        try (
                ServerSocket chatServerSocket = new ServerSocket(mServer.mPortNumber);
            ){
                /* block until a client is connnected */
                while (true) {
                    clientSocket = chatServerSocket.accept();
                    clientConnection = new TCPConnection(mServer, clientSocket);
                    new Thread(clientConnection).start();
                }

        } catch (IOException exception) {
            System.out.println("IOException caught while listening on port " + mServer.mPortNumber);
            System.out.println(exception.getMessage());
        }
    }

}
