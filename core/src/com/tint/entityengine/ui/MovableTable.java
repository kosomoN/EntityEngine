package com.tint.entityengine.ui;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

/**
 * This is very much like a regular Table, although it has some extra features. 
 * It is movable on a Stage, and can be resized by dragging.
 * It can also act modal if needed, and be locked to be inside of the Stage if needed.
 * 
 * This is basicly Window from scene2d.ui, with a few modifications.
 * 
 * @author Mads Peter Horndrup
 */
public class MovableTable extends Table {
	
	/** Different flags to define different behaviors */
	private boolean isModal, isResizable, isMovable;
	
	/** Maximum pixels from the border where the MovableWindow can be resized */
	private int resizeBorder = 8, moveAreaHeight = 50;
	
	/** Are we currently resizing or moving the MovableWindow?? */
	private boolean dragging;
	
	/** Should we make sure the MovableWindow stays inside the Viewport?? */
	private boolean keepWithinStage;
	
	/** What Actors allow the MovableWindow to be dragged */
	protected Actor[] dragActors;
	
	private MovableTableStyle style;
	
	/** Used to indicate that side of the MovableWindow that is being resized, and if it's being moved */
	static private final int MOVE = 1 << 5;
	
	public MovableTable(Skin skin) {
		this(skin.get(MovableTableStyle.class));
		setSkin(skin);
	}

	public MovableTable(Skin skin, String styleName) {
		this(skin.get(styleName, MovableTableStyle.class));
		setSkin(skin);
	}

