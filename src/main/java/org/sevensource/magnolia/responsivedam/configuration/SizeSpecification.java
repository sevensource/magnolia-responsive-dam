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
			
			return (T) SizeSpecification.of((String) value);
		}
	}
	
	
	public enum ResponsiveDamSizeDimension {
		WIDTH("w"), HEIGHT("h");
		
		private final String id;
		
		private ResponsiveDamSizeDimension(String id) {
			this.id = id;
		}

		public static ResponsiveDamSizeDimension of(String spec) {
			for(ResponsiveDamSizeDimension d : ResponsiveDamSizeDimension.values()) {
				if(d.getId().equals(spec)) {
					return d;
				}
			}
			
			throw new IllegalArgumentException("Unknown size spec " + spec);
		}
		
		public String getId() {
			return id;
		}
	}
	

	public SizeSpecification(Integer value, ResponsiveDamSizeDimension dimension) {
		
		if(value == null || value < 1) {
			throw new IllegalArgumentException("Illegal value");
		}
		
		if(dimension == null) {
			throw new IllegalArgumentException("Illegal dimension");
		}
		
		this.value = value;
		this.dimension = dimension;
	}

	public static SizeSpecification of(String spec) {
		final String dimensionSpec = spec.substring(spec.length() - 1, spec.length());
		ResponsiveDamSizeDimension pDimension = ResponsiveDamSizeDimension.of(dimensionSpec);

		final Integer pValue = Integer.valueOf(spec.substring(0, spec.length() - 1));

		return new SizeSpecification(pValue, pDimension);
	}

	public ResponsiveDamSizeDimension getDimension() {
		return dimension;
	}

	public Integer getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return value + dimension.getId();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dimension == null) ? 0 : dimension.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		SizeSpecification other = (SizeSpecification) obj;
		if (dimension != other.dimension) {
			return false;
		}
		if (value == null) {
			if (other.value != null) {
				return false;
			}
		} else if (!value.equals(other.value)) {
			return false;
		}
		return true;
	}
}