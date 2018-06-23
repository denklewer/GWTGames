package scrabble.shared;


public class ScrabbleDie {
	
	private char _letter;
	private int _point;
	private boolean _isSelected;
	
	public ScrabbleDie() {
	}
	
	public ScrabbleDie(char letter, int point) {
		_letter = letter;
		_point = point;
	}
	
	public boolean equals(Object obj) {
		ScrabbleDie die = (ScrabbleDie) obj;
		if (_letter == die._letter) {
			return true;
		}
		return false;
	}

	public boolean isSelected() {
		return _isSelected;
	}

	public void setSelected(boolean isSelected) {
		_isSelected = isSelected;
	}

	public char getLetter() {
		return _letter;
	}
	
	public int getPoint() {
		return _point;
	}

	public ScrabbleDie copy() {
		ScrabbleDie die = new ScrabbleDie(_letter, _point);
		die.setSelected(_isSelected);
		return die;
	}
}
