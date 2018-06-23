package sevendoors.shared;

import games.shared.RandomGenerator;

import java.util.Vector;

public abstract class TileFactory {
	private final BonusProbabilityInfo _bonusProbabilityInfo;
	protected final RandomGenerator _randomGenerator;

	protected abstract Tile generateTile(int scoreOnLastMove, Vector bannedTiles, int level);
	
	public TileFactory(BonusProbabilityInfo bonusProbabilityInfo, RandomGenerator randomGenerator) {
		_bonusProbabilityInfo = bonusProbabilityInfo;
		_randomGenerator = randomGenerator;
	}
	
	protected Tile generateSimpleTile() {
		Tile tile = new Tile((byte) (_randomGenerator.nextInt(Tile.N_TILES)));
		return tile;
	}

	public Vector generateTilesPackage(int size, int lastScore, int level) {
		Vector result = new Vector();
		Vector usedTiles = new Vector();
		for (int i = 0; i < size; i++) {
			Tile tile = generateTile(lastScore, usedTiles, level);
			if (tile.isBonus()) {
				usedTiles.addElement(new Integer(tile.getId()));
			}
			result.addElement(tile);
		}
		return result;
	}
	
	public Vector generateSimpleTilesPackage(int size) {
		Vector result = new Vector();
		for (int i = 0; i < size; i++) {
			result.addElement(generateSimpleTile());
		}
		return result;
	}

	public double getBonusProbability(String bonus, int scoreOnLastMove, int level) {
		return _bonusProbabilityInfo.getBonusProbability(bonus, scoreOnLastMove, level);
	}
	
}
