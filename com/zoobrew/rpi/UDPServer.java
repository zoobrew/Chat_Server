package com.zoobrew.rpi;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Hashtable;

public class UDPServer implements Runnable{

	private Server mServer;
	private DatagramSocket mUDPSocket;
	private int mPort;
	private Hashtable<InetAddress, DatagramSocket> mUDPTable;

	public UDPServer(Server server) {
		mServer = server;
		mPort = server.mPortNumber;
	}
	
	
	public void run(){
		try {
			mUDPSocket = new DatagramSocket(mPort);
			UDPVerboseConnection udpVerboseConnection = new UDPVerboseConnection(mServer, mUDPSocket, mPort);
			UDPConnection clientConnection = new UDPConnection(mServer, udpVerboseConnection);
			new Thread(clientConnection).start();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		mUDPSocket.close();
	}

}
