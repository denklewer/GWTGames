package sevendoors.client;

import games.client.Stopper;
import games.client.sprites.EngineFinishListener;
import games.client.sprites.EngineTemplate;
import games.client.sprites.ImageEngine;
import games.client.sprites.Rectangle;
import games.client.sprites.Sprite;
import games.client.sprites.SpriteGraphics;
import games.client.sprites.SpriteImage;
import games.client.util.DrawElement;
import games.client.util.ImageFactory;
import games.client.util.UIUtils;
import games.shared.Point;
import games.shared.Resources;
import sevendoors.shared.BoardMask;
import sevendoors.shared.SevenDoorsProtocol;
import sevendoors.shared.SnakeDirectionFinder;

public class SnakeWidget extends Sprite<DrawElement> {

	private final class SnakeImageEngine extends EngineTemplate<DrawElement> implements ImageEngine<DrawElement> {
		
		private long _ticks;

		public SnakeImageEngine(final EngineFinishListener stopListener,
				final Sprite<DrawElement> sprite) {
			super(stopListener, sprite);
		}

		@Override
		public <Z> void draw(final SpriteGraphics<Z> graphics, final SpriteImage<Z> image,
				final int x, final int y, final Rectangle dirtyRegion) {
			final double alpha = getAlpha();
			_snakeImages[_currentImage][_direction].draw(graphics, x, y, dirtyRegion, alpha);				
		}

		private double getAlpha() {
			double alpha = 1.0d;
			switch (_transparency) {
			case 0:
				alpha = 0.75;
				break;
			case 1:
				alpha = 0.5;
				break;
			case 2:
				alpha = 0.25;
				break;
			default:
				break;
			}
			return alpha;
		}

		@Override
		protected boolean needsChanges() {
			return _transparency < 3;
		}

		@Override
		protected void changeSprite(final long coeff) {
			if (_ticks > 22) {
				_ticks = 0;
				final Point p = SnakeDirectionFinder.getDxDy(_direction);
				_currentImage = (_currentImage + 1) % NUM_OF_IMAGES;
				moveTo(getX() + p.x * 8, getY() + p.y * 8);

				if (_disappear) {
					if (_ticks % 20 == 0) {
						_transparency++;
					}
					return;
				}

				eat();

				if (getX() > _parent.getGameWidth() || getX() < 0 || getY() > _parent.getGameHeight() || getY() < 0) {
					_transparency = 3;
				}
			}			
		}

		@Override
		protected void finalChanges() {
			_parent.removeSprite(getSprite());			
		}

		@Override
		protected long calculateSpriteChangeCoeff(final long elapsed) {
			_ticks += elapsed/2;
			return elapsed;
		}
	}

	private static final int NUM_OF_IMAGES = 6;
	private static SpriteImage[][] _snakeImages = new SpriteImage[NUM_OF_IMAGES][8];
	private BoardMask _mask;

