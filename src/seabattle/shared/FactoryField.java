package seabattle.shared;
import games.shared.Point;

import java.util.Vector;

import com.google.gwt.user.client.Random;

public class FactoryField {

	public static final int SIZE = SeaBattleProtocol.SIZE;
	
	public static int[][] createRandomField(){
		final int[][] field = new int[SIZE][SIZE];
		placeRandomShip(4, field);
		placeRandomShip(3, field);
		placeRandomShip(3, field);
		placeRandomShip(2, field);
		placeRandomShip(2, field);
		placeRandomShip(2, field);
		placeRandomShip(1, field);
		placeRandomShip(1, field);
		placeRandomShip(1, field);
		placeRandomShip(1, field);
		clearWides(field);
		return field;
	}

	private static void clearWides(final int[][] field) {
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				if (field[i][j] == SeaBattleProtocol.WIDE) {
					field[i][j] = SeaBattleProtocol.EMPTY;
				}
			}
		}
	}

	private static void placeRandomShip(final int numberOfCannons, final int[][] field) {
		final boolean horizontallyPlaced = (Math.abs(Random.nextInt()) % 2 == 0) ? true : false;
		int nSizeX = SIZE, nSizeY = SIZE;
		int x, y;
		
		if(horizontallyPlaced) {
			nSizeY = SIZE - numberOfCannons + 1;
		} else {
			nSizeX = SIZE - numberOfCannons + 1;
		}
		
		if (numberOfCannons == 4){
			x = Math.abs(Random.nextInt()) % nSizeX;
			y = Math.abs(Random.nextInt()) % nSizeY;
		}
		else {
			final Vector p = new Vector();
			
			for (int i = 0; i < nSizeX; i++) {
				for (int j = 0; j < nSizeY; j++) {
					if (isAvailable(i, j, field, numberOfCannons,
							horizontallyPlaced)) {
						p.addElement(new Point(i, j));
					}
				}
			}
			final int index = Math.abs(Random.nextInt()) % p.size();
			x = ((Point)p.elementAt(index)).x;
			y = ((Point)p.elementAt(index)).y;
		}
		if (horizontallyPlaced) {
			for (int k = y; k < y + numberOfCannons; k++) {
				field[x][k] = SeaBattleProtocol.ALIVE;
			}
		} else {
			for (int k = x; k < x + numberOfCannons; k++) {
				field[k][y] = SeaBattleProtocol.ALIVE;
			}
		}
		surroundFieldWides(field);
	}

	private static boolean isAvailable(final int i, final int j, final int[][] field, final int numberOfCannons, final boolean horizontallyPlaced) {
		
		if (horizontallyPlaced){
			for (int k = j; k < j + numberOfCannons; k++) {
				if (field[i][k] == SeaBattleProtocol.ALIVE || field[i][k] == SeaBattleProtocol.WIDE) {
					return false;
				}
			}
		}
		
		else{
			for (int k = i; k < i + numberOfCannons; k++) {
				if (field[k][j] == SeaBattleProtocol.ALIVE || field[k][j] == SeaBattleProtocol.WIDE) {
					return false;
				}
			}
		}
		return true;
	}

	private static void surroundFieldWides(final int[][] field) {
		
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				if (field[i][j] == SeaBattleProtocol.ALIVE){
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
		}
	}

	private static void makeWidePoint(final int i, final int j, final int[][] field) {
		if (i < SIZE && i >= 0 && j < SIZE && j >= 0 && field[i][j] != SeaBattleProtocol.ALIVE) {
			field[i][j] = SeaBattleProtocol.WIDE;
		}
	}
	
}
