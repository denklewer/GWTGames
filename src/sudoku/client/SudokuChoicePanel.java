package sudoku.client;

import games.client.sprites.Sprite;
import games.client.sprites.SpriteGroup;
import games.client.sprites.SpriteImage;
import games.client.util.DrawElement;
import games.client.util.ImageFactory;
import games.client.util.UIUtils;
import games.shared.Resources;

public class SudokuChoicePanel extends SpriteGroup<DrawElement> {

	private static final SpriteImage FIELD; 
	private static final SpriteImage[] DIGITS;
	private static final SpriteImage RUBBER;
	private static final SpriteImage CROSS;

	static {
		final DrawElement fieldElement = Resources.get(ImageFactory.class).getDrawElement("field");
		FIELD = new SpriteImage(fieldElement, fieldElement.getWidth(), fieldElement.getHeight());
		final DrawElement rubberElement = Resources.get(ImageFactory.class).getDrawElement("rubber");
		RUBBER = new SpriteImage(rubberElement, rubberElement.getWidth(), rubberElement.getHeight());
		final DrawElement crossElement = Resources.get(ImageFactory.class).getDrawElement("cross");
		CROSS = new SpriteImage(crossElement, crossElement.getWidth(), crossElement.getHeight());
		final DrawElement digitsElement = Resources.get(ImageFactory.class).getDrawElement("smallDigits");
		DIGITS = UIUtils.splitLetters(digitsElement, digitsElement.getWidth(), digitsElement.getHeight());
	}

	private SudokuView _parent;
	private Cross _cross;
	private Rubber _rubber;
	private Sprite _field;
	private Digit[][] _digits;
	private SudokuFieldElement _currentOwner; 
	private boolean _isOpened = false;

	public SudokuChoicePanel(final SudokuView parent){
		super();
		_parent = parent;
	}

	public void init() {
		addField();
		addNumbers();
		addCrossNRubber();
		hideSprites();
	}

	private void addCrossNRubber() {
		_cross = new Cross();
		addSprite(_cross);

		_rubber = new Rubber();
		addSprite(_rubber);
	}

	private void addNumbers() {
		_digits = new Digit[3][3];
		final int width = 33;
		final int height = 35;
		for(int i = 0; i < 3; i++){
			for(int j = 0; j < 3; j++){
				final SpriteImage digit = DIGITS[i*3 + j];
				final Digit item = new Digit(i*3 + j, digit, width, height);
				_digits[i][j] = item;
				addSprite(item);
			}
		}		
	}

	private void addField() {
		_field = new Sprite(FIELD, 0, 0, FIELD.width(), FIELD.height());
		addSprite(_field);
	}

	protected void choiceMade(final int number){
		_currentOwner.setNumber(number, true, false);
		hideSprites();
		_isOpened = false;
	}

	private class Digit extends Sprite {

		private int _id;

		protected Digit(final int id, final SpriteImage digit, final int width, final int height){
			super(digit, 0, 0, width, height);
			_id = id;
		}

		@Override
		public boolean onMouseUp(final int mousex, final int mousey, final int modifiers) {
			SudokuChoicePanel.this.choiceMade(_id + 1);
			return true;
		}

	}

	private class Cross extends Sprite {

		public Cross() {
			super(CROSS, 0, 0, CROSS.width(), CROSS.height());
		}

		@Override
		public boolean onMouseUp(final int mousex, final int mousey, final int modifiers) {
			SudokuChoicePanel.this.hideSprites();
			SudokuChoicePanel.this._isOpened = false;
			return true;
		}
	}

	private class Rubber extends Sprite {

		public Rubber() {
			super(RUBBER, 0, 0, RUBBER.width(), RUBBER.height());
		}

		@Override
		public boolean onMouseUp(final int mousex, final int mousey, final int modifiers) {
			SudokuChoicePanel.this.choiceMade(0);
			return true;
		}
	}

	private void move(final int x, final int y) {
		_field.moveTo(x, y);
		_cross.moveTo(x + 14, y + 124);
		_rubber.moveTo(x + 50, y + 119);

		final int y0 = 5 + y;
		final int x0 = 5 + x;
		final int width = 33;
		final int height = 35;
		for (int i = 0; i < _digits.length; i++) {
			for (int j = 0; j < _digits[0].length; j++) {
				final SpriteImage digitImage = DIGITS[i*3 + j];
				final int xOffset = (width - digitImage.width()) / 2;
				final int yOffset = (height - digitImage.height()) / 2;
				_digits[i][j].moveTo(x0 + width*i + xOffset, y0 + height*j + yOffset);
			}
		}
	}

	public void show(final SudokuFieldElement sudokuFieldElement) {
		_currentOwner = sudokuFieldElement;
		if(sudokuFieldElement.getY() - 31 + FIELD.height() > 550){
			move(sudokuFieldElement.getX() - 31, 550 - FIELD.height());
		} else {
			move(sudokuFieldElement.getX() - 31, sudokuFieldElement.getY() - 31);
		}
		showSprites();
		_isOpened = true;
	}

	public boolean isOpened() {
		return _isOpened;
	}

}
