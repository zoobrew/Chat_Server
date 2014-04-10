package com.zoobrew.rpi;


public class SendController {

	private String mUser;
	private ChatConnection mConnection;
    private DebugConnection mInOut;
	
	public SendController(ChatConnection connection, DebugConnection inOut){
		mConnection = connection;
		mUser = mConnection.getUsername();
		mInOut = inOut;
	}

	public void sendStartMessage(String input){
		String sender = ChatUtils.getFirstWord(input).toLowerCase();
		mInOut.printOut("sender is: " + sender + "!");
    	String remaining = input.substring(sender.length()+1);
		if (sender.equalsIgnoreCase(mUser)){
			String[] targets = ChatUtils.SplitIntoWords(remaining.toLowerCase());
			handleSending(targets);
		} else {
			mInOut.printOut("ERROR: cannot send message as another user");
		}
	}
	
	public void sendBroadcast(){
		String input;
		String numberLine;
		input = mInOut.readIn();
		if (input != null){
			numberLine = input.trim();
			if(numberLine.startsWith("c")){
				//handle chunked message
			} else
			sendAll(numberLine);
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
			while (((input = mInOut.readIn()) != null) && (messageSize > 0)) {
            	nextLine = input.trim();
            	if (nextLine.length() <= messageSize){
            		messageSize -= nextLine.length();
            		ChatUtils.sendMessageToAll(mUser, nextLine);
            	} else {
            		ChatUtils.sendMessageToAll(mUser, nextLine.substring(0, messageSize));
            		mInOut.printOut("ERROR: message is larger than specified");
            		break;
            	}
            }
		} catch (NumberFormatException numForEx){
			mInOut.printOut("ERROR: SEND must specify size of message on second line");
		}
    }
	
	public void sendMessage(String[] targets, String line){
		int messageSize;
		String input;
		String nextLine;
		try {
			messageSize = Integer.parseInt(line);
			while (((input = mInOut.readIn()) != null) && (messageSize > 0)) {
            	nextLine = input.trim();
            	if (nextLine.length() <= messageSize){
            		messageSize -= nextLine.length();
            		ChatUtils.sendMessageToUsers(mUser, targets, nextLine);
            	} else {
            		mInOut.printOut("ERROR: message is larger than specified");
            		break;
            	}
            }
		} catch (NumberFormatException numForEx){
			mInOut.printOut("ERROR: SEND must specify size of message on second line");
		}
    }
	
	private void handleSending(String[] targets){
		String input;
		String nextLine;
		input = mInOut.readIn();
		if (input != null){
			nextLine = input.trim();
			if(nextLine.startsWith("c")){
				sendMessage(targets, nextLine.substring(1));
			} else {
			sendMessage(targets, nextLine);
			}
		}
	}

}
