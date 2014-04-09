package com.zoobrew.rpi;

public class SendAllMode implements ChatMode {

	private String mSender;
	
	public String getSender() {
		return mSender;
	}
	public void setSender(String sender) {
		mSender = sender;
	}

	@Override
	public ConnectionMode getMode() {
		return ChatMode.ConnectionMode.SEND_ALL;
	}
}
