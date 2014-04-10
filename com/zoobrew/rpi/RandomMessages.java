package com.zoobrew.rpi;

import java.util.LinkedList;
import java.util.Random;

public class RandomMessages {

	
	private static String[] mRandMessages = {
	"Hey, you're kinda hot", "No way!", "I like Justin Bieber....a lot",
	"What?", "OKAY!", "What is your favorite color?", "I am leaving",
	"Who am I?", "vim or emacs?", "Share your location?"
	};
	private LinkedList<String> mLastConnections;
	private Random mRandom;

	public RandomMessages(){
		mLastConnections = new LinkedList<String>();
		mRandom = new Random();
	}
	
	public void addMessage(String sender){
		mLastConnections.add(sender);
		if (mLastConnections.size() > 3){
			mLastConnections.remove();
		}
	}

	public String getRandomConnection(){
		return mLastConnections.get(mRandom.nextInt(mLastConnections.size()));
	}
	
	public String getRandomMessage(){
		return mRandMessages[mRandom.nextInt(10)];
	}
}
