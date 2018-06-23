package scrabble.client;

import games.client.sprites.ImageEngine;
import games.client.sprites.Rectangle;
import games.client.sprites.Sprite;
import games.client.sprites.SpriteGraphics;
import games.client.sprites.SpriteImage;
import games.client.util.DrawElement;
import games.client.util.ImageFactory;
import games.client.util.Tool;
import games.client.util.UIUtils;
import games.shared.Resources;

import java.util.Vector;

import scrabble.shared.ScrabbleCell;
import scrabble.shared.ScrabbleDie;
import scrabble.shared.ScrabbleProtocol;

import com.google.gwt.dom.client.NativeEvent;

public class DieWidget extends Sprite<DrawElement> {
	
	public static final int CELL_SIZE = 34;
	static private SpriteImage[] _alphabet = UIUtils.splitLetters(Resources.get(ImageFactory.class).getDrawElement("alphabet-01"), 492, 25);
	static private SpriteImage[] _bgImages = UIUtils.cutSpriteVertically(Resources.get(ImageFactory.class).getDrawElement("box-01"), CELL_SIZE, 5 * CELL_SIZE, 5);
	static private SpriteImage[] _digits = UIUtils.splitLetters(Resources.get(ImageFactory.class).getDrawElement("score-alph-01"), 48, 6);
	static private SpriteImage _borderImage = new SpriteImage(Resources.get(ImageFactory.class).getDrawElement("select-01"), CELL_SIZE, CELL_SIZE);
	
	private ScrabbleDie _die;
	protected boolean _isJoined;
	private int _bgImageNum;
	private boolean _isSelected;
	protected final ScrabbleView _parent;
	private boolean _mousePressed;

	public DieWidget(final ScrabbleDie die, final ScrabbleView parent) {
		this(parent, die, 0, 0);
	}
	
	public DieWidget(final ScrabbleView parent, final ScrabbleDie die, final int x, final int y) {
		super(x, y, CELL_SIZE, CELL_SIZE);
		_parent = parent;
		_die = die;
		
		setImageEngine(new ImageEngine<DrawElement>() {
			
			@Override
			public boolean updateTick(final long timestamp) {
				return false;
			}
			
			@Override
			public <Z> void draw(final SpriteGraphics<Z> graphics, final SpriteImage<Z> image,
					final int x, final int y, final Rectangle dirtyRegion) {
				_bgImages[_bgImageNum].draw(graphics, x, y, dirtyRegion);
				final int shiftX = (ScrabbleCellWidget.CELL_SIZE - getDieImage().width()) / 2;
				final int shiftY = (ScrabbleCellWidget.CELL_SIZE - getDieImage().height()) / 2 - 2;
				getDieImage().draw(graphics, x + shiftX, y + shiftY, dirtyRegion);
				drawDigits(_die.getPoint(), graphics, x, y, dirtyRegion);
				if (_isSelected) {
					_borderImage.draw(graphics, x, y, dirtyRegion);
				}
			}
		});
	}

	protected SpriteImage getDieImage(final char letter) {
		return _alphabet[letter - 'а'];
	}
	
	protected SpriteImage getDieImage() {
		return getDieImage(_die.getLetter());
	}

	private void drawDigits(final int point, final SpriteGraphics graphics, final int x, final int y, final Rectangle dirtyRegion) {
		final Vector<Integer> digits = Tool.getDigits(point);
		for (int i = 0; i < digits.size(); i++) {
			final int digit = (digits.elementAt(i)).intValue();
			final int shiftX = 3 + i * (_digits[0].width() + 1);
			final int shiftY = getHeight() - _digits[0].height() - 3;
			_digits[(digit + 9) % 10].draw(graphics, x + shiftX, y + shiftY, dirtyRegion);
		}
	}
	
	@Override
	public boolean onMouseUp(final int x, final int y, final int modifiers) {
		_mousePressed = false;
		if(!_parent.isActive()){
			return true;
		}
		if (_isJoined) {
			return true;
		}
		return false;
	}
	
	@Override
	public boolean onMouseDown(final int x, final int y, final int modifiers) {
		_mousePressed = true;
		if(!_parent.isActive()){
			return true;
		}
		if (_isJoined){
			return true;
		}
		_parent.toTop(this);
		if(modifiers == NativeEvent.BUTTON_RIGHT){
			setSelected(!_isSelected);
			return true;
		}
		_bgImageNum = 0;
		return false;
	}
	
	@Override
	public boolean onDoubleClick(final int x, final int y) {
		if(!_parent.isActive()){
			return true;
		}
		if (_isJoined){
			return true;
		}
		setSelected(!_isSelected);
		return true;
	}

	public void setJoined(final boolean joined) {
		_isJoined = joined;
	}
	
	@Override
	public boolean onMouseMove(final int x, final int y) {
		if(!_parent.isActive()){
			return true;
		}
		if (_isJoined) {
			return true;
		}
		if (_isSelected && _mousePressed) {
			setSelected(false);
		}
		return false;
	}

	private void setSelected(final boolean isSelected) {
		_isSelected = isSelected;
		_parent.handleBusEvent();
		markDirty();
	}

	public ScrabbleDie getDie() {
		return _die;
	}

	public void setBg(final ScrabbleCell cell) {
		if (cell.getTypeOfBonus() == ScrabbleProtocol.BONUS_LETTER) {
			_bgImageNum = cell.getPoint() - 1;
		} else if (cell.getTypeOfBonus() == ScrabbleProtocol.BONUS_WORD) {
			_bgImageNum = cell.getPoint() + 1;
		} else {
			_bgImageNum = 0;
		}
	}

	public boolean isSelected() {
		return _isSelected;
	}

	public boolean isJoined() {
		return _isJoined;
	}
}
