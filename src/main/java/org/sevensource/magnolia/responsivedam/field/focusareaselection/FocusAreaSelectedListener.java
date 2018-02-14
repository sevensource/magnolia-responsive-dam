package org.sevensource.magnolia.responsivedam.field.focusareaselection;

import org.sevensource.magnolia.responsivedam.configuration.DamVariation;

import info.magnolia.ui.vaadin.gwt.shared.jcrop.SelectionArea;

public interface FocusAreaSelectedListener {
	public void onAreaSelected(DamVariation variation, SelectionArea selectedArea);
}
