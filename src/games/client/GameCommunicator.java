package games.client;

import java.io.Serializable;

public interface GameCommunicator {

	void sendPacketCommand(Serializable packetBody);
	
	User getCurrentUser();

}
