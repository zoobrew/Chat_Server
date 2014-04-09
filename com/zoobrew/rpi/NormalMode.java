package com.zoobrew.rpi;

public class NormalMode implements ChatMode{

	public ConnectionMode getMode() {
		return ConnectionMode.NEITHER;
	}
}
