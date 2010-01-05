package org.junit.internal;

import java.lang.reflect.Array;

import org.junit.Assert;

/**
 * Defines criteria for finding two items "equal enough". Concrete subclasses
 * may demand exact equality, or, for example, equality within a given delta.
 */
public abstract class ComparisonCriteria {
	/**
	 * Asserts that two arrays are equal, according to the criteria defined by
	 * the concrete subclass. If they are not, an {@link AssertionError} is
	 * thrown with the given message. If <code>expecteds</code> and
	 * <code>actuals</code> are <code>null</code>, they are considered equal.
	 * 
	 * @param message
	 *            the identifying message for the {@link AssertionError} (
	 *            <code>null</code> okay)
	 * @param expecteds
	 *            Object array or array of arrays (multi-dimensional array) with
	 *            expected values.
	 * @param actuals
	 *            Object array or array of arrays (multi-dimensional array) with
	 *            actual values
	 */
	public void arrayEquals(String message, Object expecteds, Object actuals)
			throws ArrayComparisonFailure {
		if (expecteds == actuals)
			return;
		String header= message == null ? "" : message + ": ";

		Lengths lengths = assertArraysAreSameLength(expecteds,
				actuals, header);

		for (int i= 0, min = lengths.min(); i < min; i++) {
			Object expected= Array.get(expecteds, i);
			Object actual= Array.get(actuals, i);

			if (isArray(expected) && isArray(actual)) {
				try {
					arrayEquals(message, expected, actual);
				} catch (ArrayComparisonFailure e) {
					e.addDimension(i);
					throw e;
				}
			} else
				try {
					assertElementsEqual(expected, actual);
				} catch (AssertionError e) {
					throw new ArrayComparisonFailure(header, e, i);
				}
		}
		
		lengths.compareLengths(header);
	}

	private boolean isArray(Object expected) {
		return expected != null && expected.getClass().isArray();
	}
	
	private static class Lengths {
		public final int expected;
		public final int actual;
		public Lengths(Object expecteds, Object actuals) {
			expected = Array.getLength(expecteds);
			actual = Array.getLength(actuals);
		}
		public void compareLengths(String header) {
			if (expected != actual)
				Assert.fail(header + "array lengths differed, expected.length="
						+ expected + " actual.length=" + actual);			
		}
		public int min() {
			return Math.min(expected, actual);
		}		
	}

	private Lengths assertArraysAreSameLength(Object expecteds,
			Object actuals, String header) {
		if (expecteds == null)
			Assert.fail(header + "expected array was null");
		if (actuals == null)
			Assert.fail(header + "actual array was null");
		return new Lengths(expecteds, actuals);
	}

	protected abstract void assertElementsEqual(Object expected, Object actual);
}
