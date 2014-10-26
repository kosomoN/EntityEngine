package com.tint.entityengine.entity.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.tint.entityengine.entity.components.PositionComponent;

public class RenderingSystem  {

    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ShapeRenderer shapeRenderer;
    private ImmutableArray<Entity> entities;

    public RenderingSystem(Engine engine) {
        shapeRenderer = new ShapeRenderer();
        entities = engine.getEntitiesFor(Family.getFor(PositionComponent.class));
    }

    public void render(float frameTime) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (int i = 0; i < entities.size(); ++i) {
            Entity entity = entities.get(i);
            PositionComponent pos = pm.get(entity);
            shapeRenderer.rect(pos.getLerpX(1) - 10, pos.getLerpY(1) - 10, 20, 20);
        }

        shapeRenderer.end();
    }
}
