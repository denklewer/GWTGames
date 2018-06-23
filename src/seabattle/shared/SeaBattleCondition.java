package seabattle.shared;

public class SeaBattleCondition {

	private static final int SIZE = SeaBattleProtocol.SIZE;
	private int[][] _field1;
	private int[][] _field2;
	private byte _side;
	private int _fieldsSet;
	private byte _winner = 0;

	public SeaBattleCondition(){
		_side = SeaBattleProtocol.WHITE;
		_field1 = new int[SIZE][SIZE];
		_field2 = new int[SIZE][SIZE];
	}
	
	public boolean isMoveValid(SeaBattleMove move) {
		if (isDigit(move._x) && isDigit(move._y) && (_side == move.getSide()))
			return true;
		return false;
	}

	private boolean isDigit(int a){
		if (a > 9 || a < 0){
			return false;
		}
		return true;
	}
	
	public int doMove(SeaBattleMove move) {
		byte side = move.getSide();
		int[][] field = (side == SeaBattleProtocol.BLACK) ? _field1 : _field2;
		
		switch (field[move._x][move._y]) {

		case SeaBattleProtocol.EMPTY:
			field[move._x][move._y] = SeaBattleProtocol.WIDE;
			_side = (_side == SeaBattleProtocol.WHITE) ? SeaBattleProtocol.BLACK : SeaBattleProtocol.WHITE;
			return SeaBattleProtocol.WIDE;
			
		case SeaBattleProtocol.WIDE:
			_side = (_side == SeaBattleProtocol.WHITE) ? SeaBattleProtocol.BLACK : SeaBattleProtocol.WHITE;
			return SeaBattleProtocol.WIDE;

		case SeaBattleProtocol.ALIVE:
			if (isKilled(move._x, move._y, field)) {
				field[move._x][move._y] = SeaBattleProtocol.KILLED;
				makeKilledNeighbours(move._x, move._y, field);
				if (isGameOver(field)){
					_side = SeaBattleProtocol.WHITE;
					return SeaBattleProtocol.GAME_OVER;
				}
				return SeaBattleProtocol.KILLED;
			}
			field[move._x][move._y] = SeaBattleProtocol.SHOT;
			return SeaBattleProtocol.SHOT;

		default:
			return field[move._x][move._y];
		}
	}
	
	public static void makeKilledNeighbours(int i, int j, int[][] field) {
		int k;
		if (checkHorizontalStatus(i, j, field)) {
			for (k = j+1; k < SIZE; k++) {
				if (field[i][k] == SeaBattleProtocol.SHOT)
					field[i][k] = SeaBattleProtocol.KILLED;
				if (field[i][k] == SeaBattleProtocol.WIDE || field[i][k] == SeaBattleProtocol.EMPTY)
					break;
			}
			for (k = j - 1; k >= 0; k--) {
				if (field[i][k] == SeaBattleProtocol.SHOT)
					field[i][k] = SeaBattleProtocol.KILLED;
				if (field[i][k] == SeaBattleProtocol.WIDE || field[i][k] == SeaBattleProtocol.EMPTY)
					break;
			}
		}
		else{
			for (k = i + 1; k < SIZE; k++) {
				if (field[k][j] == SeaBattleProtocol.SHOT)
					field[k][j] = SeaBattleProtocol.KILLED;
				if (field[k][j] == SeaBattleProtocol.WIDE || field[k][j] == SeaBattleProtocol.EMPTY)
					break;
			}
			for (k = i - 1; k >= 0; k--) {
				if (field[k][j] == SeaBattleProtocol.SHOT)
					field[k][j] = SeaBattleProtocol.KILLED;
				if (field[k][j] == SeaBattleProtocol.WIDE || field[k][j] == SeaBattleProtocol.EMPTY)
					break;
			}
		}
	}

	private boolean isKilled(int i, int j, int[][] field) {
		int tmp = 0, k;
		if (checkHorizontalStatus(i, j, field)) {
			for (k = j + 1; k < SIZE; k++) {
				tmp = field[i][k];
				if (tmp == SeaBattleProtocol.ALIVE)
					return false;
				if (tmp == SeaBattleProtocol.WIDE || tmp == SeaBattleProtocol.EMPTY)
					break;
			}
			for (k = j - 1; k >= 0; k--) {
				tmp = field[i][k];
				if (tmp == SeaBattleProtocol.ALIVE)
					return false;
				if (tmp == SeaBattleProtocol.WIDE || tmp == SeaBattleProtocol.EMPTY)
					break;
			}
			return true;
		}
		else{
			for (k = i + 1; k < SIZE; k++) {
				tmp = field[k][j];
				if (tmp == SeaBattleProtocol.ALIVE)
					return false;
				if (tmp == SeaBattleProtocol.WIDE || tmp == SeaBattleProtocol.EMPTY)
					break;
			}
			for (k = i - 1; k >= 0; k--) {
				tmp = field[k][j];
				if (tmp == SeaBattleProtocol.ALIVE)
					return false;
				if (tmp == SeaBattleProtocol.WIDE || tmp == SeaBattleProtocol.EMPTY)
					break;
			}
			return true;
		}
	}

