package scrabble.client;

import games.client.sprites.Rectangle;
import games.client.sprites.Sprite;
import games.client.sprites.SpriteGroup;
import games.client.sprites.SpriteImage;
import games.client.util.DrawElement;
import games.client.util.UIUtils;
import games.shared.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import scrabble.shared.JokerScrabbleDie;
import scrabble.shared.ScrabbleDie;
import scrabble.shared.ScrabbleProtocol;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;

public class ScrabbleStandWidget extends SpriteGroup<DrawElement> {

	private static final int SHIFT_X = 136;
	private static final int SHIFT_Y = 513;
	private DieWidget[] _places = new DieWidget[ScrabbleProtocol.DIES_IN_STAND];
	private List<DieWidget> _dies = new ArrayList<DieWidget>();
	private static final int DICE_INDENT = 4;
	private static final int INTENT_X = 9;
	private ScrabbleView _scrabbleView;

	private static final CssColor BLACK = CssColor.make(0, 0, 0);
	private static final CssColor COLOR_1 = CssColor.make(100, 159, 203);
	private static final CssColor COLOR_2 = CssColor.make(23, 88, 138);
	private static Sprite<DrawElement> BCKGR;

	static {
		final int width = 280;
		final int height = 49;
		final Canvas canvas = UIUtils.createCanvas(width, height);
		final Context2d context = canvas.getContext2d();
		context.setFillStyle(BLACK);
		context.fillRect(0, 0, width - 1, height - 1);
		context.setFillStyle(COLOR_1);
		context.fillRect(0, 0, 5, height);
		context.fillRect(0, 0, width, 3);
		context.fillRect(width - 5, 0, 5, height);
		context.fillRect(0, height - 3, width, 3);
		context.setFillStyle(COLOR_2);
		context.fillRect(5, 3, width - 10, height - 6);
		final DrawElement drawElement = DrawElement.createDrawElement(canvas.getCanvasElement());
		final SpriteImage spriteImage = new SpriteImage(drawElement, width, height);
		BCKGR = new Sprite<DrawElement>(spriteImage, SHIFT_X, SHIFT_Y, width, height);
	}

	private DieWidget _movingDie;
	private Point _mouseDown;
	private Point _startMovingDieCoord;

	public ScrabbleStandWidget(final ScrabbleView parent) {
		_scrabbleView = parent;
	}

	public void init(){
		addSprite(BCKGR);
	}

	public void putWidget(final Sprite<DrawElement> putableWidget) {
		if(putableWidget instanceof JokerDieWidget){
			((JokerDieWidget) putableWidget).resetSelectedLetter();
		}
		addSprite(putableWidget);
	}

	public void addStand(final byte side, final Vector<ScrabbleDie> dice){
		for (int i = 0; i < dice.size(); i++) {
			final DieWidget dieWidget = createDieWidget(dice.elementAt(i));
			int j;
			for (j = i; j < _places.length; j++) {
				if (_places[j] == null) {
					addToPlace(dieWidget, j);
					_dies.add(dieWidget);
					dieWidget.moveTo(getPlaceXCoord(j), getPlaceYCoord());
					addSprite(dieWidget);
					break;
				}
			}
			if (j == _places.length) {
				System.err.println("No free space for dice!");
			}
		}
	}

	private void addToPlace(final DieWidget dieWidget, final int j) {
		_places[j] = dieWidget;
	}

	private int getPlaceYCoord() {
		return 8 + SHIFT_Y;
	}

	private int getPlaceXCoord(final int j) {
		return SHIFT_X + INTENT_X + (DICE_INDENT + ScrabbleCellWidget.CELL_SIZE) * j;
	}

	private DieWidget createDieWidget(final ScrabbleDie die) {
		if(die.getLetter() == '*'){
			return new JokerDieWidget(_scrabbleView, new JokerScrabbleDie(die));
		}
		return new DieWidget(die, _scrabbleView);
	}

	public Vector<ScrabbleDie> getSelectedDices() {
		final Vector<ScrabbleDie> result = new Vector<ScrabbleDie>();
		for (int i = 0; i < _places.length; i++) {
			if (_places[i] != null) {
				if (_places[i].isSelected()) {
					result.addElement(_places[i].getDie());
				}
			}
		}
		return result;
	}

