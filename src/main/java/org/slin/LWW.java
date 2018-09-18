package org.slin;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

public class LWW {
	private ConcurrentHashMap<Integer, LocalDateTime> addSet;
	private ConcurrentHashMap<Integer, LocalDateTime> removeSet;

	/**
	 * Create an empty LWW set and initialize Add set and Remove set.
	 */
	public LWW() {
		addSet = new ConcurrentHashMap<>();
		removeSet = new ConcurrentHashMap<>();
	}
	
	/**
	 * Add an element to LWW and record the time stamp.
	 *
	 * @param element
	 *            The element being added.
	 * @param time
	 *            The time stamp of the add action.
	 * @return Returns true if adding element successfully.
	 */
	public boolean Add(Integer element, LocalDateTime time) {
		addSet.put(element, time);
		return true;
	}
	
	/**
	 * Remove an element from LWW and record the time stamp.
	 *
	 * @param element
	 *            The element being removed.
	 * @param time
	 *            The time stamp of the remove action.
	 * @return Returns true if removing element successfully.
	 */
	public boolean Remove(Integer element, LocalDateTime time) {
		removeSet.put(element, time);
		return true;
	}
}
