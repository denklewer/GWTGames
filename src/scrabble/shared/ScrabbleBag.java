package scrabble.shared;

import games.shared.RandomGenerator;

import java.util.Vector;

public class ScrabbleBag {
	
	private Vector<ScrabbleDie> _dice;
	private final RandomGenerator _randomGenerator;
	
	public ScrabbleBag(Vector<ScrabbleDie> dice, RandomGenerator randomGenerator) {
		_dice = dice;
		_randomGenerator = randomGenerator;
	}
	
	public void put(ScrabbleDie die) {
		_dice.addElement(die);
	}
	
	public ScrabbleDie take() {
		if (_dice.size() == 0) {
			return null;
		} else {
			int index = Math.abs(_randomGenerator.nextInt(_dice.size()));
			ScrabbleDie result = (ScrabbleDie) _dice.elementAt(index);
			_dice.removeElementAt(index);
			return result;
		}
	}
	
	public void addJokers(int numOfJokers){
		for (int j = 0; j < numOfJokers; j++) {
			_dice.add(new ScrabbleDie('*', 0));
		}
	}

	public void addDice(Vector<ScrabbleDie> dice) {
		for (int i = 0; i < dice.size(); i++) {
			_dice.addElement(dice.elementAt(i));
		}
	}
	
	public ScrabbleBag copy(){
		Vector<ScrabbleDie> dice = null;
		if(_dice != null){
			dice = new Vector<ScrabbleDie>();
			for (int i = 0; i < _dice.size(); i++) {
				dice.add(_dice.get(i).copy());
			}
		}
		return new ScrabbleBag(dice, _randomGenerator);
	}
	
	public int size(){
		return _dice.size();
	}

}
