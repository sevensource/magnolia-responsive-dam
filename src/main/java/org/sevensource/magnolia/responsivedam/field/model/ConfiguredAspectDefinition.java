package org.sevensource.magnolia.responsivedam.field.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfiguredAspectDefinition {
	
	private static final Logger logger = LoggerFactory.getLogger(ConfiguredAspectDefinition.class);

	private String label;
	private boolean required = false;
	private String aspect;
	private Double ratio;

	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public boolean isRequired() {
		return required;
	}
	public void setRequired(boolean required) {
		this.required = required;
	}
	
	public void setAspect(String aspect) {
		this.aspect = aspect;
		if(this.ratio == null) {
			setRatio(parseRatio(aspect));
		} else {
			logger.warn("ratio ({}) and aspect ({}) are defined, not parsing aspect into ratio", ratio, aspect);
		}
	}
	
	public String getAspect() {
		return aspect;
	}
	
	public Double getRatio() {
		return ratio;
	}
	
	public void setRatio(Double ratio) {
		this.ratio = ratio;
	}
	
	private static Double parseRatio(String value) {
		double parsedValue = 0;
		
		if(value.contains(":")) {
			String[] aspectItems = value.split(":");
			if(aspectItems.length != 2) {
				throw new IllegalArgumentException("Cannot parse aspect into ratio: " + value);
			}
			
			final double w = Double.parseDouble(aspectItems[0]);
			final double h = Double.parseDouble(aspectItems[1]);
			parsedValue = w/h;
		} else {
			parsedValue = Double.parseDouble(value);
		}
		
		if (logger.isDebugEnabled()) {
			logger.debug("Parsed aspect to ratio {}=>{}", value, parsedValue);
		}
		return parsedValue;
	}
}
