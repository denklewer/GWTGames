package scrabble.shared;

import java.util.Vector;

public class ScrabbleStand implements ScrabbleProtocol {
	private Vector<ScrabbleDie> _diceOfGamer;
	private ScrabbleBag _bag;
	
	public ScrabbleStand(ScrabbleBag bag) {
		_diceOfGamer = new Vector<ScrabbleDie>(DIES_IN_STAND);
		_bag = bag;
		fillScrabbleStand();
	}

	public ScrabbleStand() {
	}

	public Vector<ScrabbleDie> fillScrabbleStand() {
		Vector<ScrabbleDie> result = new Vector<ScrabbleDie>();
		int numOfDiesToAdd = DIES_IN_STAND - _diceOfGamer.size();
		for (int i = 0; i < numOfDiesToAdd; i++) {
			ScrabbleDie die = _bag.take();
			if (die == null) {
				break;
			} else {
				_diceOfGamer.addElement(die);
				result.addElement(die);
			}
		}
		return result;
	}

	public Vector<ScrabbleDie> getDiceOfGamer(){
		return _diceOfGamer;
	}
	
	public void removeDice(Vector<ScrabbleDie> dice) {
		for (int i = 0; i < dice.size(); i++) {
			ScrabbleDie die = dice.elementAt(i);
			_diceOfGamer.removeElement(die);
		}
	}
	
	public void returnStandToBag() {
		_bag.addDice(_diceOfGamer);
		_diceOfGamer.removeAllElements();
	}
	
}
