package com.zoobrew.rpi;

public class SendingMode extends SendAllMode{

	private String mRecipent;
	private String mSender;

	public SendingMode(String user, String recipent){
		mSender = user;
		mRecipent = recipent;
	}
	public String getRecipent() {
		return mRecipent;
	}
	public void setRecipent(String recipent) {
		mRecipent = recipent;
	}
	public String getSender() {
		return mSender;
	}
	public void setSender(String sender) {
		mSender = sender;
	}

	@Override
	public ConnectionMode getMode() {
		return ChatMode.ConnectionMode.SEND;
	}
	

}
