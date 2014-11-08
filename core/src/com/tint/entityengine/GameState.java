package com.tint.entityengine;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.tint.entityengine.entity.systems.RenderingSystem;
import com.tint.entityengine.network.ClientHandler;
import com.tint.entityengine.states.State;
import com.tint.entityengine.ui.GameHud;

public class GameState extends State {
	
	public static final float TICK_LENGTH = 1.0f / 30.0f;

	private ClientHandler client;
	private Engine engine;
	private RenderingSystem renderSystem;
	private InputProcessor inputProcessor = new InputProcessor(this);
	private InputMultiplexer inputMultiplexer;
    public ClientPlayer player;
    private GameMap map;
	private GameHud hud;
    
    private float accumulatedTicks;
	private int ticks;

    public GameState(Launcher launcher) {
        super(launcher);
        engine = new Engine();
        
        //PLZ MOVE LOADING
        try {
			Assets.loadAssets();
		} catch (Exception e) {
			System.err.println("Failed to load assets");
			e.printStackTrace();
			System.exit(1);
		}
        
        map = new GameMap(128, 128, "graphics/terrain.png");
        renderSystem = new RenderingSystem(this, map);

	    client = new ClientHandler(this);
	    
	    hud = new GameHud(launcher.batch);
	    
	    inputMultiplexer = new InputMultiplexer(hud.getStage(), inputProcessor);
    }

    @Override
    public void render(float delta) {
		accumulatedTicks += delta / TICK_LENGTH;
		
		while (accumulatedTicks >= 1) {
			player.update();
			client.processPackets();
			engine.update(TICK_LENGTH);
			inputProcessor.sendInputIfChanged(this);
			
			accumulatedTicks--;
			ticks++;
		}
		
		if(Mappers.health.get(player.getEntity()).getHp() <= 0) {
			client.getClient().close();
			Gdx.app.exit();
		}
		
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		renderSystem.render(ticks + accumulatedTicks, launcher.batch);
		
		hud.render();
	}

    @Override
	public void show() {
		super.show();
		
		Gdx.input.setInputProcessor(inputMultiplexer);
	}
    
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		hud.getStage().getViewport().update(width, height, true);
	}

	public Engine getEngine() {
    	return engine;
    }

	public ClientHandler getClientHandler() {
		return client;
	}

	public int getTick() {
		return ticks;
	}
	
	public ClientPlayer getPlayer() {
		return player;
	}
	
	public InputProcessor getInputProcessor() {
		return inputProcessor;
	}

	public GameMap getMap() {
		return map;
	}
	
}
