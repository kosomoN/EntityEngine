package com.tint.entityengine.entity.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.tint.entityengine.Camera;
import com.tint.entityengine.GameMap;
import com.tint.entityengine.GameState;
import com.tint.entityengine.Mappers;
import com.tint.entityengine.entity.components.PositionComponent;
import com.tint.entityengine.entity.components.RenderComponent;

public class RenderingSystem  {

    private ShapeRenderer shapeRenderer;
    private ImmutableArray<Entity> entities;
	private GameMap map;
	private GameState gs;

    public RenderingSystem(GameState gs, GameMap map) {
    	this.map = map;
    	this.gs = gs;
        shapeRenderer = new ShapeRenderer();
        entities = gs.getEngine().getEntitiesFor(Family.getFor(RenderComponent.class));
    }

    public void render(float frameTime, SpriteBatch batch) {
    	
    	PositionComponent playerPos = gs.getPlayer().getPos();
    	Camera.orthoCam.position.set(playerPos.getLerpX(frameTime), playerPos.getLerpY(frameTime), 0);
    	Camera.orthoCam.update();
    	batch.setProjectionMatrix(Camera.orthoCam.combined);
    	
    	batch.begin();
    	map.render(batch);
    	
        for (int i = 0; i < entities.size(); ++i) {
            Entity entity = entities.get(i);
            RenderComponent renderComp = Mappers.render.get(entity);
            
            renderComp.renderer.render(batch, entity, frameTime);
        }
        
        batch.end();
        /*
        shapeRenderer.setProjectionMatrix(Camera.orthoCam.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (int i = 0; i < entities.size(); ++i) {
            Entity entity = entities.get(i);
            PositionComponent pos = Mappers.position.get(entity);
            
            shapeRenderer.rect(pos.getLerpX(frameTime) - 10, pos.getLerpY(frameTime) - 10, 20, 20);
        }

        shapeRenderer.end();*/
    }
}
