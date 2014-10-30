package com.tint.entityengine.server;

import java.io.IOException;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.esotericsoftware.kryonet.JsonSerialization;
import com.esotericsoftware.kryonet.Server;
import com.tint.entityengine.entity.components.PositionComponent;
import com.tint.entityengine.network.packets.Packet;
import com.tint.entityengine.server.entity.components.NetworkComponent;
import com.tint.entityengine.server.entity.components.ServerPlayerComponent;
import com.tint.entityengine.server.entity.systems.ServerNetworkSystem;
import com.tint.entityengine.server.entity.systems.ServerPlayerSystem;

public class GameServer {

	public static final float TICK_LENGTH = 1000000000.0f / 30.0f;

	private Server server;
	private Engine engine;
	private ServerListener serverListener;

	private boolean running = true;
	
	//Used while developing, will be changed to kryo
	public JsonSerialization jsonSerialization;
	
	public ServerPlayerComponent spc;

	public GameServer() {
		engine = new Engine();
		engine.addSystem(new ServerPlayerSystem(this));
		engine.addSystem(new ServerNetworkSystem(this));

		jsonSerialization = new JsonSerialization();
		server = new Server();
		Packet.register(server.getKryo());
		server.start();
		try {
			server.bind(54333, 54334);
		} catch(IOException e) {
			System.err.println("Failed to bind server");
			e.printStackTrace();
			System.exit(1);
		}
		serverListener = new ServerListener(this);
		
		//server.addListener(new Listener.LagListener(200, 200, serverListener));
		server.addListener(serverListener);
		
		Entity ent = new Entity();
		ent.add(new PositionComponent());
		spc = new ServerPlayerComponent();
		ent.add(spc);
		ent.add(new NetworkComponent());
		engine.addEntity(ent);
	}

	private long lastTickTime;
	private int ticks;
	public void start() {
		lastTickTime = System.nanoTime();
		while(running) {
			if (System.nanoTime() - lastTickTime >= TICK_LENGTH) {
				lastTickTime = System.nanoTime();

				serverListener.processPackets();
				engine.update(TICK_LENGTH);

				ticks++;
			}
			
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public Server getServer() {
		return server;
	}

	public int getTicks() {
		return ticks;
	}

	public Engine getEngine() {
		return engine;
	}
}
