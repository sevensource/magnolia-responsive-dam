package org.sevensource.magnolia.responsivedam.field.link;

import info.magnolia.ui.form.field.definition.LinkFieldDefinition;

public class AspectAwareDamLinkFieldDefinition extends LinkFieldDefinition {
	private String variationSet;
	private String aspectsAppName;
	
	
	public String getVariationSet() {
		return variationSet;
	}
	
	public void setVariationSet(String variation) {
		this.variationSet = variation;
	}
	
	public String getAspectsAppName() {
		return aspectsAppName;
	}
	
	public void setAspectsAppName(String aspectsAppName) {
		this.aspectsAppName = aspectsAppName;
	}
}
