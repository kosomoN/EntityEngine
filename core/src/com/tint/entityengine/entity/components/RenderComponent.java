package com.tint.entityengine.entity.components;

import com.badlogic.ashley.core.Component;
import com.tint.entityengine.entity.components.renderers.Renderer;
import com.tint.entityengine.server.entity.components.Networked;

public class RenderComponent extends Component implements Networked {

	public Renderer renderer;
	
	@Override
	public boolean hasChanged() {
		return renderer.hasChanged();
	}

	@Override
	public void resetChanged() {
		renderer.resetChanged();
	}

}
