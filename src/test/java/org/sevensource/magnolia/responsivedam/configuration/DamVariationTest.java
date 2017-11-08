package org.sevensource.magnolia.responsivedam.configuration;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DamVariationTest {

	@Test
	public void test_parse_aspect_ratio() {
		DamVariation variation = new DamVariation();
		variation.setAspect("1:1");
		assertTrue("ratio should be 1", variation.getRatio().equals(1d));

		variation.setAspect("1:2");
		assertTrue("ratio should be 0.5", variation.getRatio().equals(0.5d));

		variation.setAspect("2:1");
		assertTrue("ratio should be 2", variation.getRatio().equals(2d));
	}

	@Test
	public void test_parse_aspect_decimal() {
		DamVariation variation = new DamVariation();
		variation.setAspect("1");
		assertTrue("ratio should be 1", variation.getRatio().equals(1d));

		variation.setAspect("1.0");
		assertTrue("ratio should be 1", variation.getRatio().equals(1d));

		variation.setAspect("1.1");
		assertTrue("ratio should be 1.1", variation.getRatio().equals(1.1d));
	}

}
