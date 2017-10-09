package org.sevensource.magnolia.responsivedam.field.focusareaselection.action;

import info.magnolia.ui.api.action.ConfiguredActionDefinition;

public class SelectionActionDefinition extends ConfiguredActionDefinition  {
	
	private final String aspectName;
	private final double aspectRatio;
	
	public SelectionActionDefinition(String aspectName, double aspectRatio) {
		this.aspectName = aspectName;
		this.aspectRatio = aspectRatio;
	}
	
	public String getAspectName() {
		return aspectName;
	}
	
	public double getAspectRatio() {
		return aspectRatio;
	}
}
