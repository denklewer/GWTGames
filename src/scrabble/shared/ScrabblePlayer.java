package scrabble.shared;


public class ScrabblePlayer implements ScrabbleProtocol {

	private int _score = 0;
	private int _pass = 0;
	private int _changeLetters = 0;
	protected ScrabbleStand _stand;
	protected byte _side;
	protected String _name;
	
	public ScrabblePlayer() {
	}
	
	public ScrabblePlayer(ScrabbleStand stand, byte side, String name) {
		_stand = stand;
		_side = side;
		_name = name;
	}

	public int getPass() {
		return _pass;
	}
	
	public int getChangeLetters(){
		return _changeLetters;
	}

	public int getScore() {
		return _score;
	}

	public ScrabbleStand getStand() {
		return _stand;
	}

	public void addScore(int score) {
		_score += score;
		if(_score < 0){
			_score = 0;
		}
	}

	public String getName() {
		return _name;
	}

	public byte getSide() {
		return _side;
	}

	public void incPass() {
		_pass++;
	}
	
	public void incChangeLetters() {
		_changeLetters++;
	}

	public void clearPass() {
		_pass = 0;
	}
	
	public void clearChangeLetters() {
		_changeLetters = 0;
	}

	public void setScore(int score) {
		_score = score;
	}

	public void setPass(int pass) {
		_pass = pass;
	}
	
	public void setChangeLetters(int changeLetters) {
		_changeLetters = changeLetters;
	}
	

}
