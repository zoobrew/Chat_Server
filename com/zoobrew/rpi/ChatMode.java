package com.zoobrew.rpi;

public interface ChatMode {
	
	public static enum ConnectionMode{
    	SEND,SEND_ALL,NEITHER;
    }
	
	public ConnectionMode getMode();

}
