package scrabble.shared;


public class JokerScrabbleDie extends ScrabbleDie {
	
	private char _selectedLetter;
	
	public JokerScrabbleDie() {
	}
	
	public JokerScrabbleDie(ScrabbleDie die) {
		super(die.getLetter(), die.getPoint());
		if(die instanceof JokerScrabbleDie){
			_selectedLetter = ((JokerScrabbleDie) die).getSelectedLetter();
		}
	}
	
	public void setSelectedLetter(char selectedLetter) {
		_selectedLetter = selectedLetter;
	}

	public char getSelectedLetter() {
		return _selectedLetter;
	}
	
	@Override
	public ScrabbleDie copy() {
		return new JokerScrabbleDie(this);
	}

}
