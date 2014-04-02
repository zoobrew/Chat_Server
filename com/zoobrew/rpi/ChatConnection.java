package com.zoobrew.rpi;

/* A single connection by a user to the server */
public class ChatConnection {
	
	public enum ConnectionType{
		TCP,UDP;
	};
	/* Case-insensitive, must be downcased */
	public String userName;
	public int ip;
	

}
