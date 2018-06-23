package games.shared;

import java.util.TreeSet;

public class DictionaryUtils {
	
	private DictionaryUtils() {
	}
	
	public static TreeSet<String> loadDictionary(final String wordList) {
		final TreeSet<String> words = new TreeSet<String>();
		final String[] splitText = wordList.split("\n");
		if(splitText.length > 0){
			for (int i = 0; i < splitText.length; i++) {
				final String word = splitText[i];
				if (word != null && !word.equals("") && !word.equals("\t")
						&& (word.indexOf('-') == -1)){
					words.add(word);
				}
			}
		}
		return words;
	}

}
