package org.sevensource.magnolia.responsivedam.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;


public class ResponsiveDamConfigurationTest {

	@Test
	public void test_get_variation_set() {
		ResponsiveDamConfiguration c = new ResponsiveDamConfiguration();
		assertThat(c.getVariationSet("test")).isNull();

		final Set<DamVariationSet> variationSets = new HashSet<>();
		variationSets.add(new DamVariationSet("test"));

		c.setVariationSets(variationSets);
		assertThat(c.getVariationSet("test")).isNotNull();

		assertThat(c.getVariationSet("")).isNull();
		assertThat(c.getVariationSet(null)).isNull();
	}

	@Test
	public void test_get_variation() {
		ResponsiveDamConfiguration c = new ResponsiveDamConfiguration();
		assertThat(c.getVariation("testset", "test")).isNull();

		final DamVariation variation = new DamVariation();
		variation.setName("test");
		final DamVariationSet variationSet = new DamVariationSet("testset");
		variationSet.setVariations(Arrays.asList(variation));
		final Set<DamVariationSet> variationSets = new HashSet<>(Arrays.asList(variationSet));

		c.setVariationSets(variationSets);

		assertThat(c.getVariation("", "bogus")).isNull();
		assertThat(c.getVariation(null, "bogus")).isNull();
		assertThat(c.getVariation("testset", null)).isNull();
		assertThat(c.getVariation("testset", "")).isNull();
		assertThat(c.getVariation("testset", "bogus")).isNull();
		assertThat(c.getVariation("testset", "test")).isNotNull();
	}
}
