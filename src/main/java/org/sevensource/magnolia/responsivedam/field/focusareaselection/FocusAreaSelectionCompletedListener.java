package org.sevensource.magnolia.responsivedam.field.focusareaselection;

import org.sevensource.magnolia.responsivedam.field.model.FocusAreas;

public interface FocusAreaSelectionCompletedListener {
	public void completed(boolean canceled, FocusAreas focusAreas);
}