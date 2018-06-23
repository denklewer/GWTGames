package games.client;

import games.shared.RandomGenerator;
import games.shared.RequestFillBuffer;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.google.gwt.user.client.Random;

public class ClientRandomGenerator implements RandomGenerator {

	private GameCommunicator _communicator;
	private Queue<Integer> _randoms = new LinkedList<Integer>();
	private int _minBufferSize;
	private boolean _requested;
	
	public ClientRandomGenerator() {
	}
	
	public ClientRandomGenerator(final int minBufferSize, final GameCommunicator communicator) {
		_communicator = communicator;
		_minBufferSize = minBufferSize;
	}
	
	@Override
	public int nextInt(final int upperBound) {
		if (_minBufferSize == 0) {
			return Random.nextInt(upperBound);
		}
		requestFillBuffer();
		if (_randoms.isEmpty()) {
			throw new EmptyBufferException();  // FIXME!!  need to handle this exception!
		}
		return _randoms.poll() % upperBound;
	}

	void requestFillBuffer() {
		if (_randoms.size() < _minBufferSize && !_requested) {
			_requested = true;
			_communicator.sendPacketCommand(new RequestFillBuffer(_minBufferSize*2));
		}
	}

	public void fillBuffer(final List<Integer> randoms) {
		_randoms.addAll(randoms);
		_requested = false;
	}

	public void setGameCommunicator(final GameCommunicator gameCommunicator) {
		_communicator = gameCommunicator;
	}

}