	public void removeSelectedDices() {
		for (int i = 0; i < _places.length; i++) {
			if (_places[i] != null) {
				if (_places[i].isSelected()) {
					removeSprite(_places[i]);
					_places[i] = null;
				}
			}
		}
	}

	public DieWidget getDie(final int x, final int y) {
		for (final DieWidget widget : _places) {
			if(widget != null && widget.contains(x, y)){
				return widget;
			}
		}
		return _scrabbleView.getDieOnBoard(x, y);
	}

	@Override
	public boolean onMouseDown(final int x, final int y, final int modifiers) {
		if(super.onMouseDown(x, y, modifiers)){
			return true;
		}
		if(_scrabbleView.dialogIsVisible()){
			return true;
		}
		_mouseDown = new Point(x, y);
		final DieWidget selectedDie = getDie(x, y);
		_movingDie = selectedDie;
		if(_movingDie != null){
			_startMovingDieCoord = new Point(_movingDie.getX(), _movingDie.getY());
			return true;
		}
		return false;
	}

	@Override
	public boolean onMouseMove(final int x, final int y) {
		if(super.onMouseMove(x, y)){
			return true;
		}
		if(_scrabbleView.dialogIsVisible()){
			return true;
		}
		if(_movingDie != null){
			final int shiftX = x - _mouseDown.x;
			final int shiftY = y - _mouseDown.y;
			toTop(_movingDie);
			_movingDie.moveTo(_startMovingDieCoord.x + shiftX, _startMovingDieCoord.y + shiftY);
			return true;
		}
		return false;
	}

	@Override
	public boolean onMouseUp(final int x, final int y, final int modifiers) {
		final boolean wasVisible = _scrabbleView.dialogIsVisible();
		if(super.onMouseUp(x, y, modifiers)){
			cleanMovingInfo();
			return true;
		}
		if(wasVisible || _scrabbleView.dialogIsVisible()){
			cleanMovingInfo();
			return true;
		}
		if(_movingDie != null){
			final ScrabbleCellWidget cell = _scrabbleView.getCellWidget(x, y);
			final ScrabbleCellWidget movingDieCell = _scrabbleView.getCellWidget(_startMovingDieCoord.x, _startMovingDieCoord.y);
			if(movingDieCell != null){
				movingDieCell.widgetTaken(_movingDie);
			}
			final int placeIndex = getPlaceIndex(x, y);
			if(cell != null && cell.getDieWidget() == null){
				cleanPlace(_movingDie);
				_movingDie.moveTo(cell.getX(), cell.getY());
				cell.widgetPut(_movingDie);
			} else if(placeIndex >= 0 && _places[placeIndex] == null){
				cleanPlace(_movingDie);
				_movingDie.moveTo(getPlaceXCoord(placeIndex), getPlaceYCoord());
				addToPlace(_movingDie, placeIndex);
				if(_movingDie instanceof JokerDieWidget){
					((JokerDieWidget) _movingDie).resetSelectedLetter();
				}
			} else {
				_movingDie.moveTo(_startMovingDieCoord.x, _startMovingDieCoord.y);
				if(movingDieCell != null){
					movingDieCell.widgetPut(_movingDie);
				}
			}
			cleanMovingInfo();
			return true;
		}
		return false;
	}

	public void cleanMovingInfo() {
		_mouseDown = null;
		_movingDie = null;
		_startMovingDieCoord = null;
	}

	private void cleanPlace(final DieWidget movingDie) {
		for (int i = 0; i < _places.length; i++) {
			if(movingDie == _places[i]){
				_places[i] = null;
				return;
			}
		}
	}

	private int getPlaceIndex(final int x, final int y){
		final int placeYCoord = getPlaceYCoord();
		for (int i = 0; i < _places.length; i++) {
			final int placeXCoord = getPlaceXCoord(i);
			final Rectangle rect = new Rectangle(placeXCoord, placeYCoord, ScrabbleCellWidget.CELL_SIZE, ScrabbleCellWidget.CELL_SIZE);
			if(!rect.contains(x, y)){
				continue;
			}
			return i;
		}
		return -1;
	}

	public void removeDies() {
		for (int i = 0; i < _places.length; i++) {
			if(_places[i] != null){
				removeSprite(_places[i]);
				_places[i] = null;
			}
		}
	}


}
