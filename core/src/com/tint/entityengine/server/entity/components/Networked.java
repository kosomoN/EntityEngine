package com.tint.entityengine.server.entity.components;

public interface Networked {
	public boolean hasChanged();
	public void resetChanged();
}