	public MovableTable(MovableTableStyle closeableWindowStyle) {
		setTouchable(Touchable.enabled);
		setClip(true);
		setStyle(closeableWindowStyle);
		setWidth(150);
		setHeight(150);
		setMovable(true);
		setResizable(false);
		setKeepWithinStage(true);
		
		dragActors = new Actor[1];
		dragActors[0] = this;

		addCaptureListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				toFront();
				return false;
			}
		});
		addListener(new InputListener() {
			int edge;
			float startX, startY, lastX, lastY;

			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				Actor hitActor = hit(x,y,true);
				boolean success = false;
				for (Actor a : getDraggableRegions()) {
					if (hitActor.equals(a)) {
						success = true;
					}
				}
				if (!success) {
					return false;
				}
				if (button == Input.Buttons.LEFT) {
					int border = resizeBorder;
					float width = getWidth(), height = getHeight();
					edge = 0;
					if (isResizable) {
						if (x < border)
							edge |= Align.left;
						if (x > width - border)
							edge |= Align.right;
						if (y < border)
							edge |= Align.bottom;
						if (y > height - border)
							edge |= Align.top;
						if (edge != 0)
							border += 25;
						if (x < border)
							edge |= Align.left;
						if (x > width - border)
							edge |= Align.right;
						if (y < border)
							edge |= Align.bottom;
						if (y > height - border)
							edge |= Align.top;
					}
					if (isMovable 
							&& edge == 0 	// We are not resizing!
							&& y <= height	// Inside the Window
							&& y >= height - moveAreaHeight 		
							&& x >= 0
							&& x <= width)
						edge = MOVE;
					dragging = edge != 0;
					startX = x;
					startY = y;
					lastX = x;
					lastY = y;
				}
				return edge != 0 || isModal;
			}

			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {
				dragging = false;
			}

			public void touchDragged(InputEvent event, float x, float y,
					int pointer) {
				if (!dragging)
					return;
				float width = getWidth(), height = getHeight();
				float windowX = getX(), windowY = getY();

				float minWidth = getMinWidth(), maxWidth = getMaxWidth();
				float minHeight = getMinHeight(), maxHeight = getMaxHeight();
				Stage stage = getStage();
				boolean clampPosition = keepWithinStage
						&& getParent() == stage.getRoot();

				if ((edge & MOVE) != 0) { // We are moving!
					float amountX = x - startX, amountY = y - startY;
					windowX += amountX;
					windowY += amountY;
				}
				if ((edge & Align.left) != 0) { // We are resizing!
					float amountX = x - startX;
					if (width - amountX < minWidth)
						amountX = -(minWidth - width);
					if (clampPosition && windowX + amountX < 0)
						amountX = -windowX;
					width -= amountX;
					windowX += amountX;
				}
				if ((edge & Align.bottom) != 0) {
					float amountY = y - startY;
					if (height - amountY < minHeight)
						amountY = -(minHeight - height);
					if (clampPosition && windowY + amountY < 0)
						amountY = -windowY;
					height -= amountY;
					windowY += amountY;
				}
				if ((edge & Align.right) != 0) {
					float amountX = x - lastX;
					if (width + amountX < minWidth)
						amountX = minWidth - width;
					if (clampPosition
							&& windowX + width + amountX > stage.getWidth())
						amountX = stage.getWidth() - windowX - width;
					width += amountX;
				}
				if ((edge & Align.top) != 0) {
					float amountY = y - lastY;
					if (height + amountY < minHeight)
						amountY = minHeight - height;
					if (clampPosition
							&& windowY + height + amountY > stage.getHeight())
						amountY = stage.getHeight() - windowY - height;
					height += amountY;
				}
				lastX = x;
				lastY = y;
				setBounds(Math.round(windowX), Math.round(windowY),
						Math.round(width), Math.round(height));
			}

			public boolean mouseMoved(InputEvent event, float x, float y) {
				return isModal;
			}

			public boolean scrolled(InputEvent event, float x, float y,
					int amount) {
				return isModal;
			}

			public boolean keyDown(InputEvent event, int keycode) {
				return isModal;
			}

			public boolean keyUp(InputEvent event, int keycode) {
				return isModal;
			}

			public boolean keyTyped(InputEvent event, char character) {
				return isModal;
			}
		});
	}
	
	/**
	 * Checks if the Window is within the Stage. If not, it is pushed inside.
	 */
	private void keepWithinStage () {
		if (!keepWithinStage) return;
		Stage stage = getStage();
		if (getParent() == stage.getRoot()) {
			float parentWidth = stage.getWidth();
			float parentHeight = stage.getHeight();
			if (getX() < 0) setX(0);
			if (getRight() > parentWidth) setX(parentWidth - getWidth());
			if (getY() < 0) setY(0);
			if (getTop() > parentHeight) setY(parentHeight - getHeight());
		}
	}
	
	/**
	 * @return	The Actor that was hit first.
	 */
	public Actor hit (float x, float y, boolean touchable) {
		Actor hit = super.hit(x, y, touchable);
		if (hit == null && isModal && (!touchable || getTouchable() == Touchable.enabled)) return this;
		return hit;
	}
	
	/**
	 * This method is used to determine what Actors can be clicked to drag the dialog.
	 * This is to prevent dragging inside the content of the MovableWindow.
	 * @return	An Array of Actors that can be used to drag the MovableWindow.
	 */
	protected Actor[] getDraggableRegions() {
		return dragActors;
	}
	
	/**
	 * Renders the beautiful Table.
	 */
	public void draw(Batch batch, float parentAlpha) {
		keepWithinStage();
		super.draw(batch, parentAlpha);
	}
	
	public void setStyle(MovableTableStyle style) {
		if (style == null) throw new IllegalArgumentException("style cannot be null.");
		this.style = style;
		setBackground(style.background);
		invalidateHierarchy(); // This one is questionable!
	}
	
	public MovableTableStyle getStyle () {
		return style;
	}
	
	public boolean isModal() {
		return isModal;
	}

	public void setModal(boolean isModal) {
		this.isModal = isModal;
	}

	public boolean isResizable() {
		return isResizable;
	}

	public void setResizable(boolean isResizable) {
		this.isResizable = isResizable;
	}

	public boolean isMovable() {
		return isMovable;
	}

	public void setMovable(boolean isMovable) {
		this.isMovable = isMovable;
	}

	public boolean isKeepWithinStage() {
		return keepWithinStage;
	}

	public void setKeepWithinStage(boolean keepWithinStage) {
		this.keepWithinStage = keepWithinStage;
	}

	static public class MovableTableStyle {
		
		public Drawable background;	
	}
}