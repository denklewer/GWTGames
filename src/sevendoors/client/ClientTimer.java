package sevendoors.client;

import games.shared.RepeatingTimer;

import com.google.gwt.user.client.Timer;

public class ClientTimer implements RepeatingTimer {
	
	private Timer _timer;
	private Runnable _action;
	
	public ClientTimer() {
		_timer = new Timer() {
			
			@Override
			public void run() {
				if(_action == null){
					return;
				}
				_action.run();
			}
		};
	}

	@Override
	public void scheduleRepeating(Runnable action, int periodMillis) {
		_action = action;
		_timer.scheduleRepeating(periodMillis);
	}

	@Override
	public void stop() {
		_timer.cancel();
	}

}
