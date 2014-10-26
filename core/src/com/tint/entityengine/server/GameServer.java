package com.tint.entityengine.server;

import java.io.IOException;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.esotericsoftware.kryonet.JsonSerialization;
import com.esotericsoftware.kryonet.Server;
import com.tint.entityengine.entity.components.PositionComponent;
import com.tint.entityengine.server.entity.components.NetworkComponent;
import com.tint.entityengine.server.entity.systems.ServerMovementSystem;
import com.tint.entityengine.server.entity.systems.ServerNetworkSystem;

public class GameServer {

	private static final float TICK_LENGTH = 1000000000.0f / 30.0f;

	private Server server;
	private Engine engine;

	private boolean running = true;
	
	//Used while developing, will be changed to kryo
	public JsonSerialization jsonSerialization;

	public GameServer() {
		engine = new Engine();
		engine.addSystem(new ServerMovementSystem(this));
		engine.addSystem(new ServerNetworkSystem(this));

		jsonSerialization = new JsonSerialization();
		server = new Server(16384, 2048, jsonSerialization);
		server.start();
		try {
			server.bind(54333, 54334);
		} catch(IOException e) {
			System.err.println("Failed to bind server");
			e.printStackTrace();
		}

		server.addListener(new ServerListener(this));

		Entity e = new Entity();
		e.add(new PositionComponent());
		e.add(new NetworkComponent());
		engine.addEntity(e);
	}

	private long lastTickTime;
	private int ticks;
	public void start() {
		lastTickTime = System.nanoTime();
		while(running) {
			if (System.nanoTime() - lastTickTime >= TICK_LENGTH) {
				lastTickTime = System.nanoTime();

				engine.update(TICK_LENGTH);

				ticks++;
			}
		}
	}
	
	public Server getServer() {
		return server;
	}

	public int getTicks() {
		return ticks;
	}
}
