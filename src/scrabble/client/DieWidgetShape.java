package scrabble.client;

import scrabble.shared.ScrabbleDie;

public class DieWidgetShape extends DieWidget {

	public DieWidgetShape(ScrabbleDie die, ScrabbleView parent) {
		super(die, parent);
	}
	
	@Override
	public boolean onMouseMove(int x, int y) {
		return true;
	}
	
	@Override
	public boolean onMouseDown(int x, int y, int modifiers) {
		return true;
	}
	
	@Override
	public boolean onMouseUp(int x, int y, int modifiers) {
		_parent.setJokerValue(getDie().getLetter());
		return true;
	}
	
	@Override
	public boolean onDoubleClick(int x, int y) {
		return false;
	}
}
