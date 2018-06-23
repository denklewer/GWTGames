package sevendoors.client;

import games.client.BaseController.Action;
import games.client.BaseView.ActionFinishedScheduledCommand;
import games.client.StopListener;
import games.client.Stopper;
import games.client.sprites.EngineFinishListener;
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
import games.shared.Resources;
import sevendoors.shared.BoardMask;
import sevendoors.shared.SevenDoorsBoard;
import sevendoors.shared.SevenDoorsProtocol;

public class WaterFallView extends SpriteGroup<DrawElement> {

	private static SpriteImage[][] _stripe1 = new SpriteImage[4][4];
	static {
		final SpriteImage[] stripe = UIUtils.cutSpriteVertically(Resources.get(ImageFactory.class).getDrawElement("water-01"), 62, 310, 5);
		for (int i = 0; i < 4; i++) {
			_stripe1[0][i] = stripe[i];
			_stripe1[1][i] = UIUtils.rotate((DrawElement) stripe[i].getImage(), stripe[i].width(), stripe[i].height(), UIUtils.ROTATE_90);
			_stripe1[2][i] = UIUtils.rotate((DrawElement) stripe[i].getImage(), stripe[i].width(), stripe[i].height(), UIUtils.ROTATE_180);
			_stripe1[3][i] = UIUtils.rotate((DrawElement) stripe[i].getImage(), stripe[i].width(), stripe[i].height(), UIUtils.ROTATE_270);
		}
		
	}
	
	private static SpriteImage _water = new SpriteImage(Resources.get(ImageFactory.class).getDrawElement("tile-water-01"), TileWidget.TILE_SIZE, TileWidget.TILE_SIZE);
	
	private static SpriteImage[] _stripe2 = {
		new SpriteImage(UIUtils.addTransparency((DrawElement) _water.getImage(), 0.20), TileWidget.TILE_SIZE, TileWidget.TILE_SIZE),
		new SpriteImage(UIUtils.addTransparency((DrawElement) _water.getImage(), 0.40), TileWidget.TILE_SIZE, TileWidget.TILE_SIZE),
		new SpriteImage(UIUtils.addTransparency((DrawElement) _water.getImage(), 0.60), TileWidget.TILE_SIZE, TileWidget.TILE_SIZE),
		new SpriteImage(UIUtils.addTransparency((DrawElement) _water.getImage(), 0.80), TileWidget.TILE_SIZE, TileWidget.TILE_SIZE),

	};

	private class WaterFallWidget extends Sprite<DrawElement> {
		class WaterFallEngine extends EngineTemplate<DrawElement> implements ImageEngine<DrawElement>{
			
			private long _ticks;
			
			public WaterFallEngine(final EngineFinishListener stopListener,
					final Sprite<DrawElement> sprite) {
				super(stopListener, sprite);
			}
			
			@Override
			public <Z> void draw(final SpriteGraphics<Z> graphics, final SpriteImage<Z> image,
					final int x, final int y, final Rectangle dirtyRegion) {
				if (_isAlreadyWater) {
					if (_deleted) {
						_stripe2[3 - _transp].draw(graphics, x, y, dirtyRegion.intersection(getDrawArea()));
					} else {
						_water.draw(graphics, x, y, dirtyRegion.intersection(getDrawArea()));
					}
				} else {
					_stripe1[_direction][_imgNum].draw(graphics, x, y, dirtyRegion.intersection(getDrawArea()));
					_stripe2[_imgNum].draw(graphics, x, y, dirtyRegion.intersection(getDrawArea()));
				}
			}
			
			@Override
			protected boolean needsChanges() {
				return _transp < 3;
			}
			
			@Override
			protected void changeSprite(final long coeff) {
				if (_ticks > 50) {
					_ticks = 0;
					if (_deleted) {
						_transp++;
						return;
					}
					if (!_isAlreadyWater && _imgNum < 4) {
						_imgNum++;
						if (_imgNum == 4) {
							_isAlreadyWater = true;
							getStopListener().arrived();
						}
					}  
					
				}				
			}
			
			@Override
			protected void finalChanges() {
				WaterFallView.this.removeSprite(WaterFallWidget.this);
				markDirty();
			}
			
			@Override
			protected long calculateSpriteChangeCoeff(final long elapsed) {
				_ticks += elapsed;
				final boolean needChange = (_transp < 3 && _deleted) || (_imgNum < 4 && !_deleted);
				if (_isAlreadyWater && !_deleted) {
					final int[] xx = new int[] {_i + 1, _i - 1, _i, _i};
					final int[] yy = new int[] {_j, _j, _j - 1, _j + 1};
					final int[] direction = new int[] {3, 1, 2, 0};
					for (int i = 0; i < 4; i++) {
						if (xx[i] >= 8 || xx[i] < 0 || yy[i] >= 8 || yy[i] < 0) {
							continue;
						}
						if (hasWater(xx[i], yy[i]) && _waterfall[xx[i]][yy[i]] == null) {
							addWater(_waterfallAction, xx[i], yy[i], direction[i]);
						}
					}
				}
				return needChange ? elapsed : 0;
			}
			
		}

