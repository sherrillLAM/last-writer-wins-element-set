package org.slin;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;

import org.redisson.Redisson;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;


/**
 * This class is a wrapper for RScoredSortedSet class, providing thread-safe operations for original RScoredSortedSet.
 */

public class RedisZset<T> {
	private Config config;
	private RedissonClient redisson;
	private RScoredSortedSet<T> zset;

	/** The configuration file for Redis server connection. */
	private final static String CONFIG_FILE_PATH = "config/RedisConfig.json";

	/**
	 * Constructor for RedisZset class.
	 *
	 * @param String
	 *            The set name in Redis server
	 */
	public RedisZset(String setName) {
		try {
			config = Config.fromJSON(new File(CONFIG_FILE_PATH));
			redisson = Redisson.create(config);
			zset = redisson.getScoredSortedSet(setName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * A synchronized method to specify the time value as score for the specified element in the set.
	 * If the set previously contained the element, compare the time values.
	 * The old time value is replaced.
	 *
	 * @param element
	 *            The element to add to the set.
	 * @param time
	 *            The time value to be set as score.
	 * @return True if time value to be associated is the latest, false otherwise.
	 */
	public synchronized boolean syncPut(T element, LocalDateTime time) {
		Double existingTime = zset.getScore(element);
		Double currentTime = (double) time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
		if(existingTime != null && existingTime > currentTime) {
			return false;
		}
		zset.add(currentTime, element);

		return true;
	}

	/**
	 * Returns the time value to which the specified element has as score, or null if this set does not contain the element.
	 *
	 * @param element
	 *            The element whose associated score is to be returned.
	 * @return The time value to which the specified element is mapped, or null if this set does not contain the element.
	 */
	public LocalDateTime get(T element) {
		Double time = zset.getScore(element);
		if (time != null) {
			// Convert from milli seconds to LocalDateTime
			return LocalDateTime.ofInstant(Instant.ofEpochMilli(Math.round(time)), ZoneId.systemDefault());
		}
		return null;
	}

	/**
	 * Returns collection with all elements contained in this set.
	 *
	 * @return A collection with all elements contained in this set.
	 */
	public Collection<T> keySet() {
		return zset.readAll();
	}

	public void disconnect() {
		redisson.shutdown();
	}
}