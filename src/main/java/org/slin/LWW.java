package org.slin;

import java.time.LocalDateTime;

public class LWW {
	private LWWHashMap addSet;
	private LWWHashMap removeSet;

	/**
	 * Create an empty LWW set and initialize Add set and Remove set.
	 */
	public LWW() {
		addSet = new LWWHashMap();
		removeSet = new LWWHashMap();
	}

	/**
	 * Add an element to LWW and record the time stamp.
	 *
	 * @param element
	 *            The element being added.
	 * @param time
	 *            The time stamp of the add action.
	 * @return True if time stamp of the Add action is the latest, false otherwise.
	 */
	public boolean Add(Object element, LocalDateTime time) {
		boolean result = addSet.syncPut(element, time);
		return result;
	}
	
	/**
	 * Remove an element from LWW and record the time stamp.
	 *
	 * @param element
	 *            The element being removed.
	 * @param time
	 *            The time stamp of the Remove action.
	 * @return True if time stamp of the Remove action is the latest, false otherwise.
	 */
	public boolean Remove(Object element, LocalDateTime time) {
		return removeSet.syncPut(element, time);
	}
}
