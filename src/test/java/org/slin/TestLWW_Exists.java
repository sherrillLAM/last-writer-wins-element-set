package org.slin;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.anarsoft.vmlens.concurrent.junit.ConcurrentTestRunner;
import com.anarsoft.vmlens.concurrent.junit.ThreadCount;

import junit.framework.TestCase;
import net.jodah.concurrentunit.Waiter;

@RunWith(ConcurrentTestRunner.class)
public class TestLWW_Exists extends TestCase {
	/* Object under test. */
	private LWW lww;

	private Integer[] elements;
	private final static int THREAD_COUNT = 1000;
	private final static int ACTION_COUNT = 10;

	@Before
	public void setUp() throws Exception {
		lww = new LWW();
		elements = new Integer[5];
		for(int i = 0; i < 5; i++) {
			elements[i] = new Integer(i);
		}
	}

	/**
	 * Element 0 is expected to exist in LWW set after this test.
	 */
	@Test
	@ThreadCount(THREAD_COUNT)
	public void Add_0() {
		lww.Add(elements[0], LocalDateTime.now());
	}

	/**
	 * Element 1 is not expected to exist in LWW set after this test.
	 */
	@Test
	@ThreadCount(THREAD_COUNT)
	public void Remove_1() {
		lww.Remove(elements[1], LocalDateTime.now());
	}

	/**
	 * Element 2 is not expected to exist in LWW set after this test.
	 */
	@Test
	@ThreadCount(THREAD_COUNT)
	public void Add_Remove_2() {
		lww.Add(elements[2], LocalDateTime.now());
		lww.Remove(elements[2], LocalDateTime.now());
	}

	/**
	 * Element 3 is expected to exist in LWW set after this test.
	 */
	@Test
	@ThreadCount(THREAD_COUNT)
	public void Remove_Add_3() {
		lww.Remove(elements[3], LocalDateTime.now());
		lww.Add(elements[3], LocalDateTime.now().plusNanos(10));
	}

	/**
	 * Element 4 is expected to exist in LWW set after this test.
	 */
	@Test
	@ThreadCount(THREAD_COUNT)
	public void Add_Remove_Add_4() {
		lww.Add(elements[4], LocalDateTime.now());
		lww.Remove(elements[4], LocalDateTime.now());
		lww.Add(elements[4], LocalDateTime.now().plusNanos(10));
	}

	@After
	public void Verify_Exists() {
		try {
			ExecutorService service = Executors.newFixedThreadPool(THREAD_COUNT);
			Waiter waiter = new Waiter();

			// Execute verification for ACTION_COUNT times split among threads.
			IntStream.range(0, ACTION_COUNT).forEach(count -> service.submit(() -> {
				// Verify all elements to exist / not exist in LWW set as expected.
				waiter.assertTrue(lww.Exists(elements[0]));
				waiter.assertFalse(lww.Exists(elements[1]));
				waiter.assertFalse(lww.Exists(elements[2]));
				waiter.assertTrue(lww.Exists(elements[3]));
				waiter.assertTrue(lww.Exists(elements[4]));

				waiter.resume();
			}));

		    service.awaitTermination(1000, TimeUnit.MILLISECONDS);
		    //Set timeout as 1 second for waiter to wake up.
		    waiter.await(1, TimeUnit.SECONDS);
		    service.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
