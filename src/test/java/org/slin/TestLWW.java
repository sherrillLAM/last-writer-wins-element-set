package org.slin;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.reflect.Whitebox;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class TestLWW {
	/* Object under test. */
	private LWW lww;

	private Integer element1;

	@Before
	public void setUp() throws Exception {
		lww = new LWW();
		element1 = new Integer(1);
	}

	@After
	public void tearDown() {
		/*
		 * When using the PowerMockRunner, include this in the @After method of
		 * JUnit to help verify Mockito methods were used correctly.
		 */
		Mockito.validateMockitoUsage();
	}

	/**
	 * This test verifies Add() function adds an element to addSet successfully.
	 */
	@Test
	public void Add_AddOnce_Success() {
		boolean addResult = lww.Add(element1, LocalDateTime.now());

		// Verify Add() function returns true.
		assertTrue(addResult);
		// Verify element is added to addSet.
		ConcurrentHashMap<Integer, LocalDateTime> addSet = Whitebox.getInternalState(lww,"addSet");
		assertNotNull(addSet.get(element1));
	}

	/**
	 * This test verifies when Add() is called twice for the same element,
	 * time stamp in addSet gets updated if the second time stamp is more recent.
	 */
	@Test
	public void Add_AddTwice_Success() {
		LocalDateTime time1 = LocalDateTime.now();
		boolean addResult1 = lww.Add(element1, time1);
		// Add the same element again with a time stamp a minute later
		LocalDateTime time2 = time1.plusMinutes(1);
		boolean addResult2 = lww.Add(element1, time2);

		// Verify Add() function returns true.
		assertTrue(addResult1);
		assertTrue(addResult2);
		// Verify element is added to addSet, and time stamp is updated.
		ConcurrentHashMap<Integer, LocalDateTime> addSet = Whitebox.getInternalState(lww,"addSet");
		assertNotEquals(time1, addSet.get(element1));
		assertEquals(time2, addSet.get(element1));
	}

	/**
	 * This test verifies when Add() is called twice for the same element,
	 * time stamp in addSet is not updated if the second time stamp is older.
	 */
	@Test
	public void Add_AddWithOlderTimestamp_Success() {
		LocalDateTime time1 = LocalDateTime.now();
		boolean addResult1 = lww.Add(element1, time1);
		// Add the same element again with a time stamp a minute before
		LocalDateTime time2 = time1.minusMinutes(1);
		boolean addResult2 = lww.Add(element1, time2);

		// Verify Add() function returns true.
		assertTrue(addResult1);
		assertTrue(addResult2);
		// Verify time stamp in addSet is not updated.
		ConcurrentHashMap<Integer, LocalDateTime> addSet = Whitebox.getInternalState(lww,"addSet");
		assertEquals(time1, addSet.get(element1));
		assertNotEquals(time2, addSet.get(element1));
	}

	/**
	 * This test verifies Remove() function adds an element to removeSet successfully.
	 */
	@Test
	public void Remove_RemoveOnce_Success() {
		boolean removeResult = lww.Remove(element1, LocalDateTime.now());

		// Verify Remove() function returns true.
		assertTrue(removeResult);
		// Verify element is added to removeSet.
		ConcurrentHashMap<Integer, LocalDateTime> removeSet = Whitebox.getInternalState(lww,"removeSet");
		assertNotNull(removeSet.get(element1));
	}

	/**
	 * This test verifies when Remove() is called twice for the same element,
	 * time stamp in removeSet gets updated if the second time stamp is more recent.
	 */
	@Test
	public void Remove_RemoveTwice_Success() {
		LocalDateTime time1 = LocalDateTime.now();
		boolean removeResult1 = lww.Remove(element1, time1);
		// Remove the same element again with a time stamp a minute later
		LocalDateTime time2 = time1.plusMinutes(1);
		boolean removeResult2 = lww.Remove(element1, time2);

		// Verify Remove() function returns true.
		assertTrue(removeResult1);
		assertTrue(removeResult2);
		// Verify element is added to removeSet, and time stamp is updated.
		ConcurrentHashMap<Integer, LocalDateTime> removeSet = Whitebox.getInternalState(lww,"removeSet");
		assertNotEquals(time1, removeSet.get(element1));
		assertEquals(time2, removeSet.get(element1));
	}

	/**
	 * This test verifies when Remove() function is called twice for the same element,
	 * time stamp in removeSet is not updated if the second time stamp is older.
	 */
	@Test
	public void Remove_RemoveWithOlderTimestamp_Success() {
		LocalDateTime time1 = LocalDateTime.now();
		boolean removeResult1 = lww.Remove(element1, time1);
		// Remove the same element again with a time stamp a minute before
		LocalDateTime time2 = time1.minusMinutes(1);
		boolean removeResult2 = lww.Remove(element1, time2);

		// Verify Remove() function returns true.
		assertTrue(removeResult1);
		assertTrue(removeResult2);
		// Verify time stamp in removeSet is not updated.
		ConcurrentHashMap<Integer, LocalDateTime> removeSet = Whitebox.getInternalState(lww,"removeSet");
		assertEquals(time1, removeSet.get(element1));
		assertNotEquals(time2, removeSet.get(element1));
	}
}
