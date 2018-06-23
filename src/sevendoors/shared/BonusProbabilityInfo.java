package sevendoors.shared;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BonusProbabilityInfo {

	public static class BonusProbab {
		private int from;
		private int to;
		private int probab;
		public int getFrom() {
			return from;
		}
		public void setFrom(int from) {
			this.from = from;
		}
		public int getTo() {
			return to;
		}
		public void setTo(int to) {
			this.to = to;
		}
		public int getProbab() {
			return probab;
		}
		public void setProbab(int probab) {
			this.probab = probab;
		}
	}
	
	
	public static class BonusElement {
		private int sinceLevel;

		private List<BonusProbab> probabs = new ArrayList<BonusProbab>();
		
		public double getBonusProbability(int scoreOnLastMove) {
			for (BonusProbab bonusProbab : probabs) {
				if (scoreOnLastMove >= bonusProbab.getFrom()
						&& scoreOnLastMove <= bonusProbab.getTo()) {
					return bonusProbab.getProbab();
				}
			}
			return 0;
		}
		
		public int getSinceLevel() {
			return sinceLevel;
		}
		
		public void setSinceLevel(int sinceLevel) {
			this.sinceLevel = sinceLevel;
		}

		public List<BonusProbab> getProbabs() {
			return probabs;
		}

		public void setProbabs(List<BonusProbab> probabs) {
			this.probabs = probabs;
		}
	}
	
	
	private Map<String, BonusElement> bonuses = new HashMap<String, BonusElement>();
	
	public double getBonusProbability(String bonus, int scoreOnLastMove, int level) {
		BonusElement bonusElement = bonuses.get(bonus);
		if (bonusElement == null)
			return 0;
		
		if (level < bonusElement.sinceLevel)
			return 0;
		
		return bonusElement.getBonusProbability(scoreOnLastMove);
	}
	
	public void addBonusInfo(String bonusName, BonusElement bonusElement){
		bonuses.put(bonusName, bonusElement);
	}
	
}
