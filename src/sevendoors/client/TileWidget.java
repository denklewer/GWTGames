package sevendoors.client;

import games.client.Stopper;
import games.client.sprites.EngineFinishListener;
import games.client.sprites.EngineTemplate;
import games.client.sprites.ImageEngine;
import games.client.sprites.MovementEngineTemplate;
import games.client.sprites.Rectangle;
import games.client.sprites.Sprite;
import games.client.sprites.SpriteGraphics;
import games.client.sprites.SpriteImage;
import games.client.util.DrawElement;
import games.client.util.ImageFactory;
import games.client.util.UIUtils;
import games.shared.Resources;
import sevendoors.shared.SevenDoorsConstants;
import sevendoors.shared.SevenDoorsMove;
import sevendoors.shared.SevenDoorsProtocol;
import sevendoors.shared.Tile;

public class TileWidget extends Sprite<DrawElement> {
	
	private static final int SWAP_SPEED = 1;

	class DisappearEngine extends EngineTemplate<DrawElement> implements ImageEngine<DrawElement> {

		private static final int PERIOD = 75;
		private int _currentFrame;
		
		private int _startDelay = 0;
		private long _startTime;
		
		private long _ticks;

		public DisappearEngine(final EngineFinishListener stopListener, final Sprite<DrawElement> sprite) {
			this(stopListener, sprite, 0);
		}
		
		public DisappearEngine(final EngineFinishListener stopListener, final Sprite<DrawElement> sprite, final int startDelay) {
			super(stopListener, sprite);
			_startTime = System.currentTimeMillis();
			_startDelay = startDelay;
		}

		@Override
		public <Z> void draw(final SpriteGraphics<Z> graphics, final SpriteImage<Z> image,
				final int x, final int y, final Rectangle dirtyRegion) {
			
			if (_currentFrame > 4) {
				return;
			}
			
			if (_currentFrame == 0) {
				drawTile(graphics, x, y, dirtyRegion.intersection(getDrawArea()));
				_disappearAnimation[2].draw(graphics, x, y, dirtyRegion.intersection(getDrawArea()));
			} else {
				_disappearAnimation[_currentFrame - 1].draw(graphics, x, y, dirtyRegion.intersection(getDrawArea()));
			}
		}

		@Override
		protected boolean needsChanges() {
			return _currentFrame < 5;
		}

		@Override
		protected void changeSprite(final long coeff) {
			_currentFrame = (int) (_ticks / PERIOD);
			if (_currentFrame > 4) {
				if (_tile.getId() == SevenDoorsProtocol.BOX && _tile.hasKey() && _j == SevenDoorsProtocol.BOARD_SIZE - 1) {
					_parent.addKey(getX(), getY());
				} else {
					final int xx = getX() + getWidth()/2;
					final int yy = getY() + getHeight()/2;
					int score;
					if(_tile.getId() == SevenDoorsProtocol.FROG){
						score = 10 * SevenDoorsConstants.SCORE_FOR_FROG;
					} else {
						score = 10;
					}
					if(_animateScore){
						_parent.addScore(xx, yy, score, _starScore);
					}
				}
			}
		}

		@Override
		protected void finalChanges() {
			_parent.removeSprite(getSprite());
			setImageEngine(_defaultImageEngine);
		}

		@Override
		protected long calculateSpriteChangeCoeff(final long elapsed) {
			if(_startDelay > 0 && (System.currentTimeMillis() - _startTime < _startDelay)){
				return 0;
			}
			_ticks += elapsed;
			return elapsed;
		}
		
	}
	
	class BlinkingEngine extends EngineTemplate<DrawElement> implements ImageEngine<DrawElement> {
		
		private static final int PERIOD = 10;
		private int _currentFrame;
		private long _ticks;
		
		public BlinkingEngine() {
			super(null, null);
		}
		
		@Override
		public <Z> void draw(final SpriteGraphics<Z> graphics, final SpriteImage<Z> image,
				final int x, final int y, final Rectangle dirtyRegion) {
			TileWidget.this.drawTile(graphics, x, y, dirtyRegion, false);
			
			if (_tile.getId() < 6) {
				_tileImages[_tile.getId()][_currentFrame + 1].draw(graphics, x, y, dirtyRegion);
			} else if (_tile.getId() == SevenDoorsProtocol.BONUS) {
				_bonus[_currentFrame + 1].draw(graphics, x, y, dirtyRegion);
			} else if (_tile.getId() == SevenDoorsProtocol.FROG) {
				_frogImage[_currentFrame + 1].draw(graphics, x, y, dirtyRegion);
			} else if (_tile.getId() == SevenDoorsProtocol.FOREST) {
				_forestImage.draw(graphics, x, y, dirtyRegion);
			} else if (_tile.getId() == SevenDoorsProtocol.SNAKE) {
				_snakeImage[_currentFrame + 1].draw(graphics, x, y, dirtyRegion);
			} else if (_tile.getId() == SevenDoorsProtocol.BOX) {
				_chest[_currentFrame + 1].draw(graphics, x, y, dirtyRegion);
			}
			
			if (_tile.hasLiana()) {
				_liana.draw(graphics, x, y, dirtyRegion);
			}
		}
		
