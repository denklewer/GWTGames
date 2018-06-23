package games.shared;

import java.util.List;

public class FillBuffer extends Command<FillBufferProcessor>{

	private List<Integer> _randoms;

	public FillBuffer() {
	}
	
	public FillBuffer(final List<Integer> randoms) {
		_randoms = randoms;
	}

	public List<Integer> getRandoms() {
		return _randoms;
	}
	
	@Override
	public boolean execute(final FillBufferProcessor t, final Object... params) {
		return t.process(this);
	}

}
