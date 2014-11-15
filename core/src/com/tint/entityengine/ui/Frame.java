package com.tint.entityengine.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

/**
 * Like a regular Window, with a few modifications. 
 * 
 * @author Mads Peter Horndrup
 */
public class Frame extends MovableTable {
	
	private Table topBar;
	private Table content;
	private Label titleLabel;

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
		add(topBar).space(5f).row();
		
		titleLabel = new Label(title, skin);
		topBar.add(titleLabel).expand().align(Align.left).padLeft(5f);
		
		final Frame frame = this;
		ImageButton closeButton = new ImageButton(skin, "closeDialog");
		closeButton.addListener(new ChangeListener() {
			
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				frame.setVisible(false);
			}
		});
		topBar.add(closeButton).expand().align(Align.right);
		
		this.getCell(topBar).expand().fill();
		
		content = new Table();
		add(content);
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