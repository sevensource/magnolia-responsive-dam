package org.sevensource.magnolia.responsivedam.field.focusareaselection.action;

import org.sevensource.magnolia.responsivedam.configuration.DamVariation;

import info.magnolia.ui.api.action.ConfiguredActionDefinition;

public class SelectionActionDefinition extends ConfiguredActionDefinition  {
	
	private final DamVariation variation;
	
	public SelectionActionDefinition(DamVariation variation) {
		this.variation = variation;
	}
	
	public DamVariation getVariation() {
		return variation;
	}
}
