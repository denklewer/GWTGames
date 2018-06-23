package seabattle.client;

import games.client.sprites.ImageEngine;
import games.client.sprites.Rectangle;
import games.client.sprites.Sprite;
import games.client.sprites.SpriteGraphics;
import games.client.sprites.SpriteImage;
import games.client.util.DrawElement;
import games.client.util.ImageFactory;
import games.client.util.UIUtils;
import games.shared.Resources;

import com.google.gwt.canvas.dom.client.CssColor;

public class Clock extends Sprite<DrawElement> {
	
	private static SpriteImage _clockImage;
	private static SpriteImage _clockShadowImage;
	private static SpriteImage _sec0Image;
	private static SpriteImage _sec5Image;
	private static SpriteImage _sec10Image;
	private static SpriteImage _sec15Image;
	private static SpriteImage _sec20Image;
	private static SpriteImage _sec25Image;
	private static SpriteImage _sec30Image;
	private static SpriteImage _sec35Image;
	private static SpriteImage _sec40Image;
	private static SpriteImage _sec45Image;
	private static SpriteImage _sec50Image;
	private static SpriteImage _sec55Image;
	
	private final static CssColor clock = CssColor.make(102, 0, 0);
	
	private boolean _isActive = true;
	private SpriteImage _arrowImage;
	private int _arrowX;
	private int _arrowY;
	private long _time;

	static {
//		Color color = new Color (0x66000000);
//		UIUtils.changeColor(_clock, _clock, Color.MAGENTA, color);
//		_clockShadow  = UIUtils.changeColor(_clockShadow, Color.BLACK, SeaBattleView._transparentBlack);
		
		final DrawElement clockElement = UIUtils.changeColorAndTransparency(Resources.get(ImageFactory.class).getDrawElement("clock"),
				255, 0, 255, 0, 0, 0, 0.66d);
		_clockImage = new SpriteImage(clockElement, clockElement.getWidth(), clockElement.getHeight());
		final DrawElement clockShadowElement = UIUtils.changeColorAndTransparency(Resources.get(ImageFactory.class).getDrawElement("clockShadow"),
				0, 0, 0, 0, 0, 0, 0.66d);
		_clockShadowImage = new SpriteImage(clockShadowElement, clockShadowElement.getWidth(), clockShadowElement.getHeight());
		final DrawElement sec0Element = Resources.get(ImageFactory.class).getDrawElement("sec0");
		_sec0Image = new SpriteImage(sec0Element, sec0Element.getWidth(), sec0Element.getHeight());
		final DrawElement sec5Element = Resources.get(ImageFactory.class).getDrawElement("sec5");
		_sec5Image = new SpriteImage(sec5Element, sec5Element.getWidth(), sec5Element.getHeight());
		final DrawElement sec10Element = Resources.get(ImageFactory.class).getDrawElement("sec10");
		_sec10Image = new SpriteImage(sec10Element, sec10Element.getWidth(), sec10Element.getHeight());
		_sec15Image = UIUtils.rotate(sec0Element, sec0Element.getWidth(), sec0Element.getHeight(), UIUtils.ROTATE_90);
		_sec20Image = UIUtils.rotate(sec5Element, sec5Element.getWidth(), sec5Element.getHeight(), UIUtils.ROTATE_90);
		_sec25Image = UIUtils.rotate(sec10Element, sec10Element.getWidth(), sec10Element.getHeight(), UIUtils.ROTATE_90);
		_sec30Image = UIUtils.rotate(sec0Element, sec0Element.getWidth(), sec0Element.getHeight(), UIUtils.ROTATE_180);
		_sec35Image = UIUtils.rotate(sec5Element, sec5Element.getWidth(), sec5Element.getHeight(), UIUtils.ROTATE_180);
		_sec40Image = UIUtils.rotate(sec10Element, sec10Element.getWidth(), sec10Element.getHeight(), UIUtils.ROTATE_180);
		_sec45Image = UIUtils.rotate(sec0Element, sec0Element.getWidth(), sec0Element.getHeight(), UIUtils.ROTATE_270);
		_sec50Image = UIUtils.rotate(sec5Element, sec5Element.getWidth(), sec5Element.getHeight(), UIUtils.ROTATE_270);
		_sec55Image = UIUtils.rotate(sec10Element, sec10Element.getWidth(), sec10Element.getHeight(), UIUtils.ROTATE_270);
	}

	public Clock(final int x, final int y) {
		super(x, y, _clockImage.width(), _clockImage.height());
		
		_arrowImage = _sec0Image;
		_arrowX = 40;
		_arrowY = 47 - _sec0Image.height();

		setImageEngine(new ImageEngine<DrawElement>() {
			
			@Override
			public boolean updateTick(final long timestamp) {
				return false;
			}
			
			@Override
			public <Z> void draw(final SpriteGraphics<Z> graphics, final SpriteImage<Z> image, final int x, final int y, final Rectangle dirtyRegion) {
				_clockImage.draw(graphics, x, y, dirtyRegion);
				_arrowImage.draw(graphics, x + _arrowX, y + _arrowY, dirtyRegion);
				if (!_isActive){
					_clockShadowImage.draw(graphics, x+13, y+13, dirtyRegion);
				}
			}
		});
		
	}
	
	public void setShadowActive(final boolean isActive){
		_isActive = isActive;
	}
	public void start(){
		_time = System.currentTimeMillis();
	}
//	
//	private void setArrowImage(SpriteImage image, int x, int y){
//		_arrowImage = image;
//		_arrowX = x;
//		_arrowY = y;
//	}
//	
//	public void stop() {
//		setArrowImage(_sec0Image, 40, 47 - _sec0Image.height());
//		_time = 0;
//	}
//
//	public void update() {
//		super.update();
//		
//		long time = System.currentTimeMillis();
//		
//		if(_time == 0)
//			return;
//		if(time - _time <= 0)
//			return;
//		
//		int sec = (int)(time - _time) / 100;
//		switch(sec){
//		case 0:
//			break;
//		case 25:
//			setArrowImage(_sec5Image, 40, 47 - _sec5Image.height());
//			break;
//		case 50:
//			setArrowImage(_sec10Image, 40, 47 - _sec10Image.height());
//			break;
//		case 75:
//			setArrowImage(_sec15Image, 40, 47 - _sec15Image.height());
//			break;
//		case 100:
//			setArrowImage(_sec20Image, 40, 40);
//			break;
//		case 125:
//			setArrowImage(_sec25Image, 40, 40);
//			break;
//		case 150:
//			setArrowImage(_sec30Image, 40, 40);
//			break;
//		case 175:
//			setArrowImage(_sec35Image, 47 - _sec35Image.width(), 40);
//			break;
//		case 200:
//			setArrowImage(_sec40Image, 47 - _sec40Image.width(), 40);
//			break;
//		case 225:
//			setArrowImage(_sec45Image, 47 - _sec45Image.width(), 40);
//			break;
//		case 250:
//			setArrowImage(_sec50Image, 47 - _sec50Image.width(), 47 - _sec50Image.height());
//			break;
//		case 275:
//			setArrowImage(_sec55Image, 47 - _sec55Image.width(), 47 - _sec55Image.height());
//			break;
//		case 300:
//			stop();
//			break;
//		}
//	}

}
