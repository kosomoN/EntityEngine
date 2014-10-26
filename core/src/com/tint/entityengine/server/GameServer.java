package com.tint.entityengine.server;

import java.io.IOException;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.esotericsoftware.kryonet.JsonSerialization;
import com.esotericsoftware.kryonet.Server;
import com.tint.entityengine.entity.components.PositionComponent;
import com.tint.entityengine.network.packets.CreateEntityPacket;
import com.tint.entityengine.network.packets.Packet;
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
		server = new Server();
		Packet.register(server.getKryo());
		server.start();
		try {
			server.bind(54333, 54334);
		} catch(IOException e) {
			System.err.println("Failed to bind server");
			e.printStackTrace();
		}

		server.addListener(new ServerListener(this));
	}

	private long lastTickTime;
	private int ticks;
	public void start() {
		lastTickTime = System.nanoTime();
		while(running) {
			if (System.nanoTime() - lastTickTime >= TICK_LENGTH) {
				lastTickTime = System.nanoTime();
				if(ticks % 60 == 0) {
					Entity ent = new Entity();
					ent.add(new PositionComponent());
					ent.add(new NetworkComponent());
					engine.addEntity(ent);
					
					CreateEntityPacket cep = new CreateEntityPacket(ent.getId());
					
					ImmutableArray<Component> components = ent.getComponents();
					for(int i = 0; i < components.size(); i++) {
						cep.addComponent(components.get(i));
					}
					
					server.sendToAllTCP(cep);
				}

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

	public Engine getEngine() {
		return engine;
	}
}
