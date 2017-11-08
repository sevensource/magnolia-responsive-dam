package org.sevensource.magnolia.responsivedam.configuration;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.sevensource.magnolia.responsivedam.configuration.SizeSpecification.SizeDimension;

public class SizeSpecificationTest {

	@Test
	public void test_parse() {
		SizeSpecification o = SizeSpecification.of("123w");
		assertTrue("should be 123", o.getValue().equals(123));
		assertTrue("should be WIDTH", o.getDimension().equals(SizeDimension.WIDTH));

		o = SizeSpecification.of("321h");
		assertTrue("should be 321", o.getValue().equals(321));
		assertTrue("should be HEIGHT", o.getDimension().equals(SizeDimension.HEIGHT));
	}
}
