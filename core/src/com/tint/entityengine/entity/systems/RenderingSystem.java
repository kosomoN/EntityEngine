package com.tint.entityengine.entity.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.tint.entityengine.Camera;
import com.tint.entityengine.GameMap;
import com.tint.entityengine.GameState;
import com.tint.entityengine.Mappers;
import com.tint.entityengine.entity.components.AttackHitbox;
import com.tint.entityengine.entity.components.CollisionComponent;
import com.tint.entityengine.entity.components.HealthComponent;
import com.tint.entityengine.entity.components.PositionComponent;
import com.tint.entityengine.entity.components.RenderComponent;

public class RenderingSystem  {

    public static boolean renderHitboxes = false, renderServerPos = false, renderHealth = false, renderFields = false;
    public static Vector2 serverPlayerPos = new Vector2();
    
	private ShapeRenderer shapeRenderer;
    private Array<Entity> sortedEntities = new Array<Entity>(false, 64);
	private GameMap map;
	private GameState gs;
	
	private BitmapFont font = new BitmapFont();
	
    public RenderingSystem(GameState gs, GameMap map) {
    	this.map = map;
    	this.gs = gs;
        shapeRenderer = new ShapeRenderer();
        gs.getEngine().addEntityListener(Family.getFor(RenderComponent.class), new EntityListener() {
			@Override
			public void entityRemoved(Entity entity) {
				sortedEntities.removeValue(entity, true);
			}
			
			@Override
			public void entityAdded(Entity entity) {
				sortedEntities.add(entity);
			}
		});
    }

