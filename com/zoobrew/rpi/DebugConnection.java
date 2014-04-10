package com.zoobrew.rpi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class DebugConnection implements VerboseIO{
    private PrintWriter mOut;
    private BufferedReader mIn;
    private String mIp;
    private Server mServer;
    
    public DebugConnection(Server server, BufferedReader in, PrintWriter out, String ip){
    	mServer = server;
    	mIn = in;
    	mOut = out;
    	mIp = ip;
    }
    
    public String readIn(){	
    	try {
			String line = mIn.readLine();
			verbosePrint("RCVD from " + mIp + " " + line);
			return line;
		} catch (IOException e) {
			System.out.println("IOException caught while reading from client");
            System.out.println(e.getMessage());
		}
		return "";
    }
    
    public void printOut(String message){
    	verbosePrint("SENT to " + mIp + " " + message);
    	mOut.println(message);
    }
	public void verbosePrint(String message){
		if (mServer.DEBUGMODE){
			System.out.println(message);
		}
	}

}
