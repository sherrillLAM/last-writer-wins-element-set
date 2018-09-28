package org.slin;

import java.time.LocalDateTime;

public class LWW<T> {
	private LWWHashMap<T> addSet;
	private LWWHashMap<T> removeSet;

	/**
	 * Create an empty LWW set and initialize Add set and Remove set.
	 */
	public LWW() {
		addSet = new LWWHashMap<T>();
		removeSet = new LWWHashMap<T>();
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
	public boolean Add(T element, LocalDateTime time) {
		synchronized(addSet) {
			return addSet.syncPut(element, time);
		}
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
	public boolean Remove(T element, LocalDateTime time) {
		synchronized(removeSet) {
			return removeSet.syncPut(element, time);
		}
	}
}
