package com.tint.entityengine.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.tint.entityengine.Launcher;

public class GameState extends State {

	private static float TICK_LENGTH = 1000000000 / 60f;
	private float unprocessed;
	private long lastTickTime =  System.nanoTime();
	
	public GameState(Launcher launcher) {
		super(launcher);
	}
	
	@Override
	public void render(float delta) {
		// Updating timer
		long currTime = System.nanoTime();
        unprocessed += (currTime - lastTickTime) / TICK_LENGTH;
        lastTickTime = currTime;
        while(unprocessed >= 1) {
        	unprocessed--;
        	update();
        }
        renderGame();
	}
	
	public void renderGame() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}
	
	public void update() {
		
	}
}