		private int _imgNum;
		private boolean _isAlreadyWater;
		private int _j;
		private int _i;
		private int _direction;
		private boolean _deleted;
		private int _transp;

		public WaterFallWidget(final EngineFinishListener action, final int i, final int j, final int direction) {
			super(SevenDoorsView.LEFT_INDENT + i * TileWidget.TILE_SIZE, SevenDoorsView.TOP_INDENT + j * TileWidget.TILE_SIZE, TileWidget.TILE_SIZE, TileWidget.TILE_SIZE);
			_i = i;
			_j = j;
			_direction = direction;
			setImageEngine(new WaterFallEngine(action, this));
		}

		public void disappear(final Stopper stopListener) {
			_deleted = true;
			setImageEngine(new WaterFallEngine(stopListener, this));
			markDirty();
		}

		
	}
	
	public synchronized void addWater(final EngineFinishListener action, final int i, final int j, final int direction) {
		if (_waterfall[i][j] != null){
			action.arrived();
			return;
		}
		final WaterFallWidget widget = new WaterFallWidget(action, i, j, direction);
		_waterfall[i][j] = widget;
		addSprite(widget);
	}
	
	private synchronized void removeWater(final Stopper stopListener, final int i, final int j) {
		if (_waterfall[i][j] == null) {
			return;
		}
		_waterfall[i][j].disappear(stopListener);
		_waterfall[i][j] = null;
	}

	private WaterFallWidget[][] _waterfall = new WaterFallWidget[SevenDoorsProtocol.BOARD_SIZE][SevenDoorsProtocol.BOARD_SIZE];

	private BoardMask _waterFallMask;

	private Stopper _waterfallAction;
	
	public boolean hasWater(final int i, final int j) {
		if (_waterFallMask != null) {
			return _waterFallMask.isTrue(i, j);
		}
		return false;
	}

	public void setBoard(final Action action, final SevenDoorsBoard board) {
		final StopListener stopListener = new StopListener(
				new ActionFinishedScheduledCommand(action));
		for (int i = 0; i < SevenDoorsProtocol.BOARD_SIZE; i++) {
			for (int j = 0; j < SevenDoorsProtocol.BOARD_SIZE; j++) {
				if (board.getTile(i, j).getId() == SevenDoorsProtocol.WATERFALL) {
					stopListener.incrementExpectedBy(1);
					addWater(stopListener, i, j, 0);
				}
			}
		}
		if(stopListener.getExpected() == 0){
			stopListener.arrived();
		}
	}
	
	public void waterfallSpread(final Action action, final BoardMask mask){
		_waterfallAction = new StopListener(
				new ActionFinishedScheduledCommand(action)){
			@Override
			public void arrived() {
				if(getExpected() <= 1){
					clearWaterFallMask();
				}
				super.arrived();
			};
		};
		final boolean spread = incrementWaterfallAction(mask);
		if(!spread){
			_waterfallAction.arrived();
			clearWaterFallMask();
			return;
		}
		_waterFallMask = mask;
	}
	
	public void waterfallSpread(final Stopper action, final BoardMask mask){
		_waterfallAction = action;
		final boolean spread = incrementWaterfallAction(mask);
		if(!spread){
			_waterFallMask = null;
		} else {
			_waterFallMask = mask;
		}
	}

	private boolean incrementWaterfallAction(final BoardMask mask) {
		boolean spread = false;
		for (int i = 0; i < SevenDoorsProtocol.BOARD_SIZE; i++) {
			for (int j = 0; j < SevenDoorsProtocol.BOARD_SIZE; j++) {
				if (mask.isTrue(i, j) && _waterfall[i][j] == null) {
					_waterfallAction.incrementExpectedBy(1);
					spread = true;
				}
			}
		}
		return spread;
	}
	
	public void clearWaterFallMask(){
		_waterFallMask = null;
	}
	
	public void addWater(final Stopper action, final int x){
		addWater(action, x, 0, 0);
	}
	
	public void tilesMarkedToDelete(final Stopper stopListener, final BoardMask mask) {
		for (int i = 0; i < SevenDoorsProtocol.BOARD_SIZE; i++) {
			for (int j = 0; j < SevenDoorsProtocol.BOARD_SIZE; j++) {
				if (mask.isTrue(i, j) && _waterfall[i][j] != null) {
					removeWater(stopListener, i, j);
				}
			}
		}
	}
	
	public void nextLevel(final Stopper action){
		for (int i = 0; i < SevenDoorsProtocol.BOARD_SIZE; i++) {
			for (int j = 0; j < SevenDoorsProtocol.BOARD_SIZE; j++) {
				if (_waterfall[i][j] != null){
					action.incrementExpectedBy(1);
					removeWater(action, i, j);
				}
			}
		}
	}
	
}
