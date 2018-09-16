package org.slin;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertFalse;

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
	
	@Before
	public void setUp() throws Exception {
		lww = new LWW();
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
	 * This test verifies New() function initializes addSet and removeSet properly.
	 */
	@Test
	public void New_Null_Success() {
		lww.New();
		ConcurrentHashMap<Object, LocalDateTime> addSet = Whitebox.getInternalState(lww,"addSet");
		assertNotNull(addSet);
		ConcurrentHashMap<Object, LocalDateTime> removeSet = Whitebox.getInternalState(lww,"removeSet");
		assertNotNull(removeSet);
	}

	/**
	 * This test verifies calling New() function twice does not initialize addSet and removeSet twice.
	 */
	@Test
	public void New_NotNull_Success() {
		lww.New();
		ConcurrentHashMap<Object, LocalDateTime> addSet1 = Whitebox.getInternalState(lww,"addSet");
		ConcurrentHashMap<Object, LocalDateTime> removeSet1 = Whitebox.getInternalState(lww,"removeSet");
		lww.New();
		ConcurrentHashMap<Object, LocalDateTime> addSet2 = Whitebox.getInternalState(lww,"addSet");
		ConcurrentHashMap<Object, LocalDateTime> removeSet2 = Whitebox.getInternalState(lww,"removeSet");

		// Verify the two addSets are the same object
		assertTrue(addSet1 == addSet2);
		// Verify the two removeSets are the same object
		assertTrue(removeSet1 == removeSet2);
	}

	/**
	 * This test verifies Add() function adds an element to addSet successfully.
	 */
	@Test
	public void Add_AddSetNotNull_Success() {
		lww.New();
		
		Integer element = new Integer(1);
		boolean addResult = lww.Add(element, LocalDateTime.now());

		// Verify Add() function returns true.
		assertTrue(addResult);
		// Verify element is added to addSet.
		ConcurrentHashMap<Object, LocalDateTime> addSet = Whitebox.getInternalState(lww,"addSet");
		assertNotNull(addSet.get(element));
	}

	/**
	 * This test verifies when Add() is called twice for the same element,
	 * time stamp in addSet gets updated if the second time stamp is more recent.
	 */
	@Test
	public void Add_AddTwice_Success() {
		lww.New();
		
		Integer element = new Integer(1);
		LocalDateTime time1 = LocalDateTime.now();
		boolean addResult1 = lww.Add(element, time1);
		// Add the same element again with a time stamp a minute later
		LocalDateTime time2 = time1.plusMinutes(1);
		boolean addResult2 = lww.Add(element, time2);

		// Verify Add() function returns true.
		assertTrue(addResult1);
		assertTrue(addResult2);
		// Verify element is added to addSet, and time stamp is updated.
		ConcurrentHashMap<Object, LocalDateTime> addSet = Whitebox.getInternalState(lww,"addSet");
		assertNotEquals(time1, addSet.get(element));
		assertEquals(time2, addSet.get(element));
	}

	/**
	 * This test verifies when Add() is called twice for the same element,
	 * time stamp in addSet is not updated if the second time stamp is older.
	 */
	@Test
	public void Add_AddWithOlderTimestamp_Success() {
		lww.New();
		
		Integer element = new Integer(1);
		LocalDateTime time1 = LocalDateTime.now();
		boolean addResult1 = lww.Add(element, time1);
		// Add the same element again with a time stamp a minute before
		LocalDateTime time2 = time1.minusMinutes(1);
		boolean addResult2 = lww.Add(element, time2);

		// Verify Add() function returns true.
		assertTrue(addResult1);
		assertTrue(addResult2);
		// Verify time stamp in addSet is not updated.
		ConcurrentHashMap<Object, LocalDateTime> addSet = Whitebox.getInternalState(lww,"addSet");
		assertEquals(time1, addSet.get(element));
		assertNotEquals(time2, addSet.get(element));
	}

	/**
	 * This test verifies Add() function fails to add an element to addSet without calling New().
	 */
	@Test
	public void Add_AddSetNull_Fail() {
		Integer element = new Integer(1);
		boolean addResult = lww.Add(element, LocalDateTime.now());

		// Verify Add() function returns false.
		assertFalse(addResult);
		// Verify addSet is null.
		ConcurrentHashMap<Object, LocalDateTime> addSet = Whitebox.getInternalState(lww,"addSet");
		assertNull(addSet);
	}

	/**
	 * This test verifies Remove() function adds an element to removeSet successfully.
	 */
	@Test
	public void Remove_RemoveSetNotNull_Success() {
		lww.New();

		Integer element = new Integer(1);
		boolean removeResult = lww.Remove(element, LocalDateTime.now());

		// Verify Remove() function returns true.
		assertTrue(removeResult);
		// Verify element is added to removeSet.
		ConcurrentHashMap<Object, LocalDateTime> removeSet = Whitebox.getInternalState(lww,"removeSet");
		assertNotNull(removeSet.get(element));
	}

	/**
	 * This test verifies when Remove() is called twice for the same element,
	 * time stamp in removeSet gets updated if the second time stamp is more recent.
	 */
	@Test
	public void Remove_RemoveTwice_Success() {
		lww.New();
		
		Integer element = new Integer(1);
		LocalDateTime time1 = LocalDateTime.now();
		boolean removeResult1 = lww.Remove(element, time1);
		// Remove the same element again with a time stamp a minute later
		LocalDateTime time2 = time1.plusMinutes(1);
		boolean removeResult2 = lww.Remove(element, time2);

		// Verify Remove() function returns true.
		assertTrue(removeResult1);
		assertTrue(removeResult2);
		// Verify element is added to removeSet, and time stamp is updated.
		ConcurrentHashMap<Object, LocalDateTime> removeSet = Whitebox.getInternalState(lww,"removeSet");
		assertNotEquals(time1, removeSet.get(element));
		assertEquals(time2, removeSet.get(element));
	}

	/**
	 * This test verifies when Remove() function is called twice for the same element,
	 * time stamp in removeSet is not updated if the second time stamp is older.
	 */
	@Test
	public void Remove_RemoveWithOlderTimestamp_Success() {
		lww.New();
		
		Integer element = new Integer(1);
		LocalDateTime time1 = LocalDateTime.now();
		boolean removeResult1 = lww.Remove(element, time1);
		// Remove the same element again with a time stamp a minute before
		LocalDateTime time2 = time1.minusMinutes(1);
		boolean removeResult2 = lww.Remove(element, time2);

		// Verify Remove() function returns true.
		assertTrue(removeResult1);
		assertTrue(removeResult2);
		// Verify time stamp in removeSet is not updated.
		ConcurrentHashMap<Object, LocalDateTime> removeSet = Whitebox.getInternalState(lww,"removeSet");
		assertEquals(time1, removeSet.get(element));
		assertNotEquals(time2, removeSet.get(element));
	}

	/**
	 * This test verifies Remove() function fails to add an element to removeSet without calling New().
	 */
	@Test
	public void Remove_RemoveSetNull_Fail() {
		Integer element = new Integer(1);
		boolean removeResult = lww.Remove(element, LocalDateTime.now());

		// Verify Remove() function returns true.
		assertFalse(removeResult);
		// Verify removeSet is null.
		ConcurrentHashMap<Object, LocalDateTime> removeSet = Whitebox.getInternalState(lww,"removeSet");
		assertNull(removeSet);
	}
}
