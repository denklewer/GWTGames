package games.shared;

import java.io.Serializable;

abstract public class Command<T> implements Serializable {

	public boolean execute(final T t, final Object ... params) {
		return false;
	}

	protected int _id = -1;

	public Command() {
	}

	public Command(final int id) {
		_id = id;
	}

	public int getID() {
		return _id;
	}

	public void setID(final int id) {
		_id = id;
	}

	@Override
	public String toString() {
		return getClass() + " id=" + _id;
	}

}
