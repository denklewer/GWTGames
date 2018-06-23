package sudoku.client;

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
import sudoku.shared.Move;

public class SudokuFieldElement extends Sprite<DrawElement> {

	private static SpriteImage[] STATIC_DIGITS;
	private static SpriteImage[] EDITABLE_DIGITS;
	private static SpriteImage TILE_BACKGROUND;

	static {
		final DrawElement staticDigits = Resources.get(ImageFactory.class).getDrawElement("staticDigits");
		STATIC_DIGITS = UIUtils.splitLetters(staticDigits, staticDigits.getWidth(), staticDigits.getHeight());
		final DrawElement editableDigits = Resources.get(ImageFactory.class).getDrawElement("editableDigits");
		EDITABLE_DIGITS = UIUtils.splitLetters(editableDigits, editableDigits.getWidth(), editableDigits.getHeight());
		final DrawElement tileBg = Resources.get(ImageFactory.class).getDrawElement("tileBg");
		TILE_BACKGROUND = new SpriteImage(tileBg, tileBg.getWidth(), tileBg.getHeight());
	}

	private boolean _isEditable = true;
	private int _number = 0;
	private Point _index;
	private SudokuView _parent;

	public SudokuFieldElement(final SudokuView parent, final Point point, final int x0, final int y0) {
		super(TILE_BACKGROUND, x0 + TILE_BACKGROUND.width() * point.y, y0 + TILE_BACKGROUND.height() * point.x, TILE_BACKGROUND.width(),
				TILE_BACKGROUND.height());
		_index = point;
		_parent = parent;

		setImageEngine(new ImageEngine<DrawElement>() {
			@Override
			public boolean updateTick(final long timestamp) {
				return false;
			}

			@Override
			public <Z> void draw(final SpriteGraphics<Z> graphics, final SpriteImage<Z> image, final int x, final int y, final Rectangle dirtyRegion) {
				TILE_BACKGROUND.draw(graphics, x, y, dirtyRegion);
				if (_number > 0) {
					final SpriteImage digit = _isEditable ? EDITABLE_DIGITS[_number - 1] : STATIC_DIGITS[_number - 1];
					digit.draw(graphics, x + (getWidth() - digit.width()) / 2, y + (getHeight() - digit.height()) / 2, dirtyRegion);
				}
			}
		});

	}

	public void setNumber(final int number, final boolean add2History, final boolean taskSetting) {
		if (!_isEditable) {
			return;
		}
		if (add2History) {
			_parent.addHistoryPoint(_index, _number, number);
		}
		_number = number;
		markDirty();
		if (!taskSetting) {
			_parent.newMove(new Move(_index, _number, number));
		}
		_parent.checkFieldIfComplete();
	}

	public int getNumber() {
		return _number;
	}

	public boolean containsNumber() {
		return _number > 0;
	}

	public void setStaticNumber(final int number) {
		_isEditable = !(number > 0);
		_number = number;
		markDirty();
	}

	@Override
	public boolean onMouseUp(final int mousex, final int mousey, final int modifiers) {
		if (_isEditable && !_parent.getChoicePanel().isOpened()) {
			_parent.showChoicePanel(this);
		}
		return true;
	}

}