		@Override
		protected boolean needsChanges() {
			return true;
		}
		
		@Override
		protected void changeSprite(final long coeff) {
			_currentFrame = (int) (_ticks / 150) % 4;
		}
		
		@Override
		protected void finalChanges() {
		}
		
		@Override
		protected long calculateSpriteChangeCoeff(final long elapsed) {
			_ticks += elapsed;
			return elapsed;
		}
		
	}
	
	class ForestEngine extends EngineTemplate<DrawElement> implements ImageEngine<DrawElement> {
		
		private int _currentImage;
		private long _ticks;
		
		public ForestEngine(final Stopper stopListener, final Sprite<DrawElement> sprite) {
			super(stopListener, sprite);
		}
		
		@Override
		public <Z> void draw(final SpriteGraphics<Z> graphics, final SpriteImage<Z> image,
				final int x, final int y, final Rectangle dirtyRegion) {
			drawTile(graphics, x, y, dirtyRegion.intersection(getDrawArea()));
			_forestAppearAnimation[_currentImage].draw(graphics, x, y, dirtyRegion.intersection(getDrawArea()));
		}
		
		@Override
		protected boolean needsChanges() {
			return _currentImage < 2;
		}
		
		@Override
		protected void changeSprite(final long coeff) {
			if(_ticks > 40){
				_ticks = 0;
				_currentImage++;
				markDirty();
			}
		}
		
		@Override
		protected void finalChanges() {
			_tile = new Tile(SevenDoorsProtocol.FOREST);
			setImageEngine(_defaultImageEngine);
			markDirty();
		}
		
		@Override
		protected long calculateSpriteChangeCoeff(final long elapsed) {
			_ticks += elapsed/2;
			return elapsed;
		}
		
	}
	
	private class SwapEngine extends MovementEngineTemplate<DrawElement> {

		private CircleTrajectory _trajectory = new CircleTrajectory();
		private int _ticks;
		private static final int T = 50;

		public SwapEngine(final EngineFinishListener stopListener,
				final Sprite<DrawElement> sprite, final int destX, final int destY, final int speed) {
			super(stopListener, sprite, destX, destY, speed);
			_trajectory.calculate(TileWidget.this.getX(), TileWidget.this.getY(), destX, destY, T);
		}
		
		@Override
		protected long calculateSpriteChangeCoeff(final long elapsed) {
			return elapsed;
		}
		
		@Override
		protected boolean needsChanges() {
			return _ticks < T;
		}

		@Override
		protected void changeSprite(final long coeff) {
			_ticks += coeff/7;
			x = _trajectory.getX(_ticks);
			y = _trajectory.getY(_ticks);
		}
		
	}

	public static final int TILE_SIZE = 62;
	private static final Rectangle ALLOWED_REGION = new Rectangle(SevenDoorsView.LEFT_INDENT, SevenDoorsView.TOP_INDENT, SevenDoorsProtocol.BOARD_SIZE * TILE_SIZE, SevenDoorsProtocol.BOARD_SIZE * TILE_SIZE);
	private static SpriteImage _boxImage;
	private static SpriteImage _lightBoxImage;
	private static SpriteImage _moveMarkerImage;
	private static SpriteImage _selectMarkerImage;
	
	private static SpriteImage[][] _tileImages = new SpriteImage[Tile.N_TILES][5];
	private static SpriteImage[] _disappearAnimation = new SpriteImage[4];
	
	private static SpriteImage[] _chest = UIUtils.cutSpriteVertically(Resources.get(ImageFactory.class).getDrawElement("tile-chest-01"), TILE_SIZE, 310, 5);
	private static SpriteImage _lightBonus = new SpriteImage<DrawElement>(Resources.get(ImageFactory.class).getDrawElement("bonus-light-01"), TILE_SIZE, TILE_SIZE);
	private static SpriteImage[] _bonus = UIUtils.cutSpriteVertically(Resources.get(ImageFactory.class).getDrawElement("tile-star-01"), TILE_SIZE, 310, 5);
	private static SpriteImage[] _frogImage = UIUtils.cutSpriteVertically(Resources.get(ImageFactory.class).getDrawElement("tile-frog-01"), TILE_SIZE, 310, 5);
	private static SpriteImage[] _snakeImage = UIUtils.cutSpriteVertically(Resources.get(ImageFactory.class).getDrawElement("tile-basket-01"), TILE_SIZE, 310, 5);
	private static SpriteImage _liana = new SpriteImage<DrawElement>(Resources.get(ImageFactory.class).getDrawElement("liana-01"), TILE_SIZE, TILE_SIZE);
	
