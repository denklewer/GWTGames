package games.shared;

import java.util.HashMap;
import java.util.Map;

public class Resources {
	
	private static Map<Class, Object> _resources = new HashMap<Class, Object>();

	private Resources() {
	}
	
	public static <T> T get(final Class<T> t)  {
		return ((T) _resources.get(t));
	}

	public static <T> void set(final Class<T> t, final T resource) {
		_resources.put(t, resource);
	}

}
