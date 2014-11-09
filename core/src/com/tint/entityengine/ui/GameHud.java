package com.tint.entityengine.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.tint.entityengine.entity.systems.RenderingSystem;

public class GameHud {
	
	private Stage stage;
	
	public GameHud(SpriteBatch batch) {
		stage = new Stage(new ScreenViewport(), batch);
		Skin skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
		
		Window debugWindow = new Window("Debug tools", skin);
		debugWindow.setWidth(256);
		Table debugTable = new Table(skin);
		debugTable.setFillParent(true);
		debugWindow.addActor(debugTable);
		
		final CheckBox hitboxRendering = new CheckBox("Render hitboxes", skin);
		hitboxRendering.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				RenderingSystem.renderHitboxes = hitboxRendering.isChecked();
			}
		});
		debugTable.add(hitboxRendering);
		
		final CheckBox serverPositionRendering = new CheckBox("Render server position", skin);
		serverPositionRendering.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				RenderingSystem.renderServerPos = serverPositionRendering.isChecked();
			}
		});
		debugTable.row();
		debugTable.add(serverPositionRendering);
		
		stage.addActor(debugWindow);	
	}

	public void render() {
		stage.act();
		stage.draw();
	}

	public Stage getStage() {
		return stage;
	}

}
