package org.slin;

import java.time.LocalDateTime;
import java.util.ArrayList;

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
		ArrayList<T> results = new ArrayList<>();
		synchronized(addSet) {
			synchronized(removeSet) {
				for(T element: addSet.keySet()) {
					if(Exists(element)) {
						results.add(element);
					}
				}
			}
		}

		return results;
	}
}
