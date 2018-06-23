package scrabble.shared;

import java.util.Comparator;

public class ScrabblePlayerComparator implements Comparator<ScrabblePlayer> {

	@Override
	public int compare(ScrabblePlayer player1, ScrabblePlayer player2) {
		if(player1.getScore() == player2.getScore()){
			return 0;
		}
		if(player1.getScore() > player2.getScore()){
			return 1;
		}
		return -1;
	}

}
