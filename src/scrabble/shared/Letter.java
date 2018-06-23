package scrabble.shared;

public class Letter {
	private char _letter;
	private int _frequency;

	public Letter(char letter, int frequency) {
		_frequency = frequency;
		_letter = letter;
	}

	public char getLetter() {
		return _letter;
	}

	public void increaseFrequency() {
		_frequency++;
	}

	public int getFrequency() {
		return _frequency;
	}
}
