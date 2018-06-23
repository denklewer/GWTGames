package games.shared;

import javax.sound.midi.Transmitter;

public interface RequestFillBufferProcessor {

	boolean process(RequestFillBuffer fillBuffer, Transmitter params);

}
