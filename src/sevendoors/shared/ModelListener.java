package sevendoors.shared;

import games.shared.BaseModelListener;
import games.shared.Point;

import java.util.Vector;

public interface ModelListener extends BaseModelListener{

	void gameFilled(boolean hideMenu);

	void tilesSwapped(int x1, int y1, int x2, int y2);

	void markTilesToDelete(BoardMask copy);

	void addTiles(Vector tiles, BoardMask copy);

	void setScore(long score);

	void secretStatusChanged(boolean solved);

	void setSecret(int secretNum, int[][] secret);

	void addKey();

	void jungleGrow(Point grow);

	void numOfMachete(int numOfMachete);

	void showUseMachete(Point point);

	void nextLevel(int level);

	void snakesActivated(int x, int y, BoardMask deletedMask);

	void lianasChangedToForest(Vector deletedLianas);

	void waterfallSpread(BoardMask waterMask);

	void gameFinished();

	void noMoreMoves();

	void moveAndAdd(TilesFallMask fallMask, BoardMask waterMask,
			int waterX, Vector<Tile> tiles, BoardMask deletedMask);

	void storeGame(SevenDoorsState sevenDoorsState);

	void storeScore(long score);

	void resetStoredGame();

	void showHint(SevenDoorsMove move);

	void stopShowHint();

}