	private static SpriteImage _forestImage = new SpriteImage<DrawElement>(Resources.get(ImageFactory.class).getDrawElement("tile-depthicket-01"), TILE_SIZE, TILE_SIZE);
	private static SpriteImage[] _forestAppearAnimation = {
		new SpriteImage(UIUtils.addTransparency((DrawElement) _forestImage.getImage(), 0.25), TILE_SIZE, TILE_SIZE),
		new SpriteImage(UIUtils.addTransparency((DrawElement) _forestImage.getImage(), 0.50), TILE_SIZE, TILE_SIZE),
		new SpriteImage(UIUtils.addTransparency((DrawElement) _forestImage.getImage(), 0.75), TILE_SIZE, TILE_SIZE),
	};
	
	static {
		_tileImages[0] = UIUtils.cutSpriteVertically(Resources.get(ImageFactory.class).getDrawElement("tile-butterfly"), TILE_SIZE, 310, 5);
		_tileImages[1] = UIUtils.cutSpriteVertically(Resources.get(ImageFactory.class).getDrawElement("tile-canabis"), TILE_SIZE, 310, 5);
		_tileImages[2] = UIUtils.cutSpriteVertically(Resources.get(ImageFactory.class).getDrawElement("tile-flower"), TILE_SIZE, 310, 5);
		_tileImages[3] = UIUtils.cutSpriteVertically(Resources.get(ImageFactory.class).getDrawElement("tile-elephant"), TILE_SIZE, 310, 5);
		_tileImages[4] = UIUtils.cutSpriteVertically(Resources.get(ImageFactory.class).getDrawElement("tile-tukan"), TILE_SIZE, 310, 5);
		_tileImages[5] = UIUtils.cutSpriteVertically(Resources.get(ImageFactory.class).getDrawElement("tile-mask"), TILE_SIZE, 310, 5);
		_boxImage = new SpriteImage<DrawElement>(Resources.get(ImageFactory.class).getDrawElement("box"), TILE_SIZE, TILE_SIZE);
		_lightBoxImage = new SpriteImage<DrawElement>(Resources.get(ImageFactory.class).getDrawElement("lightbox"), TILE_SIZE, TILE_SIZE);
		for (int i = 0; i < _disappearAnimation.length; i++) {
			_disappearAnimation[i] = new SpriteImage<DrawElement>(Resources.get(ImageFactory.class).getDrawElement("disappear-01"), 0, i * TILE_SIZE, TILE_SIZE, TILE_SIZE);
		}
		_moveMarkerImage = new SpriteImage<DrawElement>(Resources.get(ImageFactory.class).getDrawElement("move-marker-01"), TILE_SIZE, TILE_SIZE);
		_selectMarkerImage = new SpriteImage<DrawElement>(Resources.get(ImageFactory.class).getDrawElement("move-marker-01"), 0, TILE_SIZE, TILE_SIZE, TILE_SIZE);
	}
	
	private final SevenDoorsView _parent;
	private Tile _tile;
	private int _i;
	private int _j;
	
	private boolean _isLight = false;
	
	private boolean _hintBlinking;

	private final ImageEngine<DrawElement> _defaultImageEngine = new ImageEngine<DrawElement>() {
		
		@Override
		public boolean updateTick(final long timestamp) {
			return false;
		}
		
		@Override
		public <Z> void draw(final SpriteGraphics<Z> graphics, final SpriteImage<Z> image,
				final int x, final int y, final Rectangle dirtyRegion) {
			drawTile(graphics, x, y, dirtyRegion);
		}

	};
	
	private BlinkingEngine _blinkingEngine;
	private boolean _isDisappearing;
	private boolean _animateScore = true;
	private boolean _starScore = false;

