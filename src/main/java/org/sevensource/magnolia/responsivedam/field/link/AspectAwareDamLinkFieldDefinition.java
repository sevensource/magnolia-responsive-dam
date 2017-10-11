package org.sevensource.magnolia.responsivedam.field.link;

import info.magnolia.ui.form.field.definition.LinkFieldDefinition;

public class AspectAwareDamLinkFieldDefinition extends LinkFieldDefinition {
	private String variation;
	private String aspectsAppName;
	
	
	public String getVariation() {
		return variation;
	}
	
	public void setVariation(String variation) {
		this.variation = variation;
	}
	
	public String getAspectsAppName() {
		return aspectsAppName;
	}
	
	public void setAspectsAppName(String aspectsAppName) {
		this.aspectsAppName = aspectsAppName;
	}
}
