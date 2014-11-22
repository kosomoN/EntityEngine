package com.tint.entityengine.server.entity.components.ai;

import com.badlogic.ashley.core.Entity;
import com.tint.entityengine.GameMap;
import com.tint.entityengine.Mappers;
import com.tint.entityengine.entity.components.PositionComponent;
import com.tint.entityengine.entity.pathfinding.DynamicField;
import com.tint.entityengine.server.GameServer;
import com.tint.entityengine.server.ServerClient;

public class AiChargeEnemy implements AiController {

	private PositionComponent position;
	private Entity entity;
	private GameServer gameServer;
	private DynamicField goal;
	
	@Override
	public void update(int tick) {
		if(gameServer.getClients().size() == 0)
			return;
		
		PositionComponent playerPos = getNearestPlayerPosition();
		
		// If target isn't set, set it.
		if(goal == null) {
			goal = DynamicField.generate((int) (playerPos.getLerpX(tick) / GameMap.TILE_SIZE), (int) (playerPos.getLerpY(tick) / GameMap.TILE_SIZE), Math.max(gameServer.getMap().getWidth(), gameServer.getMap().getHeight()), true);
		}
		
		// Correct target position
		goal.move((int) (playerPos.getLerpX(tick) / GameMap.TILE_SIZE), (int) (playerPos.getLerpY(tick) / GameMap.TILE_SIZE));
		
		// x, y in tile coordinates
		int x = (int) (position.getX() / GameMap.TILE_SIZE);
		int y = (int) (position.getY() / GameMap.TILE_SIZE);
		
		// Value of the tile the enemy stands on, changes to the highest value of the neighbouring tiles and current tile
		int highestValue = gameServer.getFieldProcessor().getValue(x, y) + goal.getValue(x - goal.getX() + goal.getSize(), y - goal.getY() + goal.getSize()); // currentblock
		int newX = 0, newY = 0;
		for(int i = -1; i < 2; i++) {
			for(int j = -1; j < 2; j++) {
				if(!(i == 0 && j == 0)) {
					int value1 = gameServer.getFieldProcessor().getValue(x + i, y + j) + goal.getValue(x + i - goal.getX() + goal.getSize(), y + j - goal.getY() + goal.getSize());
					
					if(value1 > highestValue) {
						highestValue = value1;
						newX = x + i;
						newY = y + j;
					}
				}
			}
		}
		
		// If newX and newY are 0 it means the enemy found the target position
		if(!(newX == 0 && newY == 0)) {
			float dx = newX * GameMap.TILE_SIZE - position.getX();
			float dy = newY * GameMap.TILE_SIZE - position.getY();
			// Added offset from clean tiles
			dx += position.getLerpX(tick) % GameMap.TILE_SIZE;
			dy += position.getLerpY(tick) % GameMap.TILE_SIZE;
			float angle = (float) (Math.atan2(dy, dx));
			position.add((float) Math.cos(angle) * 3, (float) Math.sin(angle) * 3, -1, true, entity);
		} else {
			float dx = playerPos.getX() - position.getX();
			float dy = playerPos.getY() - position.getY();
			
			// Maximum distance is ~two squares and added a bit restriction
			if(dx * dx + dy * dy < 64 * 64 - 100)
				Mappers.health.get(gameServer.getClients().get(0).getEntity()).addHp(-1);
		}
	}

	@Override
	public void init(GameServer gameServer, Entity entity) {
		this.position = Mappers.position.get(entity);
		this.entity = entity;
		this.gameServer = gameServer;
	}
	
	public PositionComponent getNearestPlayerPosition() {
		PositionComponent currPlayerPos = null;
		float currentDx = Float.MAX_VALUE, currentDy = Float.MAX_VALUE;
		for(ServerClient client : gameServer.getClients()) {
			PositionComponent playerPos = Mappers.position.get(client.getEntity());
			float dx = playerPos.getX() - position.getX();
			float dy = playerPos.getY() - position.getY();
			
			if(dx * dx + dy * dy < currentDx * currentDx + currentDy * currentDy) {
				currentDx = dx;
				currentDy = dy;
				currPlayerPos = playerPos;
			}
		}
		return currPlayerPos;
	}
	
}
