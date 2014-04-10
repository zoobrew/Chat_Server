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
	
	public static void sendMessageToUser(Server server, String sender, String target, String message){
    	if (server.mConnections.containsKey(target)){
    		ChatConnection recp = server.mConnections.get(target);
    		recp.writeToClient("FROM " + sender + ": " + message);
    	}
    	if (!(sender.equalsIgnoreCase("SYSTEM"))){
    		server.addMessage(sender);
    	}
    }
	
	public static void sendMessageToUsers(Server server, String sender, String[] targets, String message){
		for (String target: targets){
			sendMessageToUser(server, sender, target, message);
		}
	}
	
	public static void sendMessageToAll(Server server, String sender, String message){
		for (String user : server.mConnections.keySet()) {
    		if (!(user.equalsIgnoreCase(sender))){
    			sendMessageToUser(server, sender, user, message);
    		}
    	}
		for (String user : server.mConnections.keySet()) {
    		if (!(user.equalsIgnoreCase(sender))){
    			sendMessageToUser(server, sender, user, message);
    		}
    	}
	}
	
	public static String[] SplitIntoWords(String message){
		return message.split(" ");
	}

}
