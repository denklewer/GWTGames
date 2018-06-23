package games.client.sprites;

import java.util.ArrayList;
import java.util.List;


public class SpriteGroup<T> implements TimerListener<T> {

	private final List<Sprite<T>> _sprites = new ArrayList<Sprite<T>>();
	private List<Rectangle> _dirtyRegions;

	public void addSprite(final Sprite<T> sprite) {
		_sprites.add(sprite);
		sprite.addedTo(this);
		markDirty(sprite);
	}

	void markDirty(final Sprite<T> sprite) {
		Rectangle drawArea = sprite.getDrawArea();
		Rectangle intersected = drawArea.findIntersected(_dirtyRegions);
		while (intersected != null && !intersected.equals(drawArea)) {
			_dirtyRegions.remove(intersected);
			drawArea = intersected.join(drawArea);
			intersected = drawArea.findIntersected(_dirtyRegions);
		}
		_dirtyRegions.add(drawArea);
	}

	public void removeSprite(final Sprite<T> sprite) {
		markDirty(sprite);
		if (sprite != null) {
			_sprites.remove(sprite);
			sprite.removedFromGroup(this);
		}
	}

	public boolean containsSprite(final Sprite<T> sprite){
		return _sprites.contains(sprite);
	}

	public Sprite<T> findSpriteAt(final int x, final int y) {
		for (final Sprite<T> sprite : _sprites) {
			if (sprite.contains(x, y)) {
				return sprite;
			}
		}
		return null;
	}

	@Override
	public void update(final long timestamp) {
		Sprite<T>[] sprites = new Sprite[0];
		sprites = _sprites.toArray(sprites);
		for (final Sprite<T> sprite : sprites) {
			if(sprite.isVisible()){
				sprite.update(timestamp);
			}
		}
	}

	@Override
	public void paint(final SpriteGraphics<T> graphics) {
		if (_dirtyRegions.size() == 0) {
			return;
		}
		Sprite<T>[] sprites = new Sprite[0];
		sprites = _sprites.toArray(sprites);
		for (final Sprite<T> sprite : sprites) {
			for (final Rectangle	dirtyRegion : _dirtyRegions) {
				if (!sprite.isVisible()) {
					continue;
				}
				Rectangle paintArea = sprite.getDrawArea().intersection(dirtyRegion);
				if (!paintArea.isEmpty()) {
					sprite.paint(graphics, paintArea);
				}
			}
		}
	}

	public void addAll(final List<Sprite<T>> icons) {
		if (icons != null) {
			for (final Sprite<T> sprite : icons) {
				addSprite(sprite);
			}
		}
	}

	public void removeAll(final List<Sprite<T>> icons) {
		if (icons != null) {
			for (final Sprite<T> sprite : icons) {
				removeSprite(sprite);
			}
		}
	}
	
	@Override
	public boolean onMouseDown(final int x, final int y, int modifiers){
		if(_sprites.isEmpty()){
			return false;
		}
		for (int i = _sprites.size() - 1; i >= 0; i--) {
			final Sprite<T> sprite = _sprites.get(i);
			if(sprite.isVisible() && sprite.contains(x, y)){
				if(sprite.onMouseDown(x, y, modifiers)){
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean onMouseUp(final int x, final int y, int modifiers){
		if(_sprites.isEmpty()){
			return false;
		}
		for (int i = _sprites.size() - 1; i >= 0; i--) {
			final Sprite<T> sprite = _sprites.get(i);
			if(sprite.isVisible() && sprite.contains(x, y)){
				if(sprite.onMouseUp(x, y, modifiers)){
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean onMouseMove(final int x, final int y){
		if(_sprites.isEmpty()){
			return false;
		}
		boolean moved = false;
		boolean entered = false;
		for (int i = _sprites.size() - 1; i >= 0; i--) {
			final Sprite<T> sprite = _sprites.get(i);
			if(!sprite.isVisible()){
				continue;
			}
			if (sprite.contains(x, y)) {
				if (!entered && !sprite.isOver()) {
					entered = sprite.onMouseEntered(x, y);
					if (entered){
						sprite.setOver(true);
					}
				}
				if (!moved){
					moved = sprite.onMouseMove(x, y);
				}
			} else {
				if (sprite.isOver()) {
					sprite.setOver(false);
					sprite.onMouseExit();
				}
			}
		}
		return moved;
	}

	@Override
	public void setDirtyRegions(List<Rectangle> dirtyRegions) {
		_dirtyRegions = dirtyRegions;
	}

	public void toTop(Sprite<T> widget) {
		 int index = _sprites.indexOf(widget);
         if (index == -1 || index == _sprites.size() - 1 || _sprites.size() < 2)
             return;

         _sprites.add(widget);
         _sprites.remove(index);
         markDirty(widget);		
	}

	@Override
	public boolean onDoubleClick(int x, int y) {
		if(_sprites.isEmpty()){
			return false;
		}
		for (int i = _sprites.size() - 1; i >= 0; i--) {
			final Sprite<T> sprite = _sprites.get(i);
			if(sprite.isVisible() && sprite.contains(x, y)){
				if(sprite.onDoubleClick(x, y)){
					return true;
				}
			}
		}
		return false;
	}
	
	public void hideSprites(){
		setVisible(false);
	}
	
	public void showSprites(){
		setVisible(true);
	}
	
	private void setVisible(boolean value) {
		for (Sprite<T> sprite : _sprites) {
			sprite.setVisible(value);
		}
	}
}
