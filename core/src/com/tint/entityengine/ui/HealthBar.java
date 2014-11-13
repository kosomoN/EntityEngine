package com.tint.entityengine.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

/**
 * Custom healthbar used width stage
 * @author Daniel Riissanen
 *
 */
public class HealthBar extends Widget {

	private HealthBarStyle style;
	private float value = -1;
	private float maxValue = -1;
	
	public HealthBar(Skin skin) {
		style = skin.get("default", HealthBarStyle.class);
	}
	
	public void setValue(float value) {
		if(value <= maxValue) this.value = value;
	}
	
	public void setMaxValue(float maxValue) {
		this.maxValue = maxValue;
		if(value > maxValue) value = maxValue;
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		style.background.draw(batch, getX(), getY(), getWidth(), getHeight());
		
		// In the background texture the frame is 6px wide, 7px tall, texture width 200px, texture height 30px. Using relative positioning and size for fill
		style.fill.draw(batch, getX() + 10, getY() + 10, (value / maxValue) * (getWidth() - 20), (getHeight() - 20));
	}
	
	public float getValue() {
		return value;
	}
	
	public float getMaxValue() {
		return maxValue;
	}

	static public class HealthBarStyle {
		public NinePatchDrawable background;
		public NinePatchDrawable fill;
		
		public HealthBarStyle() {
		}
		public HealthBarStyle(HealthBarStyle style) {
			this.background = style.background;
			this.fill = style.fill;
		}
	}
}
