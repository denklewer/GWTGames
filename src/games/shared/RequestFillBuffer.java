package games.shared;

import javax.sound.midi.Transmitter;

public class RequestFillBuffer extends Command<RequestFillBufferProcessor> {

	private int _bufferSize;

	public RequestFillBuffer() {
	}

	public RequestFillBuffer(final int bufferSize) {
		_bufferSize = bufferSize;
	}

	public int getBufferSize() {
		return _bufferSize;
	}
	
	@Override
	public boolean execute(final RequestFillBufferProcessor t, final Object... params) {
		return t.process(this, (Transmitter)params[0]);
	}

}
