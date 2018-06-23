package games.shared;

import java.util.TreeSet;

public abstract class BaseDictionary implements Dictionary {
	
	protected String[] _words;
	protected int _totalLength;
	
	@Override
	public abstract String getDictionaryFilePath();

	public abstract void letterAnalisys(String word);
	
	public abstract void valuesAndFrequenciesFilling();
	
	@Override
	public boolean isEmpty() {
		return _words == null;
	}


	public boolean searchWord(final String word) {
		if (word == null) {
			return false;
		}
		int start = 0;
		int end = _words.length - 1;

		while (start <= end) {
			final int middle = (start + end) / 2;

			final int res = _words[middle].compareTo(word);

			if (res > 0) {
				end = middle - 1;
			} else if (res < 0) {
				start = middle + 1;
			} else {
				return true;
			}
		}
		return false;
	}

	public int getDictionaryLength() {
		return _words.length;
	}

	public String getNextWord(final int i) {
		return _words[i];
	}

	@Override
	public void loadDictionary(final String wordList) {
		final TreeSet<String> words = DictionaryUtils.loadDictionary(wordList);
		
		final String[] wordsArray = new String[words.size()];
		words.toArray(wordsArray);
		_words = wordsArray;
		
		for (int i = 0; i < _words.length; i++) {
			final String word = _words[i];
			if (word != null && !word.equals("") && !word.equals("\t")) {
				_totalLength += word.length();
				letterAnalisys(word);
			}
		}
		valuesAndFrequenciesFilling();
		System.out.println("--->>  total lenght of dict = " + _totalLength);
	}

}
