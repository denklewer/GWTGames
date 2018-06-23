package scrabble.shared;

import games.shared.Point;


public class ScrabbleCell {
	
	private int _x;
	private int _y;
	private int _typeOfBonus;
	private int _point;
	private ScrabbleDie _die;
	private boolean _isNew;
	
	public ScrabbleCell() {
	}
	
	public ScrabbleCell(final int x, final int y, final int type, final int point) {
		_x = x;
		_y = y;
		_typeOfBonus = type;
		_point = point;
	}
	
	@Override
	public boolean equals(final Object obj) {
		final ScrabbleCell cell = (ScrabbleCell) obj;
		if (_x == cell._x && _y == cell._y) {
			return true;
		}
		return false;
	}

	public int getPoint() {
		return _point;
	}
	
	public int getTypeOfBonus() {
		return _typeOfBonus;
	}
	
	public void putDie(final ScrabbleDie die) {
		_die = die;
	}
	
	public boolean isBusy() {
		return _die != null;
	}

	public ScrabbleDie getDie() {
		return _die;
	}
	
	public void setNew(final boolean value){
		_isNew = value;
	}
	
	public boolean isNew(){
		return _isNew;
	}
	
	public Point getCoord() {
		return new Point(_x, _y);
	}
	
	public ScrabbleDie takeDie(){
		final ScrabbleDie die = getDie();
		_die=null;
		return die;
	}

	public ScrabbleCell copy() {
		final ScrabbleCell cell = new ScrabbleCell(_x, _y, _typeOfBonus, _point);
		cell.setNew(_isNew);
		if(_die != null){
			cell.putDie(_die.copy());
		}
		return cell;
	}
}
