package com.tint.entityengine.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.tint.entityengine.Launcher;

public class MainmenuState extends State {

	public MainmenuState(Launcher launcher) {
		super(launcher);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}
}
