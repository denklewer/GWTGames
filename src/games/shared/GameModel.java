package games.shared;


public interface GameModel<T extends GameModelListener> {

	void addListener(T listener);

}
