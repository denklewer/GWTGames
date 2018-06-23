package scrabble.shared;

import java.util.Comparator;

public class CellXComparator implements Comparator<ScrabbleCell> {

	@Override
	public int compare(ScrabbleCell o1, ScrabbleCell o2) {
		int x1 = o1.getCoord().x;
		int x2 = o2.getCoord().x;
		return x1 > x2 ? 1 : x1 == x2 ? 0 : -1;
	}

}
