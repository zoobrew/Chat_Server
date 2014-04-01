package com.zoobrew.rpi;

/* A single connection by a user to the server */
public class ChatConnection {
	
	public enum ConnectionType{
		TCP,UDP;
	};
	public String name;
	public int ip;
	

}
