package com.tint.entityengine.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.tint.entityengine.GameState;
import com.tint.entityengine.Mappers;
import com.tint.entityengine.entity.components.HealthComponent;

public class GameHud {
	
	private Stage stage;
	private GameState gs;
	private HealthBar hpBar;
	private Label hpLabel;
	private HealthComponent playerHealth;
	
	public GameHud(SpriteBatch batch, GameState gs) {
		this.gs = gs;
		stage = new Stage(new ScreenViewport(), batch);
		Skin skin = new Skin(Gdx.files.internal("graphics/ui/EntityEngineUI.json"), new TextureAtlas(Gdx.files.internal("graphics/ui/EntityEngineUI.atlas")));
		
		// Healthbar
		hpBar = new HealthBar(skin);
		hpBar.setSize(768, 64);
		hpBar.setPosition((Gdx.graphics.getWidth() - hpBar.getWidth()) / 2, 50);
		stage.addActor(hpBar);
		
		hpLabel = new Label("HP: 0/0", skin);
		hpLabel.setPosition(hpBar.getX() + hpBar.getWidth() - 2.5f * hpLabel.getWidth(), 50 + (hpBar.getHeight() - hpLabel.getHeight()) / 2);
		stage.addActor(hpLabel);
		
		/*
		Skin skin = new Skin(Gdx.files.internal("ui/uiskin.json")
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
		
		final CheckBox healthRendering = new CheckBox("Render health", skin);
		healthRendering.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				RenderingSystem.renderHealth = healthRendering.isChecked();
			}
		});
		debugTable.row();
		debugTable.add(healthRendering);
		
		stage.addActor(debugWindow);*/
	}
	
	public void updatePlayerHealth() {
		// Uninitialised
		if(hpBar.getMaxValue() == -1) {
			playerHealth = Mappers.health.get(gs.getPlayer().getEntity());
			hpBar.setMaxValue(playerHealth.getMaxHp());
		}

		hpBar.setValue(playerHealth.getHp());
		hpLabel.setText("HP: " + playerHealth.getHp() + "/" + playerHealth.getMaxHp());
	}

	public void render() {
		updatePlayerHealth();
		stage.act();
		stage.draw();
	}

	public Stage getStage() {
		return stage;
	}

}
