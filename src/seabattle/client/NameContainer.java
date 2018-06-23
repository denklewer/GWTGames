package seabattle.client;

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

import com.google.gwt.canvas.dom.client.CssColor;

public class NameContainer extends SpriteGroup<DrawElement> {

	public static final DrawElement FIELD_DRAWELEMENT;
	private static final SpriteImage MARKER;
	private static final SpriteImage SHADOW;

	final String BOLD_14PX_SANS_SERIF = "bold 14px sans-serif";
	final CssColor BLACK_COLOR = CssColor.make(0, 0, 0);
	final CssColor WHITE_COLOR = CssColor.make("rgba(" + 255 + ", " + 255 + "," + 255 + ", " + 0.0d + ")");
	final static CssColor GRAY_COLOR = CssColor.make(177, 173, 161);

	// public static final CssColor _transparentBlack = new Color (0, 0, 0,
	// 0x66);
	// public final static Color magenta = new Color(255, 0, 255);
	// public final static Color black = new Color(0, 0, 0);

	static {
		FIELD_DRAWELEMENT = UIUtils.changeColorAndTransparency(Resources.get(ImageFactory.class).getDrawElement("nameField"),
				255, 0, 255, 0, 0, 0, 0.66d);
//		FIELD = new SpriteImage(field, field.getWidth(), field.getHeight());
		final DrawElement marker = Resources.get(ImageFactory.class).getDrawElement("marker");
		MARKER = new SpriteImage(marker, marker.getWidth(), marker.getHeight());
		final DrawElement shadow = UIUtils.changeColorAndTransparency(Resources.get(ImageFactory.class).getDrawElement("shadow"), 
				0, 0, 0, 0, 0, 0, 0.66d);
		SHADOW = new SpriteImage(shadow, shadow.getWidth(), shadow.getHeight());

	}

	private Sprite _field;
	private Sprite _nameSprite;
	private boolean _withShadow = false;
	private String _name = "lalala";
	private SeaBattleView _parent;
	private static int _x;
	private static int _y;
	private int _width;
	private int _height;
	private Sprite _shadow;

	public NameContainer(final SeaBattleView parent, final int x, final int y, final int width, final int height) {
		_parent = parent;
//		super(x, y, 183, 58);
		_x = x;
		_y = y;
		_width = width;
		_height = height;

	}

	public void init() {
		_field = createFieldImage(FIELD_DRAWELEMENT, _x, _y, _width, _height);
		addSprite(_field);
		final Sprite markerSprite = new Sprite(MARKER, _x+19, _y+20, MARKER.width(), MARKER.height());
		addSprite(markerSprite);
		
		final DrawElement nameElement = UIUtils.createLeftTextElement(_name, _width, _height, BLACK_COLOR, WHITE_COLOR, 0d, BOLD_14PX_SANS_SERIF, 14);
		final SpriteImage nameImage = new SpriteImage<DrawElement>(nameElement, nameElement.getWidth(), nameElement.getHeight());
		_nameSprite = new Sprite(nameImage, _x + 38, _y - 5, nameElement.getWidth(), nameElement.getHeight());
		addSprite(_nameSprite);
		_shadow = new Sprite(SHADOW, _x + 11, _y + 11, SHADOW.width(), SHADOW.height());
//		addSprite(shadow);
	}

	public void setText(final String text) {
		_name = text;
		final DrawElement nameElement = UIUtils
				.createLeftTextElement(_name, _field.getWidth(), _field.getHeight(), BLACK_COLOR, WHITE_COLOR, 0d, BOLD_14PX_SANS_SERIF, 14);
		_nameSprite.setSpriteImage(new SpriteImage<DrawElement>(nameElement, nameElement.getWidth(), nameElement.getHeight()));
	}
	
	public String getText(){
		return _name;
	}

	public void makeShadow(){
		addSprite(_shadow);
	}
	
	public void removeShadow() {
		removeSprite(_shadow);
	}
	
	public static Sprite createFieldImage(final DrawElement fieldImage, final int x, final int y, 
			final int w, final int h) {
//		 DrawElement element = Resources.get(ImageFactory.class).getDrawElement("nameField");
		 
//		 SpriteImage image1_0 = new SpriteImage(fieldImage, 0, 0, 56, 26);	
//		 SpriteImage image1_2 = new SpriteImage(fieldImage, 0, 27, 56, 31);
		 final SpriteImage image2_0 = new SpriteImage(fieldImage, 0, 0, 25, 26);
		 final SpriteImage image2_1 = new SpriteImage(fieldImage, 25, 0, 1, 26);
		 final SpriteImage image2_2 = new SpriteImage(fieldImage, 26, 0, 30, 26);
		 final SpriteImage image3_0 = new SpriteImage(fieldImage, 0, 26, 25, 31);
		 final SpriteImage image3_1 = new SpriteImage(fieldImage, 25, 26, 1, 31);
		 final SpriteImage image3_2 = new SpriteImage(fieldImage, 26, 26, 30, 31);
		 final SpriteImage image4_2 = new SpriteImage(fieldImage, 0, 25, 26, 1);
		 final SpriteImage image5_2 = new SpriteImage(fieldImage, 26, 25, 30, 1);
		 
		 final Sprite field = new Sprite(x, y, w, h);
		 field.setImageEngine(new ImageEngine<DrawElement>() {

			@Override
			public boolean updateTick(final long timestamp) {
				return false;
			}

			@Override
			public <Z> void draw(final SpriteGraphics<Z> graphics, final SpriteImage<Z> image, final int x, final int y, final Rectangle dirtyRegion) {
				final DrawElement rect = UIUtils.createRect(w - image2_0.width() - image3_2.width(), h - image2_0.height()
						- image3_2.height(), GRAY_COLOR);
				final SpriteImage rectImage = new SpriteImage(rect, rect.getWidth(), rect.getHeight());
				rectImage.draw(graphics, x+image2_0.width(), y+image2_0.height(), rect.getWidth(), rect.getHeight(), dirtyRegion);
				
				image2_0.draw(graphics, x, y, dirtyRegion);
				for(int i = image2_0.width(); i < (w - image2_2.width()); i++){
					image2_1.draw(graphics, x+i, y, dirtyRegion);
					image3_1.draw(graphics, x+i, y+h - image3_1.height() - 1, dirtyRegion);
				}
				image2_2.draw(graphics, x+w - image2_2.width(), y, dirtyRegion);
				
				image3_0.draw(graphics, x, y+h - image3_0.height() - 1, dirtyRegion);
//				 				
				for(int i = image2_0.height(); i < (h - image3_0.height() - 1); i++){
					image4_2.draw(graphics, x, y+i, dirtyRegion);
					image5_2.draw(graphics, x+w - image5_2.width(), y+i, dirtyRegion);
				}
//				
				image3_2.draw(graphics, x+w - image3_2.width(), y+ h - image3_2.height() - 1, dirtyRegion);
			}
			 
		 });
		
		return field;
	}

}
