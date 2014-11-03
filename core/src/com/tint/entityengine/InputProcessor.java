package com.tint.entityengine;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.tint.entityengine.network.packets.InputPacket;
import static com.tint.entityengine.server.entity.components.ServerPlayerComponent.*;

public class InputProcessor extends InputAdapter {
	
	private int upKey = Keys.W, downKey = Keys.S, leftKey = Keys.A, rightKey = Keys.D;
	private InputPacket inputPacket = new InputPacket();
	private byte inputState;
	private boolean changed = false;

	@Override
	public boolean keyDown(int keycode) {
		//Set bit
		if(keycode == upKey)
			inputState |= KEY_UP;
		
		if(keycode == downKey)
			inputState |= KEY_DOWN;
		
		if(keycode == leftKey)
			inputState |= KEY_LEFT;
		
		if(keycode == rightKey)
			inputState |= KEY_RIGHT;
		
		changed = true;
		
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		//Set bit
		if(keycode == upKey)
			inputState &= ~KEY_UP;
		
		if(keycode == downKey)
			inputState &= ~KEY_DOWN;
		
		if(keycode == leftKey)
			inputState &= ~KEY_LEFT;
		
		if(keycode == rightKey)
			inputState &= ~KEY_RIGHT;
		
		changed = true;
		
		return true;
	}
	
	public void sendInputIfChanged(GameState gs) {
		if(changed) {
			changed = false;
			
			inputPacket.keyBits = inputState;
			gs.getClientHandler().getClient().sendTCP(inputPacket);
		}
	}
	
	public boolean getKey(byte key) {
		//AND operator to check one bit
		return (inputState & key) == key;
	}
}
