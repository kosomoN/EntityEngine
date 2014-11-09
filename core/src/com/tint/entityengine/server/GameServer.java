package com.tint.entityengine.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.JsonSerialization;
import com.esotericsoftware.kryonet.Server;
import com.tint.entityengine.GameMap;
import com.tint.entityengine.entity.components.CollisionComponent;
import com.tint.entityengine.entity.components.PositionComponent;
import com.tint.entityengine.entity.components.RenderComponent;
import com.tint.entityengine.entity.components.StaticCollisionComponent;
import com.tint.entityengine.entity.components.renderers.DirectionalRenderer;
import com.tint.entityengine.entity.components.renderers.TextureRenderer;
import com.tint.entityengine.network.packets.Packet;
import com.tint.entityengine.server.ServerClient.ClientState;
import com.tint.entityengine.server.entity.components.AiComponent;
import com.tint.entityengine.server.entity.components.NetworkComponent;
import com.tint.entityengine.server.entity.components.ai.AiCow;
import com.tint.entityengine.server.entity.systems.AiSystem;
import com.tint.entityengine.server.entity.systems.ServerCollisionSystem;
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
		engine.addSystem(new AiSystem(this));
		engine.addSystem(new ServerCollisionSystem());
		
		loadMap();
		
		jsonSerialization = new JsonSerialization();
		server = new Server();
		Packet.register(server.getKryo());
		server.start();
		try {
			server.bind(54333, 54333);
		} catch(IOException e) {
			System.err.println("Failed to bind server");
			e.printStackTrace();
			System.exit(1);
		}
		serverListener = new ServerListener(this);
		
		server.addListener(serverListener);
		
		if(true) {
			Entity e = new Entity();
			PositionComponent pos = new PositionComponent(1900, 1900);
			e.add(pos);
			e.add(new NetworkComponent());
			e.add(new AiComponent(new AiCow(e)));
			
			RenderComponent rc = new RenderComponent();
			rc.renderer = new DirectionalRenderer();
			((DirectionalRenderer) rc.renderer).animFile = "Cow";
			e.add(rc);
			
			engine.addEntity(e);
		}
		
		if(true) {
			Entity e = new Entity();
			PositionComponent pos = new PositionComponent(1850, 1950);
			e.add(pos);
			e.add(new NetworkComponent());
			e.add(new StaticCollisionComponent(24, 14));
			
			RenderComponent rc = new RenderComponent();
			rc.renderer = new TextureRenderer();
			((TextureRenderer) rc.renderer).textureFile = "Tree";
			e.add(rc);
			
			engine.addEntity(e);
		}
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
	
	//Should hopefully maybe at some point be replaced
	private void loadMap() {
		try {
			JsonReader jsonReader = new JsonReader();
			JsonValue mapJson = jsonReader.parse(new FileInputStream(new File("maps/Basic.json")));

			map = new GameMap(mapJson.getInt("width"), mapJson.getInt("height"), null);
			
			short[] tiles = mapJson.get("layers").get(0).get("data").asShortArray();
			for(int i = 0; i < map.getWidth(); i++) {
				for(int j = 0; j < map.getWidth(); j++) {
					//Flip the map
					map.setTile(i, (map.getHeight() - j - 1), 0, (short) (tiles[j * map.getWidth() + i] - 1));
				}
			}
			
			short[] cosmeticTiles = mapJson.get("layers").get(1).get("data").asShortArray();
			for(int i = 0; i < map.getWidth(); i++) {
				for(int j = 0; j < map.getWidth(); j++) {
					map.setTile(i, (map.getHeight() - j - 1), 1, (short) (cosmeticTiles[j * map.getWidth() + i] - 1));
				}
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
}
