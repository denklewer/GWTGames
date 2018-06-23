package games.shared;

public interface Dictionary {

	public abstract String getDictionaryFilePath();

	public abstract boolean isEmpty();

	public abstract void loadDictionary(final String wordList);

}