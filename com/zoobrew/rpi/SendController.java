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
			String[] targets = ChatUtils.SplitIntoWords(remaining.toLowerCase());
			handleSending(targets);
		} else {
			mPrinter.println("ERROR: cannot send message as another user");
		}
	}
	
	public void sendBroadcast(){
		String input;
		String numberLine;
		try {
			input = mInput.readLine();
			if (input != null){
				numberLine = input.trim();
				if(numberLine.startsWith("c")){
					//handle chunked message
				} else
				sendAll(numberLine);
			}
		} catch (IOException e) {
			System.out.println("IOException caught while writing to client");
            System.out.println(e.getMessage());
		}
	}
			
	public void sendAll(String line){
		int messageSize;
		String input;
		String nextLine;
		try {
			line = line.trim();
			if(line.startsWith("c")){
				line = line.substring(1);
			}
			messageSize = Integer.parseInt(line);
			while (((input = mInput.readLine()) != null) && (messageSize > 0)) {
            	nextLine = input.trim();
            	if (nextLine.length() <= messageSize){
            		messageSize -= nextLine.length();
            		ChatUtils.sendMessageToAll(mUser, nextLine);
            	} else {
            		ChatUtils.sendMessageToAll(mUser, nextLine.substring(0, messageSize));
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
	
	public void sendMessage(String[] targets, String line){
		int messageSize;
		String input;
		String nextLine;
		try {
			messageSize = Integer.parseInt(line);
			while (((input = mInput.readLine()) != null) && (messageSize > 0)) {
            	nextLine = input.trim();
            	if (nextLine.length() <= messageSize){
            		messageSize -= nextLine.length();
            		ChatUtils.sendMessageToUsers(mUser, targets, nextLine);
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
	
	private void handleSending(String[] targets){
		String input;
		String nextLine;
		try {
			input = mInput.readLine();
			if (input != null){
				nextLine = input.trim();
				if(nextLine.startsWith("c")){
					sendMessage(targets, nextLine.substring(1));
				} else {
				sendMessage(targets, nextLine);
				}
			}
		} catch (IOException e) {
			System.out.println("IOException caught while writing to client");
            System.out.println(e.getMessage());
		}
	}

}
