package org.slin;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.reflect.Whitebox;
import com.anarsoft.vmlens.concurrent.junit.ConcurrentTestRunner;
import net.jodah.concurrentunit.Waiter;

import junit.framework.TestCase;

@RunWith(ConcurrentTestRunner.class)
public class TestLWW_Add_Remove extends TestCase {
	/* Object under test. */
	private LWW lww;

	private Integer element1;
	private final static int THREAD_COUNT = 100;
	private final static int ACTION_COUNT = 10000;

	@Before
	public void setUp() throws Exception {
		lww = new LWW();
		element1 = new Integer(10);
	}

	/**
	 * This test verifies Add() function handles properly given multithreads adding the same element to addSet.
	 */
	@Test
	public void Add_GivenMultiThreads_Success() {
		try {
			ExecutorService service = Executors.newFixedThreadPool(THREAD_COUNT);
			Waiter waiter = new Waiter();

			// Execute Add action of element1 for ACTION_COUNT times split among threads.
			IntStream.range(0, ACTION_COUNT).forEach(count -> service.submit(() -> {
		    	LocalDateTime now = LocalDateTime.now();
				lww.Add(element1, now);

				//Verify inserted time, which should be always no earlier than parameter now.
				LWWHashMap addSet = Whitebox.getInternalState(lww,"addSet");
				LocalDateTime insertedTime = addSet.get(element1);
				waiter.assertFalse(now.isAfter(insertedTime));

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

	/**
	 * This test verifies Remove() function handles properly given multithreads adding the same element to removeSet.
	 */
	@Test
	public void Remove_GivenMultiThreads_Success() {
		try {
			ExecutorService service = Executors.newFixedThreadPool(THREAD_COUNT);
			Waiter waiter = new Waiter();

			// Execute Remove action of element1 for ACTION_COUNT times split among threads.
			IntStream.range(0, ACTION_COUNT).forEach(count -> service.submit(() -> {
		    	LocalDateTime now = LocalDateTime.now();
				lww.Remove(element1, now);

				//Verify inserted time, which should be always no earlier than parameter now.
				LWWHashMap removeSet = Whitebox.getInternalState(lww,"removeSet");
				LocalDateTime insertedTime = removeSet.get(element1);
				waiter.assertFalse(now.isAfter(insertedTime));

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