	public static boolean checkHorizontalStatus(int i, int j, int[][] field) {
		if (j > 0 && j < SIZE - 1){
			if (field[i][j + 1] != SeaBattleProtocol.EMPTY && field[i][j + 1] != SeaBattleProtocol.WIDE)
				return true;
			if (field[i][j - 1] != SeaBattleProtocol.EMPTY && field[i][j - 1] != SeaBattleProtocol.WIDE)
				return true;
		}
		if (j == 0 && field[i][j + 1] != SeaBattleProtocol.EMPTY && field[i][j + 1] != SeaBattleProtocol.WIDE)
			return true;
		if (j == SIZE - 1 && field[i][SIZE - 2] != SeaBattleProtocol.EMPTY && field[i][SIZE - 2] != SeaBattleProtocol.WIDE)
			return true;
		return false;
	}
	
	public boolean isGameOver(int[][] field) {

		for (int i = 0; i < SIZE; i++) 
			for (int j = 0; j < SIZE; j++)
				if (field[i][j] == SeaBattleProtocol.ALIVE)
					return false;
		return true;
	}

	public int[][] getField(byte side) {
		int[][] fieldSrc = (side == SeaBattleProtocol.WHITE) ? _field1 : _field2;
		surroundWide(fieldSrc);
		int[][] field = new int[SIZE][SIZE];
		
		for (int i = 0; i < SIZE; i++)
			for (int j = 0; j < SIZE; j++){
				field[i][j] = fieldSrc[i][j];
			}
		return field;
	}

	public static void surroundWide(int[][] field) {
		for (int i = 0; i < SIZE; i++)
			for (int j = 0; j < SIZE; j++)
				if (field[i][j] == SeaBattleProtocol.KILLED){
					makeWidePoint(i - 1, j - 1, field);
					makeWidePoint(i + 1, j - 1, field);
					makeWidePoint(i - 1, j + 1, field);
					makeWidePoint(i + 1, j + 1, field);
					makeWidePoint(i, j - 1, field);
					makeWidePoint(i, j + 1, field);
					makeWidePoint(i - 1, j, field);
					makeWidePoint(i + 1, j, field);
				}
	}
	
	public static boolean CheckComplect(int[][] fieldCome) {
		boolean fourCannons = false,threeCannons[] = new boolean[2],
							twoCannons[] = new boolean[3],
							oneCannon[] = new boolean[4];
		int field[][] = new int[SeaBattleProtocol.SIZE][SeaBattleProtocol.SIZE];
		for (int i = 0; i < SeaBattleProtocol.SIZE; i++)
			for (int j = 0; j < SeaBattleProtocol.SIZE; j++){
				field[i][j] = fieldCome[i][j];
			}
		int l = 0, lBegin = 0;
		for (int i = 0; i < SeaBattleProtocol.SIZE; i++) {
			for (int j = 0; j < SeaBattleProtocol.SIZE; j++) {
				if(field[i][j] == SeaBattleProtocol.ALIVE){
					if(SeaBattleCondition.checkHorizontalStatus(i, j, field)){
						lBegin = l = j;
						while( l< SeaBattleProtocol.SIZE && field[i][l] == SeaBattleProtocol.ALIVE ){
							field[i][l] = SeaBattleProtocol.KILLED;
							l++;
						}
					}
					else {
						lBegin = l = i;
						while( l< SeaBattleProtocol.SIZE && field[l][j] == SeaBattleProtocol.ALIVE ){
							field[l][j] = SeaBattleProtocol.KILLED;
							l++;
						}
					}
				int k;
				switch(l - lBegin){
				case 1:
					k =0;
					if(oneCannon[0] == false)
						oneCannon[0] = true;
					else{
						while(k < 4 && oneCannon[k]==true)
							k++;
						if(k < 4)
						oneCannon[k] = true;
					}
				case 2:
					k =0;
					if(twoCannons[0] == false)
						twoCannons[0] = true;
					else{
						while(k < 3 && twoCannons[k]==true){
							k++;
						}
						if(k < 3)
							twoCannons[k] = true;
					}
					break;
				case 3:
					if(threeCannons[0] == false)
						threeCannons[0] = true;
					else
						threeCannons[1] = true;
					break;
				case 4:
					fourCannons = true;
					break;
				}
				}
			}
		}
		
		if(fourCannons == false || threeCannons[0] ==false || threeCannons[1] == false)
			return false;
		for (int i = 0; i < 3; i++) 
			if(twoCannons[i] == false)
				return false;
		for (int i = 0; i < 4; i++) 
			if(oneCannon[i] == false)
				return false;
		return true;
	}

	public static void makeWidePoint(int i, int j, int[][] field) {
		if (i < SIZE && i >= 0 && j < SIZE && j >= 0 && field[i][j] != SeaBattleProtocol.KILLED)
			field[i][j] = SeaBattleProtocol.WIDE;
	}

	public void setField(int[][] fieldSrc, byte side) {
		int[][] field = (side == SeaBattleProtocol.WHITE)? _field1 : _field2;
		for (int i = 0; i < SIZE; i++)
			for (int j = 0; j < SIZE; j++)
				field[i][j] = fieldSrc[i][j];
		_fieldsSet++;
	}
	
	/*private static void printField(int[][] field) {
		for (int i = 0; i < SIZE; i++){
			for (int j = 0; j < SIZE; j++)
				System.err.print(field[i][j] + " ");
			System.err.println();
		}
	}*/
	

	public boolean allFieldsAreSet() {
		if (_fieldsSet == 2)
			return true;
		return false;
	}

	public byte getWinner() {
		return _winner;
	}

	public byte getSide() {
		return _side;
	}

	public void setSide(byte side) {
		_side = side;
	}

	public void setFieldsSet(int fieldsSet) {
		_fieldsSet = fieldsSet;
	}

	public void setWinner(int side) {
		_winner = (byte) side;
		
	}

}