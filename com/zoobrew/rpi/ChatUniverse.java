package com.zoobrew.rpi;


public class ChatUniverse {
	
	private static boolean DEBUGMODE = false;
	
	public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: ChatServer <port>");
            System.exit(1);
        } else {
        	int port;
        	for (String arg: args){
        		if (arg.equals("-v")){
        			DEBUGMODE = true;
        		} else {
        			port = Integer.parseInt(arg);
        			Server server = new Server(DEBUGMODE, port);
        			new Thread(server).start();
        			
        		}
        	}
        }
	}
}
