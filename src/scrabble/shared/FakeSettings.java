package scrabble.shared;

public class FakeSettings implements ScrabbleSettings {

	@Override
	public int numOfJokers() {
		return 2;
	}

	@Override
	public boolean isClassicGame() {
		return true;
	}

}
