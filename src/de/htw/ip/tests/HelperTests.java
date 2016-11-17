package de.htw.ip.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import de.htw.ip.basics.AbsoluteDirection;
import de.htw.ip.basics.RelativeDirection;
import de.htw.ip.potrace.ContourAlgorithm;

public class HelperTests {

	@Test
	public void test() {
		assertEquals(AbsoluteDirection.LEFT, ContourAlgorithm.mapRelativeToAbsolutePosition(AbsoluteDirection.TOP, RelativeDirection.LEFT));
		assertEquals(AbsoluteDirection.TOP, ContourAlgorithm.mapRelativeToAbsolutePosition(AbsoluteDirection.TOP, RelativeDirection.STRAIGHT));
		assertEquals(AbsoluteDirection.RIGHT, ContourAlgorithm.mapRelativeToAbsolutePosition(AbsoluteDirection.TOP, RelativeDirection.RIGHT));
		
		assertEquals(AbsoluteDirection.BOTTOM, ContourAlgorithm.mapRelativeToAbsolutePosition(AbsoluteDirection.LEFT, RelativeDirection.LEFT));
		assertEquals(AbsoluteDirection.LEFT, ContourAlgorithm.mapRelativeToAbsolutePosition(AbsoluteDirection.LEFT, RelativeDirection.STRAIGHT));
		assertEquals(AbsoluteDirection.TOP, ContourAlgorithm.mapRelativeToAbsolutePosition(AbsoluteDirection.LEFT, RelativeDirection.RIGHT));
		
		assertEquals(AbsoluteDirection.RIGHT, ContourAlgorithm.mapRelativeToAbsolutePosition(AbsoluteDirection.BOTTOM, RelativeDirection.LEFT));
		assertEquals(AbsoluteDirection.BOTTOM, ContourAlgorithm.mapRelativeToAbsolutePosition(AbsoluteDirection.BOTTOM, RelativeDirection.STRAIGHT));
		assertEquals(AbsoluteDirection.LEFT, ContourAlgorithm.mapRelativeToAbsolutePosition(AbsoluteDirection.BOTTOM, RelativeDirection.RIGHT));
		
		assertEquals(AbsoluteDirection.TOP, ContourAlgorithm.mapRelativeToAbsolutePosition(AbsoluteDirection.RIGHT, RelativeDirection.LEFT));
		assertEquals(AbsoluteDirection.RIGHT, ContourAlgorithm.mapRelativeToAbsolutePosition(AbsoluteDirection.RIGHT, RelativeDirection.STRAIGHT));
		assertEquals(AbsoluteDirection.BOTTOM, ContourAlgorithm.mapRelativeToAbsolutePosition(AbsoluteDirection.RIGHT, RelativeDirection.RIGHT));
	}

}
