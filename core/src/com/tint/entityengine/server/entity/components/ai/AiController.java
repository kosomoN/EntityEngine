package com.tint.entityengine.server.entity.components.ai;

import com.badlogic.ashley.core.Entity;
import com.tint.entityengine.server.GameServer;

public interface AiController {
	public void update(int tick);
	public void init(GameServer gameServer, Entity entity);
}
