package com.zoobrew.rpi;

public interface VerboseIO {

	public String readIn();
	
	public void printOut(String message);
    
	public void verbosePrint(String message);
}
