package org.sevensource.magnolia.responsivedam.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DamVariation {
	
	private static final Logger logger = LoggerFactory.getLogger(DamVariation.class);
	
	private String name;
	private DamVariationSet variationSet;
	private String aspect;
	private Double ratio;
	private final DamSizeConstraints constraints = new DamSizeConstraints();


	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAspect() {
		return aspect;
	}

	public void setAspect(String aspect) {
		this.aspect = aspect;
		this.ratio = parseRatio(aspect);
	}

	public Double getRatio() {
		return ratio;
	}

	public void setRatio(Double ratio) {
		this.ratio = ratio;
	}

	public DamSizeConstraints getConstraints() {
		return constraints;
	}
	
	public void setVariationSet(DamVariationSet variationSet) {
		this.variationSet = variationSet;
	}
	
	public DamVariationSet getVariationSet() {
		return variationSet;
	}

	private static Double parseRatio(String value) {
		double parsedValue = 0;

		if (value.contains(":")) {
			String[] aspectItems = value.split(":");
			if (aspectItems.length != 2) {
				throw new IllegalArgumentException("Cannot parse aspect into ratio: " + value);
			}

			final double w = Double.parseDouble(aspectItems[0]);
			final double h = Double.parseDouble(aspectItems[1]);
			parsedValue = w / h;
		} else {
			parsedValue = Double.parseDouble(value);
		}
		
		return parsedValue;
	}
}