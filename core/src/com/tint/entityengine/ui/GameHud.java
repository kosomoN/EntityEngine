package com.tint.entityengine.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class GameHud {
	
	private Stage stage;
	
	public GameHud(SpriteBatch batch) {
		stage = new Stage(new ScreenViewport(), batch);
		Skin skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
	}

	public void render() {
		stage.act();
		stage.draw();
	}

	public Stage getStage() {
		return stage;
	}

}
