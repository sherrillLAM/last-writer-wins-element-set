package org.slin;


import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.client.RedisConnectionException;

import com.anarsoft.vmlens.concurrent.junit.ConcurrentTestRunner;
import com.anarsoft.vmlens.concurrent.junit.ThreadCount;

import junit.framework.TestCase;

@RunWith(ConcurrentTestRunner.class)
public class TestLWW_Redis_Get extends TestCase {
	/* Object under test. */
	private LWW_Redis<Integer> lww;

	private Integer[] elements;
	private final static int THREAD_COUNT = 1;
	private final static int ACTION_COUNT = 10;

	private boolean skipTest = false;

	@Before
	public void setUp() {
		try {
			lww = new LWW_Redis<Integer>("int");
			elements = new Integer[3];
			for(int i = 0; i < 3; i++) {
				elements[i] = new Integer(i);
			}
		} catch (RedisConnectionException e) {
			System.out.println("Cannot connect to Redis server. Skipping all tests in TestLWW_Redis_Get...");
			skipTest = true;
		}
	}
	
	@Test
	@ThreadCount(THREAD_COUNT)
	public void Add_Remove_0() {
		org.junit.Assume.assumeFalse(skipTest);

		for(int i = 0; i < ACTION_COUNT; i++) {
			lww.Add(elements[0], LocalDateTime.now());
			lww.Remove(elements[0], LocalDateTime.now());

			// Verify LWW set does not contain elements[0].
			ArrayList<Integer> results = lww.Get();
			assertFalse(results.contains(elements[0]));
		}
	}
	
	@Test
	@ThreadCount(THREAD_COUNT)
	public void Add_Remove_Add_1() {
		org.junit.Assume.assumeFalse(skipTest);

		for(int i = 0; i < ACTION_COUNT; i++) {
			lww.Add(elements[1], LocalDateTime.now());
			lww.Remove(elements[1], LocalDateTime.now());
			lww.Add(elements[1], LocalDateTime.now().plusNanos(10));

			// Verify LWW set has only one element and the element is elements[1].
			ArrayList<Integer> results = lww.Get();
			assertTrue(results.contains(elements[1]));
		}
	}

	@After
	public void cleanUp() {
		org.junit.Assume.assumeFalse(skipTest);

		lww.disconnect();
	}
}
