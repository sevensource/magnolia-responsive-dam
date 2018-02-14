package org.sevensource.magnolia.responsivedam.field.focusareaselection;

import org.sevensource.magnolia.responsivedam.focusarea.FocusAreas;

public interface FocusAreaSelectionCompletedListener {
	void completed(boolean canceled, FocusAreas focusAreas);
}