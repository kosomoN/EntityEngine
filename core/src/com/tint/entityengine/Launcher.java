package com.tint.entityengine;

import java.util.EnumMap;
import java.util.Map;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.tint.entityengine.states.ConnectionState;
import com.tint.entityengine.states.State;

public class Launcher extends Game {
	
	public enum States { GAMESTATE, CONNECTIONSTATE };
	
	public Map<States, State> states = new EnumMap<States, State>(States.class);
	
	public SpriteBatch batch;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		Camera.orthoCam = new OrthographicCamera(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
		
		load();
		enterState(States.CONNECTIONSTATE);
		
	}
	
	public void load() {
		// Loading states
		states.put(States.GAMESTATE, new GameState(this));
		states.put(States.CONNECTIONSTATE, new ConnectionState(this));
	}
	
	public void enterState(States state) {
		State s = states.get(state);
		if(s != null)
			setScreen(s);
		else
			throw new RuntimeException("No state assigned to this enum: " + state);
	}
}
