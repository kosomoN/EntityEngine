package com.tint.entityengine.server;

import static com.tint.entityengine.server.ServerClient.ClientState.CONNECTING;
import static com.tint.entityengine.server.ServerClient.ClientState.IN_GAME;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.esotericsoftware.kryonet.Connection;
import com.tint.entityengine.entity.components.HealthComponent;
import com.tint.entityengine.entity.components.PositionComponent;
import com.tint.entityengine.network.packets.ConnectionApprovedPacket;
import com.tint.entityengine.network.packets.CreateEntityPacket;
import com.tint.entityengine.server.entity.components.NetworkComponent;
import com.tint.entityengine.server.entity.components.ServerPlayerComponent;

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
					if(!(comp instanceof ServerPlayerComponent))
						cep.addComponent(components.get(j));
				}
				
				connection.sendTCP(cep);
			}
			
			playerEntity = new Entity();
			playerEntity.add(new PositionComponent((float) (Math.random() * 1000), (float) (Math.random() * 600)));
			playerComponent = new ServerPlayerComponent();
			playerEntity.add(new HealthComponent(100));
			playerEntity.add(new NetworkComponent());
			playerEntity.add(playerComponent);
			gameServer.getEngine().addEntity(playerEntity);
			
			connection.sendTCP(new ConnectionApprovedPacket(playerEntity.getId()));
			
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
