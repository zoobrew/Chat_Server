package com.zoobrew.rpi;

public class ChatUtils {
	
	public static String getFirstWord(String sentance){
    	if (!(sentance.indexOf("\r\n") < 0)){
			return sentance.substring(0, sentance.indexOf("\r\n"));
		} else if  (!(sentance.indexOf('\n') <0 )){
			return sentance.substring(0, sentance.indexOf('\n'));
		} else if (!(sentance.indexOf(' ') < 0)){
			return sentance.substring(0, sentance.indexOf(' '));
		} else {
			return sentance;
		}
    }
	
	public static void sendMessageToUser(String sender, String target, String message){
    	ChatConnection recp = Server.mConnections.get(target);
    	recp.writeToClient("FROM " + sender + ": " + message);
    	if (!(sender.equalsIgnoreCase("SYSTEM"))){
    		Server.addMessage(sender);
    	}
    }
	
	public static void sendMessageToAll(String sender, String message){
		for (String user : Server.mConnections.keySet()) {
    		if (!(user.equalsIgnoreCase(sender))){
    			ChatUtils.sendMessageToUser(sender, user, message);
    		}
    	}
	}

}
