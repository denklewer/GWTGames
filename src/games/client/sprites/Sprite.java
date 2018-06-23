package games.client.sprites;

public class Sprite<T> {

	private class DefaultEngine implements ImageEngine<T>, MovementEngine {

		@Override
		public boolean updateTick(final long timestamp) {
			return false;
		}

		@Override
		public <T> void draw(final SpriteGraphics<T> graphics,
				final SpriteImage<T> image, final int x, final int y,
				final Rectangle dirtyRegion) {
			image.draw(graphics, x, y, dirtyRegion);
		}

		@Override
		public int getX() {
			return _area.x;
		}

		@Override
		public int getY() {
			return _area.y;
		}

	}

	private SpriteImage<T> _image;

	private final Rectangle _area = new Rectangle(0, 0, 0, 0); 
	private Rectangle _drawArea;
	
	private ImageEngine<T> _imageEngine;
	private MovementEngine _movementEngine;
	private final DefaultEngine _defaultEngine = new DefaultEngine();
	private SpriteGroup<T> _spriteGroup;
	private Rectangle _allowedRegion = new Rectangle(Short.MIN_VALUE,
			Short.MIN_VALUE, Short.MAX_VALUE * 2, Short.MAX_VALUE * 2);

	private boolean _isVisible = true;
	private MouseHandler _mouseHandler;
	private boolean _isOver;


	public Sprite(final int x, final int y, final int width, final int height) {
		this(null, x, y, width, height);
	}

	public Sprite(final SpriteImage<T> image, final int x, final int y,
			final int width, final int height) {
		_image = image;
		changeArea(x, y, width, height);
		_imageEngine = _defaultEngine;
		_movementEngine = _defaultEngine;
	}

	private void changeArea(final int x, final int y, final int width,
			final int height) {
		_area.setBounds(x, y, width, height);
		_drawArea = _area.intersection(_allowedRegion);
	}
	
	public void setImageEngine(final ImageEngine<T> engine) {
		_imageEngine = engine;
	}

	public void setMovementEngine(final MovementEngine engine) {
		_movementEngine = engine;
	}

	public void update(final long timestamp) {
		boolean changed = _imageEngine.updateTick(timestamp);
		if (changed) {
			markDirty();
		}
		changed = _movementEngine.updateTick(timestamp);
		if (changed) {
			moveTo(_movementEngine.getX(), _movementEngine.getY());
		}
	}

	public void paint(final SpriteGraphics<T> graphics,
			final Rectangle paintArea) {
		_imageEngine.draw(graphics, _image, _area.x, _area.y, paintArea);
	}

	public int getX() {
		return _area.x;
	}

	public int getY() {
		return _area.y;
	}

	public Rectangle getDrawArea() {
		return _drawArea;
	}

	public void addedTo(final SpriteGroup<T> spriteGroup) {
		_spriteGroup = spriteGroup;
	}

	public boolean intersects(final Sprite<?> sprite) {
		return getDrawArea().intersects(sprite.getDrawArea());
	}

	public boolean contains(final int x, final int y) {
		return _area.contains(x, y);
	}

	public boolean includes(final Sprite<?> sprite) {
		return contains(sprite._area.x, sprite._area.y)
				&& contains(sprite._area.x + sprite._area.width - 1, sprite._area.y
						+ sprite._area.height - 1);
	}

	public void markDirty() {
		if (_spriteGroup == null) {
			return;
		}
		_spriteGroup.markDirty(this);
	}

	public void setBounds(final int x, final int y, final int width, final int height) {
		if(_area.x == x && _area.y == y && _area.width == width && _area.height == height){
			return;
		}
		markDirty();
		changeArea(x, y, width, height);
		markDirty();
	}
	
	public boolean moveTo(final int x, final int y) {
		if(_area.x == x && _area.y == y){
			return false;
		}
		setBounds(x, y, _area.width, _area.height);
		return true;
	}

	public void resize(final int width, final int height) {
		if(_area.width == width && _area.height == height){
			return;
		}
		setBounds(_area.x, _area.y, width, height);
	}
	
	public void setSpriteImage(final SpriteImage<T> spriteImage) {
		_image = spriteImage;
		markDirty();
	}

	public void setDefaultMovementEngine() {
		_movementEngine = _defaultEngine;
	}

	public void setDefaultImageEngine() {
		_imageEngine = _defaultEngine;
	}

	public void setAllowedRegion(final Rectangle allowedRegion) {
		_allowedRegion = allowedRegion;
		_drawArea = _area.intersection(_allowedRegion);
	}

	public void removedFromGroup(final SpriteGroup<T> spriteGroup) {
		if (_spriteGroup == spriteGroup) {
			_spriteGroup = null;
		}
	}

	public int getWidth() {
		return _area.width;
	}

	public int getHeight() {
		return _area.height;
	}

	public boolean isVisible() {
		return _isVisible;
	}
	
	public boolean isOver() {
        return _isOver;
    }
	
	public void setOver(boolean value){
		_isOver = value;
	}

	public void setVisible(final boolean isVisible) {
		_isVisible = isVisible;
		markDirty();
	}
	
	public void setMouseHandler(final MouseHandler handler) {
		_mouseHandler = handler;
	}

	public boolean onMouseDown(final int x, final int y, int modifiers){
		if (_mouseHandler != null) {
			return _mouseHandler.onMouseDown(x, y, modifiers);
		}
		return false;
	}
	
	public boolean onMouseUp(final int x, final int y, int modifiers){
		if (_mouseHandler != null) {
			return _mouseHandler.onMouseUp(x, y, modifiers);
		}
		return false;
	}

	public boolean onMouseMove(int x, int y) {
		if (_mouseHandler != null) {
			return _mouseHandler.onMouseMove(x, y);
		}
		return false;
	}

	public boolean onMouseEntered(int x, int y) {
		if (_mouseHandler != null) {
			return _mouseHandler.onMouseEntered(x, y);
		}
		return false;
	}

	public boolean onMouseExit() {
		if (_mouseHandler != null) {
			return _mouseHandler.onMouseExit();
		}
		return false;
	}

	public boolean onDoubleClick(int x, int y) {
		if (_mouseHandler != null) {
			return _mouseHandler.onDoubleClick(x, y);
		}
		return false;
	}
}

