package com.tint.entityengine.server.entity.components;

import com.badlogic.ashley.core.Component;
import com.tint.entityengine.network.packets.InputPacket;

public class ServerPlayerComponent extends Component implements Networked {
	
	public static final byte KEY_UP = 1, KEY_DOWN = 2, KEY_LEFT = 4, KEY_RIGHT = 8;
	public byte inputState;

	public boolean getKey(byte key) {
		//AND operator to check one bit
		return (inputState & key) == key;
	}

	public void setInput(InputPacket packet) {
		inputState = packet.keyBits;
	}

	public float getSpeed() {
		return 5;
	}

	@Override
	public void resetChanged() {
		
	}
	
	@Override
	public boolean hasChanged() {
		return false;
	}
	
}
