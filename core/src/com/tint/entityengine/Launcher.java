package com.tint.entityengine;

import java.util.EnumMap;
import java.util.Map;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.tint.entityengine.states.State;

public class Launcher extends Game {
	
	public enum States { GAMESTATE };
	
	Map<States, State> states = new EnumMap<States, State>(States.class);
	
	SpriteBatch batch;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		
		load();
		enterState(States.GAMESTATE);
		
	}
	
	public void load() {
		// Loading states
		states.put(States.GAMESTATE, new GameState(this));
	}
	
	public void enterState(States state) {
		State s = states.get(state);
		if(s != null)
			setScreen(s);
		else
			throw new RuntimeException("No state assigned to this enum: " + state);
	}
}
