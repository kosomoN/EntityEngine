package com.tint.entityengine.server;

import static com.tint.entityengine.server.ServerClient.ClientState.CONNECTING;
import static com.tint.entityengine.server.ServerClient.ClientState.IN_GAME;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.esotericsoftware.kryonet.Connection;
import com.tint.entityengine.entity.components.AttackHitbox;
import com.tint.entityengine.entity.components.HealthComponent;
import com.tint.entityengine.entity.components.CollisionComponent;
import com.tint.entityengine.entity.components.PositionComponent;
import com.tint.entityengine.entity.components.RenderComponent;
import com.tint.entityengine.entity.components.renderers.DirectionalRenderer;
import com.tint.entityengine.network.packets.ConnectionApprovedPacket;
import com.tint.entityengine.network.packets.CreateEntityPacket;
import com.tint.entityengine.network.packets.MapChunkPacket;
import com.tint.entityengine.server.entity.components.NetworkComponent;
import com.tint.entityengine.server.entity.components.Networked;
import com.tint.entityengine.server.entity.components.ServerPlayerComponent;

import static com.tint.entityengine.GameMap.*;

public class ServerClient {
	public enum ClientState { CONNECTING, IN_GAME; }
	
	private Connection connection;
	private ClientState state;
	private Entity playerEntity;
	private ServerPlayerComponent playerComponent;
	private GameServer gameServer;
	
	public ServerClient(Connection connection, GameServer gameServer) {
		this.connection = connection;
		this.gameServer = gameServer;
		state = CONNECTING;
	}

	public Connection getConnection() {
		return connection;
	}

	public ClientState getState() {
		return state;
	}

	public void update() {
		if(state == CONNECTING) {
			ImmutableArray<Entity> enitites = gameServer.getEngine().getEntitiesFor(Family.getFor(NetworkComponent.class));
			
			for(int i = 0; i < enitites.size(); i++) {
				Entity e = enitites.get(i);
				
				CreateEntityPacket cep = new CreateEntityPacket(e.getId());
				
				ImmutableArray<Component> components = e.getComponents();
				for(int j = 0; j < components.size(); j++) {
					Component comp =  components.get(j);
					if(comp instanceof Networked)
						cep.addComponent(components.get(j));
				}
				
				connection.sendTCP(cep);
			}
			
			playerEntity = new Entity();
			playerEntity.add(new CollisionComponent(20, 20));
			playerEntity.add(new AttackHitbox(20, 32, 0, 10));
			playerEntity.add(new PositionComponent((float) (Math.random() * (500) + 1200), (float) (Math.random() * (500) + 1200)));
			playerComponent = new ServerPlayerComponent();
			playerEntity.add(new HealthComponent(100));
			
			RenderComponent rc = new RenderComponent();
			rc.renderer = new DirectionalRenderer();
			((DirectionalRenderer) rc.renderer).animFile = "Player";
			playerEntity.add(rc);
			
			playerEntity.add(new NetworkComponent());
			playerEntity.add(playerComponent);
			gameServer.getEngine().addEntity(playerEntity);
			
			connection.sendTCP(new ConnectionApprovedPacket(playerEntity.getId()));
			
			MapChunkPacket mcp = new MapChunkPacket();
			mcp.tiles = new short[CHUNK_SIZE][CHUNK_SIZE];
			
			//Loop through chunks
			for(int i = 0; i < gameServer.getMap().getWidth() / CHUNK_SIZE; i++) {
				for(int j = 0; j < gameServer.getMap().getHeight() / CHUNK_SIZE; j++) {
					
					//Loop through chunk tiles
					for(int l = 0; l < LAYERS; l++) {
						for(int cx = 0; cx < CHUNK_SIZE; cx++) {
							for(int cy = 0; cy < CHUNK_SIZE; cy++) {
								mcp.startX = i * CHUNK_SIZE;
								mcp.startY = j * CHUNK_SIZE;
								
								mcp.tiles[cx][cy] = gameServer.getMap().getTile(i * CHUNK_SIZE + cx, j * CHUNK_SIZE + cy, l);
							}
						}
						mcp.layer = l;
						connection.sendTCP(mcp);
					}
				}
			}
			
			state = IN_GAME;
		}
	}

	public Entity getEntity() {
		return playerEntity;
	}

	public int getID() {
		return connection.getID();
	}

	public ServerPlayerComponent getPlayerComponent() {
		return playerComponent;
	}
}
