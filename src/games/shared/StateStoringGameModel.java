package games.shared;

import java.io.Serializable;

public abstract class StateStoringGameModel<T extends GameModelListener>
implements Serializable {

	private long _playerMaxScore;

	public void setPlayerMaxScore(final long playerMaxScore) {
		_playerMaxScore = playerMaxScore;
	}

	public long getPlayerMaxScore() {
		return _playerMaxScore;
	}

	public abstract void addListener(T listener);

	public abstract void removeListener(T listener);

	public abstract long getScore();

}
