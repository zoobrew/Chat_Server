package com.zoobrew.rpi;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPVerboseConnection implements VerboseIO{
	
    private DatagramSocket mSock;
	private DatagramPacket mOut;
    private DatagramPacket mIn;
    private InetAddress mIp;
    private int mPort;
    private Server mServer;
    private byte[] receiveData = new byte[1024];
	private byte[] sendData = new byte[1024];
    
    public UDPVerboseConnection(Server server, DatagramSocket sock, int port){
    	mServer = server;
    	mSock = sock;
    	mIn = new DatagramPacket(receiveData, receiveData.length);
    	mPort = port;
    }
    
    public String readIn(){	
    	try {
			mSock.receive(mIn);
			mIp = mIn.getAddress();
			String line = new String(mIn.getData());
			verbosePrint("RCVD from " + mIp + " " + line);
			return line;
		} catch (IOException e) {
			System.out.println("IOException caught while reading from client " + mIp);
            System.out.println(e.getMessage());
		}
		return "";
    }
    
    public void printOut(String message){
    	verbosePrint("SENT to " + mIp + " " + message);
    	int sent=0;
    	while (sent < message.length()){
    		if (message.length() < sendData.length){
    			sendData = message.getBytes();
    			sent = sendData.length;
    		} else {
    			sendData = message.substring(0, sendData.length).getBytes();
    			message = message.substring(sendData.length);
    			sent = sendData.length;
    		}
    		System.out.println("" + mIp +":"+ mPort);
    		DatagramPacket sendPacket = new DatagramPacket(sendData,
                    sendData.length, mIp, mPort);
    		System.out.println(sendData.toString());
            try {
				mSock.send(sendPacket);
			} catch (IOException e) {
				System.out.println("IOException caught while reading from client");
	            System.out.println(e.getMessage());
			}
    	}
    }
	public void verbosePrint(String message){
		if (mServer.DEBUGMODE){
			System.out.println(message);
		}
	}
}
