package com.zoobrew.rpi;

public interface ChatConnection extends Runnable{

	public void writeToClient(String message);
	
	 public String getUsername();
}
