package org.slin;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Set;

/**
 * This class is a wrapper for HashMap class, providing thread-safe operations for original HashMap.
 */

public class LWWHashMap {
	private HashMap<Object, LocalDateTime> map = new HashMap<Object, LocalDateTime>();

	/**
	 * A synchronized method to associate the specified time value with the specified key in the map.
	 * If the map previously contained a mapping for the key, compare the time values.
	 * The old time value is replaced.
	 *
	 * @param key
	 *            The key with which the specified value is to be associated.
	 * @param time
	 *            The time value to be associated with the specified key.
	 * @return True if time value to be associated is the latest, false otherwise.
	 */
	public synchronized boolean syncPut(Object key, LocalDateTime time) {
		LocalDateTime existing_time = (LocalDateTime) map.get(key);
		if(existing_time != null && existing_time.isAfter(time)) {
			return false;
		}
		map.put(key, time);

		return true;
	}

	/**
	 * Returns the time value to which the specified key is mapped, or null if this map contains no mapping for the key.
	 *
	 * @param key
	 *            The key whose associated value is to be returned.
	 * @return The time value to which the specified key is mapped, or null if this map contains no mapping for the key.
	 */
	public LocalDateTime get(Object key) {
		return map.get(key);
	}

	/**
	 * Returns a set view of the keys contained in this map.
	 *
	 * @return A set view of the keys contained in this map.
	 */
	public Set<Object> keySet() {
		return map.keySet();
	}
}