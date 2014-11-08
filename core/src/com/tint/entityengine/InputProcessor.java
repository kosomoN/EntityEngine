package com.tint.entityengine;

import static com.tint.entityengine.server.entity.components.ServerPlayerComponent.*;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.tint.entityengine.network.packets.InputPacket;

public class InputProcessor extends InputAdapter {
	
	private int upKey = Keys.W, downKey = Keys.S, leftKey = Keys.A, rightKey = Keys.D, attackKey = Keys.SPACE;
	private InputPacket inputPacket = new InputPacket();
	private byte inputState;
	private boolean changed = false;
	private GameState gs;

	public InputProcessor(GameState gs) {
		this.gs = gs;
	}
	
	@Override
	public boolean keyDown(int keycode) {
		//Set bit
		if(keycode == upKey)
			inputState |= KEY_UP;
		
		else if(keycode == downKey)
			inputState |= KEY_DOWN;
		
		else if(keycode == leftKey)
			inputState |= KEY_LEFT;
		
		else if(keycode == rightKey)
			inputState |= KEY_RIGHT;
		
		else if(keycode == attackKey)
			inputState |= KEY_ATTACK;
		
		changed = true;
		
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		//Set bit
		if(keycode == upKey)
			inputState &= ~KEY_UP;
		
		else if(keycode == downKey)
			inputState &= ~KEY_DOWN;
		
		else if(keycode == leftKey)
			inputState &= ~KEY_LEFT;
		
		else if(keycode == rightKey)
			inputState &= ~KEY_RIGHT;
		
		else if(keycode == rightKey)
			inputState &= ~KEY_ATTACK;
		
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
