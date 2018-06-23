package sevendoors.shared;

import games.shared.RandomGenerator;

import java.util.Vector;

public class FirstZoneTileFactory extends TileFactory implements SevenDoorsProtocol {
	
	public FirstZoneTileFactory(BonusProbabilityInfo bonusProbabilityInfo, RandomGenerator randomGenerator) {
		super(bonusProbabilityInfo, randomGenerator);
	}

	@Override
	protected Tile generateTile(int scoreOnLastMove, Vector bannedTiles, int level) {
		Tile result;

		double randNum = _randomGenerator.nextInt(_100_PERSENTS);
		double tmp = 0.;
		if (randNum < (tmp += getBonusProbability("forest", scoreOnLastMove, level)))
			result = new Tile(SevenDoorsProtocol.FOREST);
		else if (randNum < (tmp += getBonusProbability("snake", scoreOnLastMove, level)))
			result = new Tile(SevenDoorsProtocol.SNAKE);
		else if (randNum < (tmp += getBonusProbability("box", scoreOnLastMove, level)))
			result = new Tile(SevenDoorsProtocol.BOX);
		else if (randNum < (tmp += getBonusProbability("frog", scoreOnLastMove, level)))
			result = new Tile(SevenDoorsProtocol.FROG);
		else if (randNum < (tmp += getBonusProbability("bonus", scoreOnLastMove, level)))
			result = new Tile(SevenDoorsProtocol.BONUS);
		else {
			result = new Tile((Math.abs(_randomGenerator.nextInt(Tile.N_TILES))));
			result.setLightBonus(_randomGenerator.nextInt(_100_PERSENTS) < getBonusProbability("light", scoreOnLastMove, level));
			result.setLiana(_randomGenerator.nextInt(_100_PERSENTS) < getBonusProbability("liana", scoreOnLastMove, level));
		}
		
		if (bannedTiles.contains(new Integer(result.getId()))) {
			return generateSimpleTile();
		}
		
		return result;
	}
	
}
