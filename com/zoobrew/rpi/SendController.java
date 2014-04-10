package com.zoobrew.rpi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class SendController {

	private String mUser;
	private ChatConnection mConnection;
	private PrintWriter mPrinter;
	private BufferedReader mInput;
	
	public SendController(ChatConnection connection, PrintWriter printer, BufferedReader bufferInput){
		mConnection = connection;
		mUser = mConnection.getUsername();
		mPrinter = printer;
		mInput = bufferInput;
	}

	public void sendStartMessage(String input){
		String sender = ChatUtils.getFirstWord(input).toLowerCase();
    	mPrinter.println("sender is: " + sender + "!");
    	String remaining = input.substring(sender.length()+1);
		if (sender.equalsIgnoreCase(mUser)){
			String target = ChatUtils.getFirstWord(remaining).toLowerCase();
			mPrinter.println("Receipiant is: " + target + "!");
			handleSending(target);
		} else {
			mPrinter.println("ERROR: cannot send message as another user");
		}
	}
	
	
	public void sendMessage(String target, String line){
		int messageSize;
		String input;
		String nextLine;
		try {
			messageSize = Integer.parseInt(line);
			while (((input = mInput.readLine()) != null) && (messageSize > 0)) {
            	nextLine = input.trim();
            	if (nextLine.length() <= messageSize){
            		messageSize -= nextLine.length();
            		ChatUtils.sendMessageToUser(mUser, target, nextLine);
            	} else {
            		mPrinter.println("ERROR: message is larger than specified");
            		break;
            	}
            }
		} catch (NumberFormatException numForEx){
			mPrinter.println("ERROR: SEND must specify size of message on second line");
		} catch (IOException e) {
			System.out.println("IOException caught while writing to client");
            System.out.println(e.getMessage());
		}
    }
	
	private void handleSending(String toUser){
		String input;
		String nextLine;
		try {
			input = mInput.readLine();
			if (input != null){
				nextLine = input.trim();
				if(nextLine.startsWith("c")){
					//handle chunked message
				} else
				sendMessage(toUser, nextLine);
			}
		} catch (IOException e) {
			System.out.println("IOException caught while writing to client");
            System.out.println(e.getMessage());
		}
	}

}