    public void render(float frameTime, SpriteBatch batch) {
    	//Sort using insertion sort
    	for(int i = 1; i < sortedEntities.size; i++) {
    		Entity tempEnt = sortedEntities.get(i);
    		PositionComponent temp = Mappers.position.get(tempEnt);
    		
    		int j;
    		for(j = i - 1; j >= 0 && temp.getLerpY(frameTime) > Mappers.position.get(sortedEntities.get(j)).getLerpY(frameTime); j--) {
    			sortedEntities.set(j + 1, sortedEntities.get(j));
    		}
    		
    		sortedEntities.set(j + 1, tempEnt);
    	}
    	
    	PositionComponent playerPos = gs.getPlayer().getPos();
    	Camera.orthoCam.position.set(playerPos.getLerpX(frameTime), playerPos.getLerpY(frameTime), 0);
    	Camera.orthoCam.update();
    	batch.setProjectionMatrix(Camera.orthoCam.combined);
    	
    	batch.begin();
    	map.render(batch);
        for (int i = 0; i < sortedEntities.size; ++i) {
            Entity entity = sortedEntities.get(i);
            RenderComponent renderComp = Mappers.render.get(entity);
            
            PositionComponent posComp = Mappers.position.get(entity);
            renderComp.renderer.render(batch, entity, posComp, frameTime);

			HealthComponent health = Mappers.health.get(entity);
			
			if(renderHealth && health != null) {
				font.draw(batch, health.getHp() + "/" + health.getMaxHp(), posComp.getLerpX(frameTime), posComp.getLerpY(frameTime) + 50);
			}
        }
        
        batch.end();
        
        if(renderFields) {
        	shapeRenderer.setProjectionMatrix(Camera.orthoCam.combined);
	        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
	        
	    	//Lower left
	  		int startX = (int) ((Camera.orthoCam.position.x - Camera.orthoCam.viewportWidth / 2) / GameMap.TILE_SIZE);
	  		if(startX < 0) startX = 0;
	  		else if(startX >= map.getWidth()) startX = map.getWidth() - 1;
	  		
	  		int startY = (int) ((Camera.orthoCam.position.y - Camera.orthoCam.viewportHeight / 2) / GameMap.TILE_SIZE);
	  		if(startY < 0) startY = 0;
	  		else if(startY >= map.getHeight()) startY = map.getHeight() - 1;
	  		
	  		//Upper right
	  		int endX = (int) (startX + Math.ceil(Camera.orthoCam.viewportWidth / GameMap.TILE_SIZE) + 1);
	  		if(endX < 0) endX = 0;
	  		else if(endX >= map.getWidth()) endX = map.getWidth() - 1;
	  		int endY = (int) (startY + Math.ceil(Camera.orthoCam.viewportHeight / GameMap.TILE_SIZE) + 1);
	  		if(endY < 0) endY = 0;
	  		else if(endY >= map.getHeight()) endY = map.getHeight() - 1;
	  		
	  		for(int l = 0; l < GameMap.LAYERS; l++) {
				for(int i = startX; i < endX; i++) {
					for(int j = startY; j < endY; j++) {
						if(map.getTile(i, j, l) != -1) {
							int value = map.getMapField().getValue(i, j);
							
							if(value < 0)
								shapeRenderer.setColor(Math.min(-value, 500) / 500f, 0, 0, 0.2f);
							else
								shapeRenderer.setColor(0, 0, Math.min(value, 500) / 500f, 0.2f);
							
							shapeRenderer.rect(i * GameMap.TILE_SIZE, j * GameMap.TILE_SIZE, GameMap.TILE_SIZE, GameMap.TILE_SIZE);
						}
					}
				}
			}
	  		shapeRenderer.end();
        }
        
        if(renderHitboxes) {
	        shapeRenderer.setProjectionMatrix(Camera.orthoCam.combined);
	        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
	
	        for (int i = 0; i < sortedEntities.size; ++i) {
	            Entity entity = sortedEntities.get(i);
	            PositionComponent pos = Mappers.position.get(entity);
	            
	            shapeRenderer.setColor(0.2f, 1, 0.2f, 1);
	            
	            CollisionComponent dynamicCollision = Mappers.dynamicCollision.get(entity);
	            if(dynamicCollision != null) {
	            	shapeRenderer.setColor(0.2f, 1, 0.2f, 1);
		            
		            int width = dynamicCollision.getWidth();
		            int height = dynamicCollision.getHeight();
		            shapeRenderer.rect(pos.getLerpX(frameTime) - width / 2, pos.getLerpY(frameTime) - height / 2, width, height);
		            
		            shapeRenderer.setColor(0.1f, 0.6f, 0.1f, 1);
		            if(renderServerPos && entity != gs.getPlayer().getEntity())
		            	shapeRenderer.rect(pos.getX() - width / 2, pos.getY() - height / 2, width, height);
	            }
	            
	            AttackHitbox attackHitbox = Mappers.attackHitbox.get(entity);
	            if(attackHitbox != null) {
	            	shapeRenderer.setColor(1, 0.2f, 0.2f, 1);

	            	int width = attackHitbox.getWidth();
		            int height = attackHitbox.getHeight();
		            shapeRenderer.rect(pos.getLerpX(frameTime) - width / 2 + attackHitbox.getOffsetX(), pos.getLerpY(frameTime) - height / 2 + attackHitbox.getOffsetY(), width, height);
		            
		            shapeRenderer.setColor(0.5f, 0.1f, 0.1f, 1);
		            if(renderServerPos && entity != gs.getPlayer().getEntity())
		            	shapeRenderer.rect(pos.getX() - width / 2 + attackHitbox.getOffsetX(), pos.getY() - height / 2 + attackHitbox.getOffsetY(), width, height);
	            }
	        }
	        
	        if(renderServerPos) {
	            shapeRenderer.setColor(0.2f, 1, 0.2f, 1);
	            
	            CollisionComponent collisionHitbox = Mappers.dynamicCollision.get(gs.getPlayer().getEntity());
	            if(collisionHitbox != null) {
		            int width = collisionHitbox.getWidth();
		            int height = collisionHitbox.getHeight();
		            shapeRenderer.rect(serverPlayerPos.x - width / 2, serverPlayerPos.y - height / 2, width, height);
	            }
	            
	            shapeRenderer.setColor(1, 0.2f, 0.2f, 1);
	            
	            AttackHitbox attackHitbox = Mappers.attackHitbox.get(gs.getPlayer().getEntity());
	            if(attackHitbox != null) {
		            int width = attackHitbox.getWidth();
		            int height = attackHitbox.getHeight();
		            shapeRenderer.rect(serverPlayerPos.x - width / 2 + attackHitbox.getOffsetX(), serverPlayerPos.y - height / 2 + attackHitbox.getOffsetY(), width, height);
	            }
	        }
	
	        shapeRenderer.end();
        }
        
        if(renderFields) {
        	
        }
    }
}
