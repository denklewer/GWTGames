package seabattle.shared;

import com.google.gwt.user.client.Random;

public class SeaBattleRobot implements SeaBattleProtocol{
	
	private byte _side;
	private int[][] _fieldOpp;
	
	public SeaBattleRobot(){
		_fieldOpp = new int[SIZE][SIZE];
		for (int i = 0; i < SIZE; i++)
			for (int j = 0; j < SIZE; j++)
				_fieldOpp[i][j] = EMPTY;
	}

	public int[][] getRandomField(){
		int[][] field;
		field = FactoryField.createRandomField();
		//printField(field);
		return field;
	}
	
//	private static void printField(int[][] field) {
//		for (int i = 0; i < SIZE; i++){
//			for (int j = 0; j < SIZE; j++)
//				System.out.print(field[i][j] + " ");
//			System.out.println();
//		}
//	}
	
	public SeaBattleMove doMove(byte playerSide){
		int x = 0, y = 0;

//		TODO
//		if (playerSide == _side) {
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
		
		for (int i = 0; i < SIZE; i++)
			for (int j = 0; j < SIZE; j++)
				if (_fieldOpp[i][j] == SHOT){
					if(ShotNear(i, j)){
						if(checkHorizontalStatus(i, j)){
							int l = j;
							while(l < SIZE - 1 && _fieldOpp[i][l] == SHOT)
								l++;
							if(_fieldOpp[i][l] != EMPTY)
								return new SeaBattleMove(i, j - 1, playerSide);
							else
								return new SeaBattleMove(i, l, playerSide);
						}
						else{
							int l=i;
							while(l < SIZE-1 && _fieldOpp[l][j] == SHOT)
								l++;
							if(_fieldOpp[l][j] != EMPTY)
								return new SeaBattleMove(i-1, j,playerSide);
							else
								return new SeaBattleMove(l, j,playerSide);
						}
					}
					else{
						int counter=0;
						if(i+1<SIZE && _fieldOpp[i+1][j] == EMPTY)
							counter++;
						if(i-1>=0 && _fieldOpp[i-1][j] == EMPTY)
							counter++;
						if(j+1<SIZE && _fieldOpp[i][j+1] == EMPTY)
							counter++;
						if(j-1>=0 && _fieldOpp[i][j-1] == EMPTY)
							counter++;
						switch(Math.abs(Random.nextInt(counter))){
						case 0:
							if(i+1<SIZE && _fieldOpp[i+1][j] == EMPTY)
								return new SeaBattleMove(i+1, j,playerSide);
							if(i-1>=0 && _fieldOpp[i-1][j] == EMPTY)
								return new SeaBattleMove(i-1, j,playerSide);
							if(j+1<SIZE && _fieldOpp[i][j+1] == EMPTY)
								return new SeaBattleMove(i, j+1,playerSide);
							if(j-1>=0 && _fieldOpp[i][j-1] == EMPTY)
								return new SeaBattleMove(i, j-1,playerSide);
							break;
						case 1:
							if(j-1>=0 && _fieldOpp[i][j-1] == EMPTY)
								return new SeaBattleMove(i, j-1, playerSide);
							if(i+1<SIZE && _fieldOpp[i+1][j] == EMPTY)
								return new SeaBattleMove(i+1, j, playerSide);
							if(i-1>=0 && _fieldOpp[i-1][j] == EMPTY)
								return new SeaBattleMove(i-1, j, playerSide);
							if(j+1<SIZE && _fieldOpp[i][j+1] == EMPTY)
								return new SeaBattleMove(i, j+1, playerSide);
							break;
						case 2:
							if(j+1<SIZE && _fieldOpp[i][j+1] == EMPTY)
								return new SeaBattleMove(i, j+1, playerSide);
							if(j-1>=0 && _fieldOpp[i][j-1] == EMPTY)
								return new SeaBattleMove(i, j-1, playerSide);
							if(i+1<SIZE && _fieldOpp[i+1][j] == EMPTY)
								return new SeaBattleMove(i+1, j, playerSide);
							if(i-1>=0 && _fieldOpp[i-1][j] == EMPTY)
								return new SeaBattleMove(i-1, j, playerSide);
							break;
						case 3:
							if(i-1>=0 && _fieldOpp[i-1][j] == EMPTY)
								return new SeaBattleMove(i-1, j, playerSide);
							if(j+1<SIZE && _fieldOpp[i][j+1] == EMPTY)
								return new SeaBattleMove(i, j+1, playerSide);
							if(j-1>=0 && _fieldOpp[i][j-1] == EMPTY)
								return new SeaBattleMove(i, j-1, playerSide);
							if(i+1<SIZE && _fieldOpp[i+1][j] == EMPTY)
								return new SeaBattleMove(i+1, j, playerSide);
							break;
						}
					}	
				}
		do{
			x = Math.abs(Random.nextInt()) % 10;
			y = Math.abs(Random.nextInt()) % 10;
		}while(_fieldOpp[x][y] != EMPTY);
		
//		int time = Math.abs(r.nextInt()) % 8 + 2;
//		
		
		return new SeaBattleMove(x, y, playerSide);
	}
	
	private boolean ShotNear(int i, int j) {
		if(i+1<SIZE && _fieldOpp[i+1][j] == SHOT)
			return true;
		if(i-1>=0 && _fieldOpp[i-1][j] == SHOT)
			return true;
		if(j+1<SIZE && _fieldOpp[i][j+1] == SHOT)
			return true;
		if(j-1>=0 && _fieldOpp[i][j-1] == SHOT)
			return true;
		return false;
	}

	private boolean checkHorizontalStatus(int i, int j) {
		if (j > 0 && j < SIZE - 1){
			if (_fieldOpp[i][j + 1] != EMPTY && _fieldOpp[i][j + 1] != WIDE)
				return true;
			if (_fieldOpp[i][j - 1] != EMPTY && _fieldOpp[i][j - 1] != WIDE)
				return true;
		}
		if (j == 0 && _fieldOpp[i][j + 1] != EMPTY && _fieldOpp[i][j + 1] != WIDE)
			return true;
		if (j == SIZE - 1 && _fieldOpp[i][SIZE - 2] != EMPTY && _fieldOpp[i][SIZE - 2] != WIDE)
			return true;
		return false;
	}
	
	public void setChangedField(int[][] field){
		for (int i = 0; i < SIZE; i++)
			for (int j = 0; j < SIZE; j++)
				_fieldOpp[i][j] = field[i][j];
	}

	public byte getSide() {
		return _side;
	}

	public void setSide(byte side) {
		_side = side;
	}
}
