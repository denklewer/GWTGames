package scrabble.shared;

import games.shared.BaseDictionary;
import games.shared.RandomGenerator;

import java.util.ArrayList;

public class ScrabbleDictionary extends BaseDictionary{

    private ArrayList<Letter> _letters;
    private int[] _values;
    private double[] _frequencies;
	private RandomGenerator _randomGenerator;

    public ScrabbleDictionary(final RandomGenerator randomGenerator) {
    	_randomGenerator = randomGenerator;
    	_values = new int[ScrabbleProtocol.AlPHABET_SIZE];
    	_frequencies = new double[ScrabbleProtocol.AlPHABET_SIZE];
    	_letters = new ArrayList<Letter>();
    }
    
    @Override
    public void letterAnalisys(String word) {
        char[] wletters = word.toCharArray();

        if (_letters.size() == 0) {
            if ((wletters[0] >= 'а') && (wletters[0] <= 'я'))
            	_letters.add(new Letter(wletters[0], 1));
        }
        if (_letters.size() != 0) {
            for (int i = 0; i < wletters.length; i++) {
                boolean found = false;
                for (int j = 0; j < _letters.size(); j++) {
                    if (_letters.get(j).getLetter() == wletters[i]) {
                    	_letters.get(j).increaseFrequency();
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    if ((wletters[i] >= 'а') && (wletters[i] <= 'я'))
                    	_letters.add(new Letter(wletters[i], 1));
                }
            }
        }
    }

    public int searchWordInclusion(String word) {
        if (word == null)
            return -1;
        int start = 0;
        int end = _words.length - 1;

        while (start <= end) {
            int middle = (start + end) / 2;

            if (_words[middle].startsWith(word)
                    && _words[middle].length() > word.length())
                return 1;
            int res = _words[middle].compareTo(word);

            if (res > 0)
                end = middle - 1;
            else if (res < 0)
                start = middle + 1;
            else
                return 0;
        }
        return -1;
    }

    public int evaluateWord(String word) {
        char[] letters = word.toCharArray();
        int rating = 0;

        for (int i = 0; i < letters.length; i++)
            rating += _values[letters[i] - 'а'];

        return rating;
    }

    public int[] getValues() {
        return _values;
    }

    public String getWord() {
        String word = new String();

        do {
            int index = Math.abs(_randomGenerator.nextInt(_words.length));
            word = _words[index];
        }
        while (word.length() <= 3 || word.length() > 6);

        return word;
    }

	public String findWord(String word) {
		
		for(int i = 0; i < _letters.size(); i++){
			String newWord = word.replace('*', _letters.get(i).getLetter());
			if(searchWord(newWord)){
				return newWord;
			}
		}
		
		return null;
	}
	
	public char[] getLetters(){
		char[] letters = new char[_letters.size()];
		for (int i = 0; i < _letters.size(); i++) {
			letters[i] = _letters.get(i).getLetter();
		}
		return letters;
	}

	public void check(){
		for (int i = 0; i < 10; i++) {
			System.out.println(_words[i]);
		} 
	}

	@Override
	public String getDictionaryFilePath() {
		return "data/scrabble/dictionary.txt.utf8";
	}

	@Override
	public void valuesAndFrequenciesFilling() {
		for (int i = 0; i < _letters.size(); i++) {
			Letter letter = _letters.get(i);
			int value = 100 - ((letter.getFrequency() * 1000) / _totalLength);
			_values[letter.getLetter() - 'а'] = value;
			_frequencies[letter.getLetter() - 'а'] = (double) letter.getFrequency()
					/ (double) _totalLength;
		}		
	}
}
