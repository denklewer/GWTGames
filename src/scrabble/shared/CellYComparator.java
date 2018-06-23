package scrabble.shared;

import java.util.Comparator;

public class CellYComparator implements Comparator<ScrabbleCell> {

	@Override
	public int compare(ScrabbleCell o1, ScrabbleCell o2) {
		int y1 = o1.getCoord().y;
		int y2 = o2.getCoord().y;
		return y1 > y2 ? 1 : y1 == y2 ? 0 : -1;
	}


}
