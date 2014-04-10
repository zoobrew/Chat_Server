package com.zoobrew.rpi;


public class UDPConnection implements ChatConnection{
	
	private String LOGIN = "ME IS ";
    private String SEND_MESSAGE = "SEND ";
    private String SEND_ALL = "BROADCAST";
    private String HERE = "WHO HERE";
    private String EXIT = "LOGOUT";

    private String mUserName;
    private Boolean mLoggedIn;
    private UDPVerboseConnection mUDPVerboseConnection;
    private SendController mSendController;
    private Server mServer;
    
    UDPConnection(Server server, UDPVerboseConnection connection) {
    	mServer = server;
        mLoggedIn = false;
        mUDPVerboseConnection = connection;
	}
    
    @Override
    public void run() {
        String inputLine;
        String input;
        while ((inputLine = mUDPVerboseConnection.readIn()) != null) {
        	input = inputLine.trim();
        	parseMessage(input);
        }
    }
    
    public void writeToClient(String message){
    	mUDPVerboseConnection.printOut(message);
    }
    
    public String getUsername(){
    	return mUserName;
    }
    
    private boolean parseMessage(String input){
    	if(input.startsWith(LOGIN)){
    		login(input.substring(LOGIN.length()));
    	} else if (!(mUserName == null)) {
	    	if (input.startsWith(SEND_MESSAGE)){
	    		if(mSendController == null){
	    				mSendController = new SendController(mServer, this, mUDPVerboseConnection);
	    		}
	    		mSendController.sendStartMessage(input.substring(SEND_MESSAGE.length()));
	    	} else if (input.startsWith(SEND_ALL)){
	    		if(mSendController == null){
    				mSendController = new SendController(mServer, this, mUDPVerboseConnection);
	    		}
	    		mSendController.sendBroadcast();
	    	} else if (input.startsWith(HERE)){
	    		userList();
	    	} else if (input.startsWith(EXIT)){
	    		logout();
	    	}
    	} else {
    		return false;
    	}
    	return true;
    }
    
    private void userList(){
    	for (String user : mServer.mConnections.keySet()) {
    		mUDPVerboseConnection.printOut(user);
    	}
    }
    
    private void login(String message){
    	if( mLoggedIn){
    		mUDPVerboseConnection.printOut("Already logged in, log out to log in as another user");
    	}
    	else {
    		String username = ChatUtils.getFirstWord(message).toLowerCase();
			if (mServer.mConnections.containsKey(username)){
				mUDPVerboseConnection.printOut("Username is already in use");
			} else {
				mUserName = username;
				mServer.mConnections.put(mUserName, this);
				mLoggedIn = true;
				mUDPVerboseConnection.printOut("USERNAME IS: " + mUserName);
			}
    	}
    }
    
    private void logout(){
    	if (mLoggedIn){
    		mServer.mConnections.remove(mUserName);
    		mUDPVerboseConnection.printOut("User " + mUserName + " has logged out");
    		mUserName = null;
    		mLoggedIn = false;
    	} else{
    		mUDPVerboseConnection.printOut("ERROR: Not logged is as any user");
    	}
    }

}
