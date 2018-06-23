package seabattle.client;

import games.client.sprites.EngineTemplate;
import games.client.sprites.ImageEngine;
import games.client.sprites.Rectangle;
import games.client.sprites.Sprite;
import games.client.sprites.SpriteGraphics;
import games.client.sprites.SpriteImage;
import games.client.util.DrawElement;
import games.client.util.ImageFactory;
import games.client.util.UIUtils;
import games.shared.Resources;

public class BurningSprite extends Sprite {

	private static final int DELAY = 10;
	private static SpriteImage[] _crash;
	private static SpriteImage[] _explose;
	
	class DisappearEngine extends EngineTemplate<DrawElement> implements ImageEngine<DrawElement> {

		private int _count;
		
//		private int _startDelay = 0;
//		private long _startTime;
		
		private long _ticks;

		public DisappearEngine() {
			super(null, null);
//			_startTime = System.currentTimeMillis();
//			_startDelay = startDelay;
		}

		@Override
		public <Z> void draw(final SpriteGraphics<Z> graphics, final SpriteImage<Z> image,
				final int x, final int y, final Rectangle dirtyRegion) {
			if(_explosion){
				_explose[_count].draw(graphics, x, y, dirtyRegion);
			} else {
				_crash[_count].draw(graphics, x, y, dirtyRegion);
			}
		}

		@Override
		protected boolean needsChanges() {
			return true;
		}

		@Override
		protected void changeSprite(final long coeff) {
			_count = (int) (_ticks / DELAY);
			
			if(_explosion){
				if(_count < 8){
					_count++; 
				} else {
					_count = 0;
					_explosion = false;
					setBounds(getX() + CellsContainerGroup.CELL_SIZE, getY() + CellsContainerGroup.CELL_SIZE, _crash[0].width(), _crash[0].height());
				}
			}
			else{
				if(_count < 4) {
					_count++;
				}
				if(_count == 4){
					if(_last) {
						_parent.removeSprite(getSprite());
					}
					_count = 0;
				}
			}
			
			
		}

		@Override
		protected void finalChanges() {
//			if (_last)
//			_parent.removeSprite(getSprite());
		}

		@Override
		protected long calculateSpriteChangeCoeff(final long elapsed) {
			_ticks += elapsed;
			return elapsed;
		}
		
	}
	
	
//	private int _count;
	private boolean _explosion = true;
	private boolean _last;
	private CellsContainerGroup _parent;
	
	static {
		final DrawElement crashElement = Resources.get(ImageFactory.class).getDrawElement("crash");
		_crash = UIUtils.cutSpriteVertically(crashElement, crashElement.getWidth(), crashElement.getHeight(), 4); 
		final DrawElement exploseElement = Resources.get(ImageFactory.class).getDrawElement("explose");
		_explose = UIUtils.cutSpriteVertically(exploseElement, exploseElement.getWidth(), exploseElement.getHeight(), 8); 
	}
	
	public BurningSprite(final int x, final int y, final CellsContainerGroup parent) {
		super(_explose[0], x, y, _explose[0].width(), _explose[0].height());
		_parent = parent;
		setImageEngine(new DisappearEngine());
	}

	public void setLast(final boolean b) {
		_last = b;
	}
	
	public void setXY(final int x, final int y) {
		if(_explosion) {
			super.moveTo(x - CellsContainerGroup.CELL_SIZE, y - CellsContainerGroup.CELL_SIZE);
		} else {
			super.moveTo(x, y);
		}
	}

}
