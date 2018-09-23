package org.slin;

import java.time.LocalDateTime;
import java.util.ArrayList;

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
	public boolean Remove(Object element, LocalDateTime time) {
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
	public boolean Exists(Object element) {
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
	public ArrayList<Object> Get() {
		ArrayList<Object> results = new ArrayList<>();
		synchronized(addSet) {
			synchronized(removeSet) {
				for(Object element: addSet.keySet()) {
					if(Exists(element)) {
						results.add(element);
					}
				}
			}
		}

		return results;
	}
}
