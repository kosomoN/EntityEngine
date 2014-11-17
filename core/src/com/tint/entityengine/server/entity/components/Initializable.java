package com.tint.entityengine.server.entity.components;

import com.badlogic.ashley.core.Entity;
import com.tint.entityengine.server.GameServer;

public interface Initializable {
	public void init(GameServer gameServer, Entity entity);
}
