package sudoku.shared;

import games.shared.RandomGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FieldsByLevel {

	private Map<String, List<List<List<Integer>>>> fieldsByLevel = new HashMap<String, List<List<List<Integer>>>>();
	private final RandomGenerator _randomGenerator;

	public FieldsByLevel(RandomGenerator randomGenerator) {
		_randomGenerator = randomGenerator;
		fieldsByLevel.put("Easy", new ArrayList<List<List<Integer>>>());
		fieldsByLevel.put("Middle", new ArrayList<List<List<Integer>>>());
		fieldsByLevel.put("Hard", new ArrayList<List<List<Integer>>>());
	}
	
	public void addField(String level, List<List<Integer>> field){
		List<List<List<Integer>>> levelFields = fieldsByLevel.get(level);
		if(levelFields == null){
			return;
		}
		levelFields.add(field);
	}
	
	public List<List<Integer>> getField(int level){
		String difficulty;
		switch (level) {
		case CommonConstants.DIFFICULTY_EASY:
			difficulty = "Easy";
			break;
		case CommonConstants.DIFFICULTY_INTERMIDIATE:
			difficulty = "Middle";
			break;
		default:
			difficulty = "Hard";
			break;
		}
		List<List<List<Integer>>> fieldsOfCurrLevel = fieldsByLevel.get(difficulty);
		int fieldNum = Math.abs(_randomGenerator.nextInt(fieldsOfCurrLevel.size()));
		return fieldsOfCurrLevel.get(fieldNum);
	}
	
}
