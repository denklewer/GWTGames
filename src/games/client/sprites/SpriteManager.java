package games.client.sprites;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.animation.client.AnimationScheduler.AnimationCallback;


public class SpriteManager<T> implements AnimationCallback {

	private final List<TimerListener<T>> _listeners = new ArrayList<TimerListener<T>>();
	private final SpriteGraphics<T> _graphics;
	private final List<Rectangle> _dirtyRegions = new ArrayList<Rectangle>();

	public SpriteManager(final SpriteGraphics<T> graphics) {
		_graphics = graphics;
	}

	private void paint(final TimerListener<T>[] listeners) {
		for (final TimerListener<T> listener : listeners) {
			listener.paint(_graphics);
		}
	}

	private void update(final TimerListener<T>[] listeners, final long timestamp) {
		for (final TimerListener<T> listener : listeners) {
			listener.update(timestamp);
		}
	}

	public void addListener(final TimerListener<T> listener) {
		_listeners.add(listener);
		listener.setDirtyRegions(_dirtyRegions);
	}

	public void removeListener(final TimerListener<T> listener) {
		_listeners.remove(listener);
	}

	@Override
	public void execute(final double timestamp) {
		TimerListener<T>[] listeners = new TimerListener[0];
		listeners = _listeners.toArray(listeners);
		update(listeners, (long) timestamp);
		paint(listeners);
		_dirtyRegions.clear();
		AnimationScheduler.get().requestAnimationFrame(this);
	}

	public void onMouseDown(int x, int y, int modifiers) {
		for (int i = _listeners.size() - 1; i >= 0; i--) {
			TimerListener<T> listener = _listeners.get(i);
			if(listener.onMouseDown(x, y, modifiers)){
				return;
			}
		}
	}

	public void onMouseUp(int x, int y, int modifiers) {
		for (int i = _listeners.size() - 1; i >= 0; i--) {
			TimerListener<T> listener = _listeners.get(i);
			if(listener.onMouseUp(x, y, modifiers)){
				return;
			}
		}
	}
	
	public void onMouseMove(int x, int y) {
		for (int i = _listeners.size() - 1; i >= 0; i--) {
			TimerListener<T> listener = _listeners.get(i);
			if(listener.onMouseMove(x, y)){
				return;
			}
		}
	}

	public void onDoubleClick(int x, int y) {
		for (int i = _listeners.size() - 1; i >= 0; i--) {
			TimerListener<T> listener = _listeners.get(i);
			if(listener.onDoubleClick(x, y)){
				return;
			}
		}
	}

}
