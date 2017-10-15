package org.sevensource.magnolia.responsivedam.configuration;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class DamVariationSet {
	private final String name;
	private List<DamVariation> variations;
	
	public DamVariationSet(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public List<DamVariation> getVariations() {
		return variations;
	}
	
	public DamVariation getVariation(String name) {
		if(variations == null || StringUtils.isBlank(name)) {
			return null;
		}
		return variations
			.stream()
			.filter(i -> name.equals(i.getName()))
			.findFirst()
			.orElse(null);
	}
	
	public void setVariations(List<DamVariation> variations) {
		this.variations = variations;
	}
	
	public void addVariation(DamVariation variation) {
		if(this.variations == null) {
			this.variations = new ArrayList<>();
		}
		this.variations.add(variation);
	}
}
