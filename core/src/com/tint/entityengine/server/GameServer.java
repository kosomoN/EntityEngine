package com.tint.entityengine.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Engine;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.JsonSerialization;
import com.esotericsoftware.kryonet.Server;
import com.tint.entityengine.GameMap;
import com.tint.entityengine.network.packets.Packet;
import com.tint.entityengine.server.ServerClient.ClientState;
import com.tint.entityengine.server.entity.systems.ServerNetworkSystem;
import com.tint.entityengine.server.entity.systems.ServerPlayerSystem;

public class GameServer {

	public static final float TICK_LENGTH = 1000000000.0f / 30.0f;

	private Server server;
	private Engine engine;
	private ServerListener serverListener;
	private GameMap map;
	
	private List<ServerClient> clients = new ArrayList<ServerClient>();

	private boolean running = true;
	
	//Used while developing, will be changed to kryo
	public JsonSerialization jsonSerialization;
	
	public GameServer() {
		engine = new Engine();
		engine.addEntityListener(new ServerEntityListener(this));
		engine.addSystem(new ServerPlayerSystem(this));
		engine.addSystem(new ServerNetworkSystem(this));
		
		map = new GameMap(128, 128, null);
		map.randomize();

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
		
		server.addListener(serverListener);
	}

	private long lastTickTime;
	private int ticks;
	public void start() {
		lastTickTime = System.nanoTime();
		while(running) {
			if (System.nanoTime() - lastTickTime >= TICK_LENGTH) {
				lastTickTime = System.nanoTime();

				serverListener.processPackets();
				synchronized (clients) { for(ServerClient client : clients) client.update(); }
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
	
	public int getTicks() {
		return ticks;
	}

	public Engine getEngine() {
		return engine;
	}
	
	public Server getServer() {
		return server;
	}

	public void sendToAllConnectedUDP(Object object) {
		synchronized (clients) {
			for(ServerClient sc : clients) {
				if(sc.getState() == ClientState.IN_GAME)
					sc.getConnection().sendUDP(object);
			}
		}
		
	}

	public void addClient(ServerClient serverClient) {
		synchronized (clients) {
			clients.add(serverClient);
		}
	}

	public void removeClient(Connection connection) {
		synchronized (clients) {
			int index = -1;
			for(int i = 0; i < clients.size(); i++) {
				if(clients.get(i).getConnection() == connection){
					index = i;
					break;
				}
			}
			
			if(index != -1) {
				ServerClient client = clients.get(index);
				
				clients.remove(index);
				
				if(client.getEntity() != null) {
					engine.removeEntity(client.getEntity());
				}
			}
		}
	}

	public List<ServerClient> getClients() {
		return clients;
	}

	public GameMap getMap() {
		return map;
	}
}
