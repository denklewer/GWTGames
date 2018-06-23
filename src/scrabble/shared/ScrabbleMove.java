package scrabble.shared;

import java.util.Vector;

public class ScrabbleMove {
	
	private int _typeOfMove;
	private Vector _cells;
	private byte _side;
	
	public ScrabbleMove() {
	}
	
	public ScrabbleMove(int typeOfMove, Vector dies, Vector cells) {
		_typeOfMove = typeOfMove;
		_cells = cells;
	}


	public Vector getCells() {
		return _cells;
	}

	public int getTypeOfMove() {
		return _typeOfMove;
	}

	public void setSide(byte playerSide) {
		_side = playerSide;
	}

	public byte getSide() {
		return _side;
	}
	
}
