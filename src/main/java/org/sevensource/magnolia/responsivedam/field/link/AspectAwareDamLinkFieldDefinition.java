package org.sevensource.magnolia.responsivedam.field.link;

import java.util.List;

import info.magnolia.ui.form.field.definition.LinkFieldDefinition;

public class AspectAwareDamLinkFieldDefinition extends LinkFieldDefinition {
	private List<String> variationSets;
	private String aspectsAppName;

	public List<String> getVariationSets() {
		return variationSets;
	}

	public void setVariationSets(List<String> variationSets) {
		this.variationSets = variationSets;
	}

	public String getAspectsAppName() {
		return aspectsAppName;
	}

	public void setAspectsAppName(String aspectsAppName) {
		this.aspectsAppName = aspectsAppName;
	}
}
