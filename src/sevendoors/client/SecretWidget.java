package sevendoors.client;

import games.client.sprites.ImageEngine;
import games.client.sprites.Rectangle;
import games.client.sprites.Sprite;
import games.client.sprites.SpriteGraphics;
import games.client.sprites.SpriteImage;
import games.client.util.DrawElement;
import games.client.util.ImageFactory;
import games.client.util.UIUtils;
import games.shared.Resources;

public class SecretWidget extends Sprite<DrawElement> {

	private final SevenDoorsView _parent;
	private boolean _solved;
	private int _num;
	
	private boolean _hintBlinking;

	public SecretWidget(final SevenDoorsView parent, final int x, final int y) {
		super(x, y, 85, 77);
		_parent = parent;
		
		final SpriteImage[] secretImage = UIUtils.cutSpriteVertically(Resources.get(ImageFactory.class).getDrawElement("bonus-secret"), 85, 154, 2);
		final SpriteImage light = new SpriteImage<DrawElement>(Resources.get(ImageFactory.class).getDrawElement("bonus-secret-light"), 85, 77);
		final SpriteImage[] secretSymbols = UIUtils.cutSpriteVertically(Resources.get(ImageFactory.class).getDrawElement("bonus-secret-symbol-02"), 85, 539, 7);
		
		
		setImageEngine(new ImageEngine<DrawElement>() {
			
			@Override
			public boolean updateTick(final long timestamp) {
				return false;
			}
			
			@Override
			public <Z> void draw(final SpriteGraphics<Z> graphics, final SpriteImage<Z> image,
					final int x, final int y, final Rectangle dirtyRegion) {
				secretImage[0].draw(graphics, x, y, dirtyRegion);
				if (isOver()) {
					secretImage[1].draw(graphics, x, y + 1, dirtyRegion);
					if (_solved) {
						light.draw(graphics, x, y + 1, dirtyRegion);
					}
				}
				secretSymbols[_num].draw(graphics, x, y, dirtyRegion);
			}
		});
	}
	
	@Override
	public boolean onMouseEntered(final int x, final int y) {
		if(_hintBlinking){
			markDirty();
			return true;
		}
		animateSecret();
		return true;
	}

	public void animateSecret() {
		_parent.showSymbolsEvent(true);
		markDirty();
	}
	
	@Override
	public boolean onMouseExit() {
		if(_hintBlinking){
			markDirty();
			return true;
		}
		stopAnimateSecret();
		return true;
	}

	public void stopAnimateSecret() {
		_parent.showSymbolsEvent(false);
		markDirty();
	}
	
	public void setSecret(final int num){
		_num = num;
		markDirty();
	}
	
	public void secretStatusChanged(final boolean solved){
		_solved = solved;
	}
	
	@Override
	public boolean onMouseDown(final int x, final int y, final int modifiers) {
		if (_solved) {
			_parent.secretSolved();
		}
		return true;
	}
	
	public void setHintBlinking(final boolean hintBlinking) {
		_hintBlinking = hintBlinking;
	}
	
}
