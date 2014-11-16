package com.tint.entityengine.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/**
 * Like a regular Window, with a few modifications. 
 * 
 * @author Mads Peter Horndrup
 */
public class Frame extends MovableTable {
	
	private Table topBar;
	private Table content;
	private Label titleLabel;
	
	private Drawable windowHeader;

	public Frame(String title, Skin skin) {
		super(skin);
		
		prepareGUI(title, skin);
		
		dragActors = new Actor[3];
		dragActors[0] = this;
		dragActors[1] = topBar;
		dragActors[2] = titleLabel;
	}
	
	private void prepareGUI(String title, Skin skin) {
		topBar = new Table();
		add(topBar).row();
		
		titleLabel = new Label(title, skin.get("brown", LabelStyle.class));
		topBar.add(titleLabel).expand().align(Align.center);
		
		final Frame frame = this;
		ImageButton closeButton = new ImageButton(skin, "closeDialog");
		closeButton.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				frame.setVisible(false);
			}
		});
		//topBar.add(closeButton).align(Align.right);
		
		this.getCell(topBar).fill().padLeft(30).padRight(30).padBottom(20);
		
		content = new Table();
		add(content).expand().fill();
		
		windowHeader = getStyle().windowHeader;
	}
	
	
	@Override
	protected void drawBackground(Batch batch, float parentAlpha, float x, float y) {
		Color color = getColor();
		batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
		
		//Sorry for the hard-coded numbers D:
		
		getBackground().draw(batch, x, y, getWidth(), content.getHeight() + 28);
		windowHeader.draw(batch, x - 5, y + getHeight() - windowHeader.getMinHeight() + 8, getWidth() + 10, windowHeader.getMinHeight());
	}

	/**
	 * Adds an Actor to the content table of this widget.
	 */
	public <T extends Actor> Cell<T> addContent (T actor) {
		return (Cell<T>)content.add(actor);
	}
	
	/**
	 * Indicates that the next inserted item should be on a different row.
	 * @return	The new cell after adding the row.
	 */
	public Cell rowContent() {
		return content.row();
	}
}