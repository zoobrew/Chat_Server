package com.zoobrew.rpi;

import java.io.PrintWriter;

public class SendController {

	private String mUser;
	private ChatConnection mConnection;
	private PrintWriter mPrinter;
	
	public SendController(ChatConnection connection, PrintWriter printer){
		mConnection = connection;
		mUser = mConnection.getUsername();
		mPrinter = printer;
	}

	public void sendMessage(String input){
    	String sender = ChatUtils.getFirstWord(input).toLowerCase();
    	mPrinter.println("sender is: " + sender + "!");
    	String remaining = input.substring(sender.length()+1);
		if (sender.equalsIgnoreCase(mUser)){
			String target = ChatUtils.getFirstWord(remaining).toLowerCase();
			mPrinter.println("Receipiant is: " + target + "!");
			remaining = remaining.substring(target.length()+1);
			mConnection.setMode(new SendingMode(mUser, target));
			sendMessageToUser(target, remaining);
		} else {
			mPrinter.println("ERROR: cannot send message as another user");
		}
    }

	public void sendMessageToUser(String target, String message){
    	ChatConnection recp = Server.mConnections.get(target);
    	recp.writeToClient("FROM " + mUser + '\n' + message);
    }
}
