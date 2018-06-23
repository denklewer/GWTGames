package sudoku.shared;

import games.shared.Point;

import java.util.Vector;

public class Cell {

	private int _number;

	private boolean[] _littleNumbers = new boolean[9];

	final public static int NONE = 1;

	final public static int TASK = 2;

	final public static int SOLVED = 3;

	final public static int WRONG = 4;

	final public static int LAST = 5;

	private int _cellstyle = Cell.NONE;

	private Point _ColRow = new Point();

	public void setNumber(final int number) {
		_number = number;
	}

	public Cell(final int col, final int row) {
		_ColRow.x = col;
		_ColRow.y = row;
		_number = 0;
		_cellstyle = NONE;
		for (int currLittleNumber = 0; currLittleNumber < 9; currLittleNumber++) {
			_littleNumbers[currLittleNumber] = true;
		}
	}
	
	public Cell(final int col, final int row, final int number){
		this(col, row);
		_number = number;
	}

	public Cell() {
		
	}

	public int getNumber() {
		return _number;
	}

	public boolean[] getLettleNumbers() {
		// vozvrawaet massiv boolean littlenumbers
		return _littleNumbers;
	}

	public int[] getLittleNumbers() {
		// vozvrawaet massiv int littlenumbers
		final int[] tmpint = new int[getLittleNumbersCount()];
		int tmpcount = 0;
		for (int littleNumber = 0; littleNumber < 9; littleNumber++) {
			if (_littleNumbers[littleNumber]) {
				tmpint[tmpcount] = littleNumber + 1;
				tmpcount++;
			}
		}
		return tmpint;
	}

	public Vector getLittleNumbersVector() {
		final Vector tmpvector = new Vector();
		for (int littleNumber = 0; littleNumber < 9; littleNumber++) {
			if (_littleNumbers[littleNumber]) {
				tmpvector.addElement(new Integer(littleNumber + 1));
			}
		}
		return tmpvector;
	}

	public boolean getLittleNumber(final int number) {
		return _littleNumbers[number - 1];
	}

	public boolean isCanBeNumber(final Vector vectorLittleNuber) {
		// esli xotabi odno 4islo is vectorLittleNumbers mogut bit v etom cell
		// to return true
		if (isStyleNone()) {
			for (int tmpNumber = 0; tmpNumber < vectorLittleNuber.size(); tmpNumber++) {
				if (_littleNumbers[((Integer)vectorLittleNuber.elementAt(tmpNumber)).intValue() - 1]) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isCanBeNumberOnly(final Vector vectorLittleNumbers) {
		// esli TOLKO 4isla is vectorLittleNumbers mogut bit v etom cell to
		// return true
		if (isStyleNone()) {
			for (int littleNumber = 1; littleNumber <= 9; littleNumber++) {
				if (_littleNumbers[littleNumber - 1] == true) {
					boolean only = false;
					for (int tmpLittleNumber = 0; tmpLittleNumber < vectorLittleNumbers
							.size(); tmpLittleNumber++) {
						if (littleNumber == ((Integer)vectorLittleNumbers
								.elementAt(tmpLittleNumber)).intValue()) {
							only = true;
						}
					}
					if (!only) {
						return false;
					}
				}
			}
		}
		return true;
	}

	public void setLittleNumbers(final boolean[] LittleNumbers) {
		for (int littleNumber = 0; littleNumber < LittleNumbers.length; littleNumber++) {
			this._littleNumbers[littleNumber] = LittleNumbers[littleNumber];
		}
	}

	public void setLittleNumbers(final int number, final boolean LittleNumber) {
		this._littleNumbers[number - 1] = LittleNumber;
	}

	public void setStyle(final int style) {
		this._cellstyle = style;
	}

	public int getStyle() {
		return _cellstyle;
	}

	public int getLittleNumbersCount() {
		int counter = 0;
		for (int littleNumber = 0; littleNumber < 9; littleNumber++) {
			if (_littleNumbers[littleNumber]) {
				counter++;
			}
		}
		return counter;
	}

	public int getRow() {
		return _ColRow.y;
	}

	public int getCol() {
		return _ColRow.x;
	}

	public void setLittleNumbersNot(final Vector vectorLittleNumbers) {
		// set littlenumbers=false dla vsex 4isel iz vectora
		if (isStyleNone()) {
			for (int tmpLittleNumber = 0; tmpLittleNumber < vectorLittleNumbers
					.size(); tmpLittleNumber++) {
				_littleNumbers[((Integer)vectorLittleNumbers.elementAt(tmpLittleNumber)).intValue() - 1] = false;
			}
		}
	}

	public void setLittleNumbersOnly(final Vector tmpvectorint) {
		// set littlenumbers=true dla vsex 4isel iz vectora, ostalnie vse =
		// false
		if (isStyleNone()) {
			for (int littleNumber = 1; littleNumber <= 9; littleNumber++) {
				_littleNumbers[littleNumber - 1] = false;
			}
			for (int tmpLittleNumber = 0; tmpLittleNumber < tmpvectorint.size(); tmpLittleNumber++) {
				_littleNumbers[((Integer)tmpvectorint.elementAt(tmpLittleNumber)).intValue() - 1] = true;
			}
		}
	}

	public boolean isStyleNone() {
		if (_cellstyle == Cell.NONE) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isStyleTask() {
		if (_cellstyle == Cell.TASK) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isStyleSolved() {
		if (_cellstyle == Cell.SOLVED) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isStyleLast() {
		if (_cellstyle == Cell.LAST) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isStyleWrong() {
		if (_cellstyle == Cell.WRONG) {
			return true;
		} else {
			return false;
		}
	}

	public void clear() {
		_number = 0;
		for (int littleNumber = 0; littleNumber < 9; littleNumber++) {
			_littleNumbers[littleNumber] = true;
		}
		_cellstyle = Cell.NONE;
	}

//	public void pack(DataOutputStream out) throws IOException {
//		out.writeInt(_number);
//		out.writeInt(_cellstyle);
//		out.writeInt(_ColRow.x);
//		out.writeInt(_ColRow.y);
//		out.writeBoolean(_littleNumbers != null);
//		if(_littleNumbers != null){
//			out.writeInt(_littleNumbers.length);
//			for(int i = 0; i < _littleNumbers.length; i++){
//				out.writeBoolean(_littleNumbers[i]);
//			}
//		}
//		
//		
//	}
//
//	public void unpack(DataInputStream in) throws IOException, WrongClassException {
//		_number = in.readInt();
//		_cellstyle = in.readInt();
//		_ColRow = new Point(in.readInt(), in.readInt());
//		if(in.readBoolean()){
//			int lenth = in.readInt();
//			_littleNumbers = new boolean[lenth];
//			for(int i = 0; i < lenth; i++){
//				_littleNumbers[i] = in.readBoolean();
//			}
//		}
//		
//	}

	public boolean equals(final Cell cell) {
		return _number == cell.getNumber();
	}

}
