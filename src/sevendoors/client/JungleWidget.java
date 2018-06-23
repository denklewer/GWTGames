package sevendoors.client;

import games.client.sprites.EngineTemplate;
import games.client.sprites.ImageEngine;
import games.client.sprites.Rectangle;
import games.client.sprites.Sprite;
import games.client.sprites.SpriteGraphics;
import games.client.sprites.SpriteGroup;
import games.client.sprites.SpriteImage;
import games.client.util.DrawElement;
import games.client.util.ImageFactory;
import games.client.util.UIUtils;
import games.shared.Point;
import games.shared.Resources;
import sevendoors.shared.SevenDoorsProtocol;

public class JungleWidget extends SpriteGroup<DrawElement> {
	
	
	private class JungleElementWidget extends Sprite<DrawElement>{
		
		private class JungleAnimation extends EngineTemplate<DrawElement> implements ImageEngine<DrawElement>{
			
			private long _ticks;
			
			public JungleAnimation() {
				super(null, null);
			}
			
			@Override
			protected boolean needsChanges() {
				return true;
			}
			
			@Override
			protected void changeSprite(final long coeff) {
				if (isOver()) {
					_currentAnim = (int) (_ticks / ANIMATION_PERIOD) % (_animation.length - 1) + 1;
				}
				
				if (!_deleted) {
					if (_ticks / APPEAR_DISAPPEAR_PERIOD > 0 && !_deleted && _appearDisappearAnim < 4) {
						_ticks = 0;
						_appearDisappearAnim++;
					}
				} else {
					if (_ticks / APPEAR_DISAPPEAR_PERIOD > 0) {
						_ticks = 0;
						_appearDisappearAnim--;
						if (_appearDisappearAnim <= 0){
							removeSprite(JungleElementWidget.this);
						}
					}
				}
				
			}
			
			@Override
			protected void finalChanges() {
			}
			
			@Override
			protected long calculateSpriteChangeCoeff(final long elapsed) {
				_ticks += elapsed/2;
				return (isOver() || _deleted || (!_deleted && _appearDisappearAnim < 4)) ? _ticks : 0;
			}

			@Override
			public <Z> void draw(final SpriteGraphics<Z> graphics,
					final SpriteImage<Z> image, final int x, final int y, final Rectangle dirtyRegion) {
				if (_appearDisappearAnim < 4) {
					_transpAnimation[_appearDisappearAnim].draw(graphics, x, y, dirtyRegion);
					return;
				} else {
					_jungleElement.draw(graphics, x, y, dirtyRegion);
				}
				if (_currentAnim != 0 && isOver()){
					_animation[_currentAnim].draw(graphics, x, y, dirtyRegion);
				}				
			}
			
			
			
		}
		
		private static final int ANIMATION_PERIOD = 150;
		private static final int APPEAR_DISAPPEAR_PERIOD = 200;
		
		private int _currentAnim;
		private int _appearDisappearAnim;
		private boolean _deleted;

		public JungleElementWidget(final int x, final int y) {
			super(x, y, 62, 62);
			setImageEngine(new JungleAnimation());
		}
		
		public void dissapear() {
			_deleted = true;
		}
		
		@Override
		public boolean onMouseExit() {
			markDirty();
			return true;
		}
		
		@Override
		public boolean onMouseEntered(final int x, final int y) {
			markDirty();
			return true;
		}
		
	}
	
	public final static int LEFT_INDENT = 50;
	public final static int TOP_INDENT = 40;
	
	private static SpriteImage[] _animation;
	private static SpriteImage _jungleElement;
	private static SpriteImage[] _transpAnimation;
	private final SevenDoorsView _parent;
	private JungleElementWidget[][] _jungle = new JungleElementWidget[SevenDoorsProtocol.BOARD_SIZE][SevenDoorsProtocol.BOARD_SIZE];

	public JungleWidget(final SevenDoorsView parent) {
		_parent = parent;
		final DrawElement image = Resources.get(ImageFactory.class).getDrawElement("thicket");
		_animation = UIUtils.cutSpriteVertically(image, 62, 310, 5);
		
		_jungleElement = new SpriteImage<DrawElement>(image, 62, 62);
		_transpAnimation = new SpriteImage[4];
		
		_transpAnimation[0] = new SpriteImage<DrawElement>(UIUtils.addTransparency(image, 0.2), 62, 62);
		_transpAnimation[1] = new SpriteImage<DrawElement>(UIUtils.addTransparency(image, 0.4), 62, 62);
		_transpAnimation[2] = new SpriteImage<DrawElement>(UIUtils.addTransparency(image, 0.6), 62, 62);
		_transpAnimation[3] = new SpriteImage<DrawElement>(UIUtils.addTransparency(image, 0.8), 62, 62);
	}
	
	public void setJungle(final boolean[][] jungle){
		removeAllWidgets();
		for (int i = 0; i < SevenDoorsProtocol.BOARD_SIZE; i++) {
			for (int j = 0; j < SevenDoorsProtocol.BOARD_SIZE; j++) {
				if(jungle[i][j]) {
					grow(new Point(i,j));
				}
			}
		}
	}
	
	private void removeAllWidgets() {
		for (int i = 0; i < _jungle.length; i++) {
			for (int j = 0; j < _jungle[i].length; j++) {
				final JungleElementWidget widget = _jungle[i][j];
				if(widget != null){
					removeSprite(widget);
				}
			}
		}
	}

	public void grow(final Point p) {
		final JungleElementWidget jungleElementWidget = new JungleElementWidget(LEFT_INDENT + p.x * 62, TOP_INDENT + p.y * 62);
		_jungle[p.x][p.y] = jungleElementWidget;
		addSprite(jungleElementWidget);
	}

	private boolean isOutOfBounds(final Point p) {
		return !(p.x < 8 && p.x >= 0 && p.y < 8 && p.y >= 0);
	}
	
	@Override
	public boolean onMouseDown(final int x, final int y, final int modifiers) {
		if(_parent.hasMachete()){
			final TileWidget tile = _parent.findTile(x, y);
			if(tile == null){
				return false;
			}
			final int i = tile.getI();
			final int j = tile.getJ();
			_parent.useMachete(new Point(i, j));
			return true;
		}
		return false;
	}
	
	public boolean canUseMachete(final int mousex, final int mousey) {
		final int i = mousex / 62;
		final int j = mousey / 62;
		for (int k = -1; k <= 1; k++) {
			for (int l = -1; l <= 1; l++) {
				if (!isOutOfBounds(new Point(i + k, j + l))) {
					if (_jungle[i + k][j + l] != null) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public void showUseMachete(final Point point) {
		_parent.setMachete(false);
		for (int k = -1; k <= 1; k++) {
			for (int l = -1; l <= 1; l++) {
				if (!isOutOfBounds(new Point(point.x + k, point.y + l))) {
					if (_jungle[point.x + k][point.y + l] != null) {
						_jungle[point.x + k][point.y + l].dissapear();
						_jungle[point.x + k][point.y + l] = null;
					}
				}
			}
		}
		
	}

	public void setVisible(final boolean value) {
		for (int i = 0; i < _jungle.length; i++) {
			for (int j = 0; j < _jungle[i].length; j++) {
				final JungleElementWidget widget = _jungle[i][j];
				if(widget != null){
					widget.setVisible(false);
				}
			}
		}
	}

}