	public TileWidget(final SevenDoorsView parent, final Tile tile,final int x, final int y) {
		super(SevenDoorsView.LEFT_INDENT + x * TILE_SIZE, SevenDoorsView.TOP_INDENT + y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
		_parent = parent;
		_tile = tile;
		setImageEngine(_defaultImageEngine);
		setAllowedRegion(ALLOWED_REGION);
		_i = x;
		_j = y;
		_blinkingEngine = new BlinkingEngine();
	}
	
	@Override
	public boolean onMouseDown(final int x, final int y, final int modifiers) {
		if (!_tile.canBeMoved()) {
			return false;
		}
		final TileWidget lastTile = _parent.lastTile();
		if (lastTile == null) { 
			_parent.setLastTile(this);
			setLight(true);
		} else {
			final SevenDoorsMove move = new SevenDoorsMove(getI(), getJ(), lastTile.getI(), lastTile.getJ());
			_parent.sendMove(move);
			_parent.lastTile().setLight(false);
			setLight(false);
			_parent.setLastTile(null);
			setImageEngine(_defaultImageEngine);
			setOver(false);
			markDirty();
		}
		return true;
	}
	
	@Override
	public boolean onDoubleClick(final int x, final int y) {
		if (_tile.getId() == SevenDoorsProtocol.SNAKE) {
			_parent.activateSnakes(_i, _j);
			return true;
		}
		return false;
	}
	
	private void setLight(final boolean value) {
		_isLight = value;
		markDirty();
	}

	public void disappear(final Stopper stopListener) {
		disappear(stopListener, 0, true);
	}
	
	public void disappear(final Stopper stopListener, final boolean starScore) {
		_starScore = starScore;
		disappear(stopListener, 0, true);
	}
	
	public void disappear(final Stopper stopListener, final int startDelay, final boolean animateScore) {
		_animateScore = animateScore;
		_isDisappearing = true;
		setImageEngine(new DisappearEngine(stopListener, this, startDelay));
	}
	
	public void swap(final Stopper stopListener, final int destX, final int destY){
		setMovementEngine(new SwapEngine(stopListener, this, destX, destY, SWAP_SPEED));
	}
	
	@Override
	public boolean onMouseEntered(final int x, final int y) {
		if(_hintBlinking){
			return true;
		}
		animateTile();
		return true;
	}

	public void animateTile() {
		setImageEngine(_blinkingEngine);
	}
	
	@Override
	public boolean onMouseExit() {
		if(_hintBlinking){
			return true;
		}
		stopAnimateTile();
		return true;
	}

	public void stopAnimateTile() {
		setImageEngine(_defaultImageEngine);
		markDirty();
	}

	public String getTileName() {
		return _tile.toString();
	}

	public void setI(final int i) {
		_i = i;
	}

	public int getI() {
		return _i;
	}

	public void setJ(final int j) {
		_j = j;
	}

	public int getJ() {
		return _j;
	}
	
	private <Z> void drawTile(final SpriteGraphics<Z> graphics, final int x, final int y,
			final Rectangle dirtyRegion) {
		drawTile(graphics, x, y, dirtyRegion, true);
	}
	private <Z> void drawTile(final SpriteGraphics<Z> graphics, final int x, final int y,
			final Rectangle dirtyRegion, final boolean drawStaticImage) {
		
		if (_tile.getId() != SevenDoorsProtocol.BOX && _tile.getId() != SevenDoorsProtocol.SNAKE){
			if(!_tile.isLightBonus()){
				_boxImage.draw(graphics, x, y, dirtyRegion);
			} else {
				_lightBoxImage.draw(graphics, x, y, dirtyRegion);
			}
		}
		
		if (_tile.isLightBonus()) {
			_lightBonus.draw(graphics, x, y, dirtyRegion);
		}
		if(drawStaticImage){
			if (_tile.getId() == SevenDoorsProtocol.FROG) {
				_frogImage[0].draw(graphics, x, y, dirtyRegion);
			} else if (_tile.getId() == SevenDoorsProtocol.SNAKE) {
				_snakeImage[0].draw(graphics, x, y, dirtyRegion);
			} else if (_tile.getId() == SevenDoorsProtocol.FOREST) {
				_forestImage.draw(graphics, x, y, dirtyRegion);
			} else if (_tile.getId() == SevenDoorsProtocol.BOX) {
				_chest[0].draw(graphics, x, y, dirtyRegion);
			} else if (_tile.getId() == SevenDoorsProtocol.BONUS) {
				_bonus[0].draw(graphics, x, y, dirtyRegion);
			} else {
				_tileImages[_tile.getId()][0].draw(graphics, x, y, dirtyRegion);
			}
		}
		
		if (_tile.hasLiana()) {
			_liana.draw(graphics, x, y, dirtyRegion);
		}
		if(_isLight){
			_selectMarkerImage.draw(graphics, x, y, dirtyRegion);
		}
		if(isOver()){
			_moveMarkerImage.draw(graphics, x, y, dirtyRegion);
		}
	}

	public void changeToForest(final Stopper stopListener) {
		setImageEngine(new ForestEngine(stopListener, this));
		markDirty();
	}

	public boolean isDisappearing() {
		return _isDisappearing;
	}

	public boolean isBonus() {
		return _tile.getId() == SevenDoorsProtocol.BONUS;
	}

	public void setHintBlinking(final boolean hintBlinking) {
		_hintBlinking = hintBlinking;
	}

}
