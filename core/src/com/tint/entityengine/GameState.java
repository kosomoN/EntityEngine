package com.tint.entityengine;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.tint.entityengine.entity.systems.RenderingSystem;
import com.tint.entityengine.network.ClientHandler;
import com.tint.entityengine.states.State;

public class GameState extends State {
	
	private static final float TICK_LENGTH = 1.0f / 30.0f;
	
	private ClientHandler client;
    private Engine engine;
    private RenderingSystem renderSystem;
    private float accumulatedTicks;
	private int ticks;
	
	public Entity e;

    public GameState(Launcher launcher) {
        super(launcher);
        engine = new Engine();
        //engine.addSystem(new MovementSystem());
        renderSystem = new RenderingSystem(engine);

	    client = new ClientHandler(this);
    }

    @Override
    public void render(float delta) {
		accumulatedTicks += delta / TICK_LENGTH;
		
		while (accumulatedTicks >= 1) {
			client.processPackets();
			engine.update(TICK_LENGTH);
			
			accumulatedTicks--;
			ticks++;
		}
		
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		renderSystem.render(accumulatedTicks);
	}

    public Engine getEngine() {
    	return engine;
    }

	public ClientHandler getClientHandler() {
		return client;
	}
}