	static {
		final DrawElement snakeElement = Resources.get(ImageFactory.class).getDrawElement("snake-01");
		final SpriteImage[] straightSnakeImages = UIUtils.cutSpriteVertically(snakeElement, 62, 372, NUM_OF_IMAGES);
		
		
		final DrawElement straightSnakeDrawElement270 = UIUtils.rotateDrawElement(snakeElement, 62, 372, UIUtils.ROTATE_270);
		final SpriteImage<DrawElement>[] straightSnakeImages270 = 
				UIUtils.cutStripeHorizontally(straightSnakeDrawElement270, 
						straightSnakeDrawElement270.getWidth(), straightSnakeDrawElement270.getHeight(), NUM_OF_IMAGES);
		
		final DrawElement straightSnakeDrawElement180 = UIUtils.rotateDrawElement(snakeElement, 62, 372, UIUtils.ROTATE_180);
		final SpriteImage<DrawElement>[] straightSnakeImages180 = 
				UIUtils.cutSpriteVertically(straightSnakeDrawElement180, 
						straightSnakeDrawElement180.getWidth(), straightSnakeDrawElement180.getHeight(), NUM_OF_IMAGES);
		
		final DrawElement straightSnakeDrawElement90 = UIUtils.rotateDrawElement(snakeElement, 62, 372, UIUtils.ROTATE_90);
		final SpriteImage<DrawElement>[] straightSnakeImages90 = 
				UIUtils.cutStripeHorizontally(straightSnakeDrawElement90, 
						straightSnakeDrawElement90.getWidth(), straightSnakeDrawElement90.getHeight(), NUM_OF_IMAGES);
		

		final DrawElement snakeElement02 = Resources.get(ImageFactory.class).getDrawElement("snake-02");
		final SpriteImage[] diagonalSnakeImages = UIUtils.cutSpriteVertically(snakeElement02, 62, 372, NUM_OF_IMAGES);
		final DrawElement diagonalSnakeDrawElement270 = UIUtils.rotateDrawElement(snakeElement02, 62, 372, UIUtils.ROTATE_270);
		final SpriteImage<DrawElement>[] diagonalSnakeImages270 = 
				UIUtils.cutStripeHorizontally(diagonalSnakeDrawElement270, 
						diagonalSnakeDrawElement270.getWidth(), diagonalSnakeDrawElement270.getHeight(), NUM_OF_IMAGES);
		
		final DrawElement diagonalSnakeDrawElement180 = UIUtils.rotateDrawElement(snakeElement02, 62, 372, UIUtils.ROTATE_180);
		final SpriteImage<DrawElement>[] diagonalSnakeImages180 = 
				UIUtils.cutSpriteVertically(diagonalSnakeDrawElement180, 
						diagonalSnakeDrawElement180.getWidth(), diagonalSnakeDrawElement180.getHeight(), NUM_OF_IMAGES);
		
		final DrawElement diagonalSnakeDrawElement90 = UIUtils.rotateDrawElement(snakeElement02, 62, 372, UIUtils.ROTATE_90);
		final SpriteImage<DrawElement>[] diagonalSnakeImages90 = 
				UIUtils.cutStripeHorizontally(diagonalSnakeDrawElement90, 
						diagonalSnakeDrawElement90.getWidth(), diagonalSnakeDrawElement90.getHeight(), NUM_OF_IMAGES);
		
		for (int i = 0; i < NUM_OF_IMAGES; i++) {
			final SpriteImage[] tmp = new SpriteImage[] {
					straightSnakeImages[i],
					diagonalSnakeImages[i],

					straightSnakeImages270[i],
					diagonalSnakeImages270[i],

					straightSnakeImages180[i],
					diagonalSnakeImages180[i],

					straightSnakeImages90[i],
					diagonalSnakeImages90[i],
			};

			for (int j = 0; j < 8; j++) {
				_snakeImages[i][j] = tmp[j];
			}
		}
	}

	private int _direction;
	private int _currentImage;
	private boolean _disappear;
	private int _transparency;
	private final SevenDoorsView _parent;
	private final Stopper _action;
	
	public SnakeWidget(final SevenDoorsView parent, final int x, final int y, final Stopper action, final int direction, final BoardMask mask) {
		super(x, y, _snakeImages[0][0].width(), _snakeImages[0][0].height());
		_parent = parent;
		_action = action;
		_direction = direction;
		_mask = mask;
		setImageEngine(new SnakeImageEngine(_action, this));
	}

	private void eat() {

		final int xx = getX() + _snakeImages[0][0].width() / 2 - SevenDoorsView.LEFT_INDENT;
		final int yy = getY() + _snakeImages[0][0].height() / 2 - SevenDoorsView.TOP_INDENT;

		int i = xx / _snakeImages[0][0].width();
		int j = yy / _snakeImages[0][0].height();

		final int delta = 5;
		if ((xx + delta) / _snakeImages[0][0].width() != i ||
				(xx - delta) / _snakeImages[0][0].width() != i ||
				(yy + delta) / _snakeImages[0][0].height() != j ||
				(yy - delta) / _snakeImages[0][0].height() != j) {
			return;
		}


		final Point p = SnakeDirectionFinder.getDxDy(_direction);
		i -= p.x;
		j -= p.y;

		if(i < 0 || i >= SevenDoorsProtocol.BOARD_SIZE || j < 0 || j >= SevenDoorsProtocol.BOARD_SIZE) {
			return;
		}

		if(!_mask.isTrue(i, j)) {
			dissapear();
			return;
		}
		
		_parent.eatWidget(_action, i, j);
	}

	private void dissapear() {
		_disappear = true;
		markDirty();
	}


}
