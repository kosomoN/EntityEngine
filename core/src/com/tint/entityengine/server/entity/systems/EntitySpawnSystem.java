package com.tint.entityengine.server.entity.systems;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.math.Vector2;
import com.tint.entityengine.Mappers;
import com.tint.entityengine.entity.components.PositionComponent;
import com.tint.entityengine.server.GameServer;
import com.tint.entityengine.server.ServerClient;

public class EntitySpawnSystem extends EntitySystem {

	private List<Spawn> enemySpawns = new ArrayList<Spawn>();
	private static List<String> entityTypes = new ArrayList<String>();
	private static List<String> enemyTypes = new ArrayList<String>();
	private int night;
	private int spawnTicks;
	private float tickRate;
	private boolean isNight = true; // True because when server starts it is dawn
	private Random rand = new Random();
	private GameServer server;
	
	public EntitySpawnSystem(GameServer server)  {
		this.server = server;
	}
	
	@Override
	public void update(float deltaTime) {
		// Day and night cycle
		if(server.getTicks() % (GameServer.DAY_LENGTH + GameServer.NIGHT_LENGTH) < GameServer.DAY_LENGTH) {
			if(isNight) {
				System.out.println("DAY - " + GameServer.DAY_LENGTH + " ticks");
				night++;
				isNight = false;
			}
		} else {
			if(!isNight) {
				System.out.println("NIGHT - " + GameServer.NIGHT_LENGTH + " ticks");
				tickRate = GameServer.NIGHT_LENGTH / (night + 1);
				isNight = true;
			}
			
			// Spawn an enemy regularly throughout the night
			if(spawnTicks > tickRate) {
				spawnEntity();
				spawnTicks = 0;
			} else {
				spawnTicks++;
			}
		}
	}

	public void spawnEntity() {
		long start = System.nanoTime();
		Entity spawnPlayer;
		PositionComponent playerPos;
		List<Vector2> spawns = new ArrayList<Vector2>(); // Keeping track of all possible spawn locations
		List<Integer> clientIndexes = new ArrayList<Integer>(); // Clients that hasn't been "spawners"
		int tiles = 32; // The amount of tiles the screen is to be cut in
		
		synchronized(server.getClients()) {
			// Adding every client
			for(int i = 0; i < server.getClients().size(); i++)
				clientIndexes.add(i);
			
			// If no clients are connected just skip
			if(clientIndexes.isEmpty())
				return;
			
			// Do until you find a suitable spawn location
			do {
				// Getting random player and his/her position
				int index = clientIndexes.get(rand.nextInt(clientIndexes.size()));
				spawnPlayer = server.getClients().get(index).getEntity();
				playerPos = Mappers.position.get(spawnPlayer);
				clientIndexes.remove((Object) index);
				
				
				// Filling list with valid spawn locations
				for(int i = 0; i < tiles; i++)
					for(int j = 0; j < tiles; j++)
						if(i == 0 || j == 0 || i == tiles - 1 || j == tiles - 1)
							spawns.add(new Vector2(i, j));
				
				// Checking for nearby players
				for(ServerClient client : server.getClients()) {
					Entity player = client.getEntity();
					if(!player.equals(spawnPlayer)) {
						// Calculating delta x and y
						PositionComponent pos = Mappers.position.get(player);
						float dx = playerPos.getX() - pos.getX();
						float dy = playerPos.getY() - pos.getY();
						
						
						int xIndex = tiles, yIndex = tiles;
						// One "tile" more
						if(Math.abs(dx) < 1920 * ((tiles + 1) / tiles)) {
							if(dx < 0)
								xIndex = tiles / 2;
							else
								xIndex = 0;
						}
						
						if(Math.abs(dy) < 1080 * ((tiles + 1) / tiles)) {
							if(dy < 0)
								yIndex = 0;
							else
								yIndex = tiles / 2;
						}
						
						// Removing possible spawn locations
						for(int i = xIndex; i < tiles; i++) {
							for(int j = yIndex; j < tiles; j++) {
								for(Iterator<Vector2> it = spawns.iterator(); it.hasNext();) {
									Vector2 vec = it.next();
									if(vec.x == i && vec.x == j)
										if(!spawns.isEmpty())
											it.remove();
								}
							}
						}
					}
				}
			} while(spawns.isEmpty());
			
			// Spawning right amount of enemies
			Vector2 position = spawns.get(rand.nextInt(spawns.size()));
			float x = playerPos.getX() + (position.x - tiles / 2) * (1920 / tiles);
			float y = playerPos.getY() + (position.y - tiles / 2) * (1080 / tiles);
			server.spawnEntity(enemyTypes.get(rand.nextInt(Math.min(night, enemyTypes.size()))), x, y);
		}
		long end = System.nanoTime();
		System.out.println("Spawn enemy: " + (int) ((end - start) / 1000000f));
	}
	
	public static void registerEntityType(String name) {
		String[] split = name.split(" ");
		if(split.length > 1) {
			if(!entityTypes.contains(split[1])) {
				if(split[0].equals("Enemy"))
					enemyTypes.add(split[1]);
				
				entityTypes.add(split[1]);
			}
		} else if(!entityTypes.contains(split[0])) {
			entityTypes.add(split[0]);
		}
	}
	
	public void addSpawnLocation(GameServer server, int x, int y) {
		enemySpawns.add(new Spawn(server, x, y));
	}
	
	public void removeSpawnLocation(int x, int y) {
		for(Spawn spawn : enemySpawns) {
			if(spawn.x == x && spawn.y == y) {
				enemySpawns.remove(spawn);
				break;
			}
		}
	}
	
	private class Spawn {
		private int x = 0, y = 0;
		private GameServer server;
		
		public Spawn(GameServer server, int x, int y) {
			this.x = x;
			this.y = y;
			this.server = server;
		}
		
		public void spawn(int night) {
			Random rand = new Random();
			for(int i = 0; i < night; i++) {
				int entityIndex = rand.nextInt(Math.min(night, enemyTypes.size()));
				server.spawnEntity(enemyTypes.get(entityIndex), x, y);
			}
		}
	}
}
