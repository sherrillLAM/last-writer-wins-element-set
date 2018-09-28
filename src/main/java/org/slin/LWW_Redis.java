package org.slin;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

public class LWW_Redis<T> {
	private RedisZset<T> addSet;
	private RedisZset<T> removeSet;

	/**
	 * Create an empty LWW set and initialize Add set and Remove set.
	 *
	 * @param String
	 *            The set name of the LWW set
	 */
	public LWW_Redis(String setName) {
		addSet = new RedisZset<T>(setName + "-addSet");
		removeSet = new RedisZset<T>(setName + "-removeSet");
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
		return addSet.syncPut(element, time);
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
		return removeSet.syncPut(element, time);
	}

	/**
	 * Determine if an element exists in LWW element set.
	 *
	 * @param element
	 *            The element being checked.
	 * @return Returns true if the element exists in LWW element set.
	 */
	public boolean Exists(T element) {
		synchronized(addSet) {
			synchronized(removeSet) {
				LocalDateTime addTime = addSet.get(element);
				LocalDateTime removeTime = removeSet.get(element);
		
				if(addTime == null) {
					return false;
				}
				if(removeTime == null) {
					return true;
				}
		
				boolean result = addTime.isAfter(removeTime);
				return result;
			}
		}
	}

	/**
	 * Get all elements in LWW element set.
	 *
	 * @return Returns an array with all elements in LWW element set.
	 */
	public ArrayList<T> Get() {
		synchronized(addSet) {
			synchronized(removeSet) {
				ArrayList<T> results = new ArrayList<>();
				Collection<T> allKeys = addSet.keySet();
				for(T element: allKeys) {
					if(Exists(element)) {
						results.add(element);
					}
				}
		
				return results;
			}
		}
	}

	public void disconnect() {
		addSet.disconnect();
		removeSet.disconnect();
	}
}
