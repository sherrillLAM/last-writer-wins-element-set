package org.slin;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.reflect.Whitebox;
import com.anarsoft.vmlens.concurrent.junit.ConcurrentTestRunner;
import net.jodah.concurrentunit.Waiter;

import junit.framework.TestCase;

@RunWith(ConcurrentTestRunner.class)
public class TestLWW_Redis_Add_Remove extends TestCase {
	/* Object under test. */
	private LWW_Redis<String> lwwStr;
	private LWW_Redis<Integer> lwwInt;

	private Integer elementInt;
	private String elementStr;
	private final static int THREAD_COUNT = 100;
	private final static int ACTION_COUNT = 1000;

	@Before
	public void setUp() throws Exception {
		lwwStr = new LWW_Redis<String>("string");
		lwwInt = new LWW_Redis<Integer>("int");
		elementInt = new Integer(10);
		elementStr = "test string";
	}

	/**
	 * This test verifies Add() function handles properly given multithreads adding the same Integer to addSet.
	 */
	@Test
	public void AddInteger_GivenMultiThreads_Success() {
		try {
			ExecutorService service = Executors.newFixedThreadPool(THREAD_COUNT);
			Waiter waiter = new Waiter();

			// Execute Add action of elementInt for ACTION_COUNT times split among threads.
			IntStream.range(0, ACTION_COUNT).forEach(count -> service.submit(() -> {
				LocalDateTime now = LocalDateTime.now();
				lwwInt.Add(elementInt, now);

				//Verify inserted time, which should be always no earlier than parameter now.
				RedisZset<Integer> addSet = Whitebox.getInternalState(lwwInt,"addSet");
				LocalDateTime insertedTime = addSet.get(elementInt);
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
	 * This test verifies Add() function handles properly given multithreads adding the same String to addSet.
	 */
	@Test
	public void AddString_GivenMultiThreads_Success() {
		try {
			ExecutorService service = Executors.newFixedThreadPool(THREAD_COUNT);
			Waiter waiter = new Waiter();

			// Execute Add action of elementStr for ACTION_COUNT times split among threads.
			IntStream.range(0, ACTION_COUNT).forEach(count -> service.submit(() -> {
				LocalDateTime now = LocalDateTime.now();
				lwwStr.Add(elementStr, now);

				//Verify inserted time, which should be always no earlier than parameter now.
				RedisZset<String> addSet = Whitebox.getInternalState(lwwStr,"addSet");
				LocalDateTime insertedTime = addSet.get(elementStr);
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
	 * This test verifies Remove() function handles properly given multithreads adding the same Integer to removeSet.
	 */
	@Test
	public void RemoveInteger_GivenMultiThreads_Success() {
		try {
			ExecutorService service = Executors.newFixedThreadPool(THREAD_COUNT);
			Waiter waiter = new Waiter();

			// Execute Remove action of elementInt for ACTION_COUNT times split among threads.
			IntStream.range(0, ACTION_COUNT).forEach(count -> service.submit(() -> {
				LocalDateTime now = LocalDateTime.now();
				lwwInt.Remove(elementInt, now);

				//Verify inserted time, which should be always no earlier than parameter now.
				RedisZset<Integer> removeSet = Whitebox.getInternalState(lwwInt,"removeSet");
				LocalDateTime insertedTime = removeSet.get(elementInt);
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
	 * This test verifies Remove() function handles properly given multithreads adding the same String to removeSet.
	 */
	@Test
	public void RemoveString_GivenMultiThreads_Success() {
		try {
			ExecutorService service = Executors.newFixedThreadPool(THREAD_COUNT);
			Waiter waiter = new Waiter();

			// Execute Remove action of elementStr for ACTION_COUNT times split among threads.
			IntStream.range(0, ACTION_COUNT).forEach(count -> service.submit(() -> {
				LocalDateTime now = LocalDateTime.now();
				lwwStr.Remove(elementStr, now);

				//Verify inserted time, which should be always no earlier than parameter now.
				RedisZset<String> removeSet = Whitebox.getInternalState(lwwStr,"removeSet");
				LocalDateTime insertedTime = removeSet.get(elementStr);
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

	@After
	public void cleanUp() {
		lwwInt.disconnect();
		lwwStr.disconnect();
	}
}
