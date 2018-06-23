package games.client;

import games.shared.FillBuffer;
import games.shared.FillBufferProcessor;
import games.shared.GameModel;
import games.shared.GameModelListener;

import java.io.Serializable;

public abstract class GenericController<Model extends GameModel<? extends GameModelListener>> implements GameController, FillBufferProcessor {
	
	protected GameCommunicator _gameCommunicator = new GameCommunicator() {

		@Override
		public void sendPacketCommand(final Serializable packetBody) {
			// ignore
			System.out.println("======>>> ignoring " + packetBody);
		}

		@Override
		public User getCurrentUser() {
			return new User() {};
		}
	};
	
	protected ClientRandomGenerator _clientRandomGenerator;
	private boolean _bufferReady = true;
	protected Model _model;

	public GenericController(final Model model) {
		_model = model;
	}
	
	@Override
	public void setGameCommunicator(final GameCommunicator gameCommunicator) {
		_gameCommunicator = gameCommunicator;
		if(_clientRandomGenerator != null){
			_clientRandomGenerator.setGameCommunicator(gameCommunicator);
		}
	}

	public void uiReady() {
		if (_bufferReady) {
			start();
		} else {
			requestGameInfo();
			_clientRandomGenerator.requestFillBuffer();
		}
	}
	
	protected void requestGameInfo() {
		// request server info
	}

	abstract protected void start();
	
	public void setBufferReady(final boolean isReady) {
		_bufferReady = isReady;
	}
	
	@Override
	public boolean process(final FillBuffer fillBuffer) {
		_clientRandomGenerator.fillBuffer(fillBuffer.getRandoms());
		if(!_bufferReady){
			start();
		}
		setBufferReady(true);
		return true;
	} 

}
