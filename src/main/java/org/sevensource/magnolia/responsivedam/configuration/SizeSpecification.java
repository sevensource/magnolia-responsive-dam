package org.sevensource.magnolia.responsivedam.configuration;

import org.apache.commons.beanutils.Converter;

public class SizeSpecification {
	private final Integer value;
	private final ResponsiveDamSizeDimension dimension;

	public static class SizeSpecificationConverter implements Converter {
		@Override
		public <T> T convert(Class<T> type, Object value) {
			if(! (value instanceof String)) {
				throw new IllegalArgumentException("value is of type " + value.getClass().getName());
			} else if(! SizeSpecification.class.isAssignableFrom(type)) {
				throw new IllegalArgumentException("type is of type " + type.getName());
			}
			
			return (T) new SizeSpecification((String) value);
		}
	}
	
	
	public enum ResponsiveDamSizeDimension {
		WIDTH, HEIGHT;

		public static ResponsiveDamSizeDimension of(String spec) {
			if (spec.equals("w")) {
				return WIDTH;
			} else if (spec.equals("h")) {
				return HEIGHT;
			} else {
				throw new IllegalArgumentException("Unknown size spec " + spec);
			}
		}
	}
	

	public SizeSpecification(Integer value, ResponsiveDamSizeDimension dimension) {
		this.value = value;
		this.dimension = dimension;
	}

	public SizeSpecification(String spec) {
		final String dimensionSpec = spec.substring(spec.length() - 1, spec.length());
		ResponsiveDamSizeDimension pDimension = ResponsiveDamSizeDimension.of(dimensionSpec);

		final Integer pValue = Integer.valueOf(spec.substring(0, spec.length() - 1));

		this.value = pValue;
		this.dimension = pDimension;
	}

	public ResponsiveDamSizeDimension getDimension() {
		return dimension;
	}

	public Integer getValue() {
		return value;
	}
}