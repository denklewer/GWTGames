package sevendoors.client;

import games.shared.RandomGenerator;
import sevendoors.shared.SevenDoorsState;

public interface Store {

	String store(SevenDoorsState state) throws Exception;
	SevenDoorsState restore(String value, RandomGenerator randomGenerator) throws Exception;

}
