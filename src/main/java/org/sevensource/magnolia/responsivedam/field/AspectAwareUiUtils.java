package org.sevensource.magnolia.responsivedam.field;

import com.vaadin.ui.Label;

public class AspectAwareUiUtils {

	private static final String INFO_LABEL_WARN_CLASS = "warn";
	private static final String INFO_LABEL_ERROR_CLASS = "error";

	public enum InfoLabelStyle {
		OK,
		WARN,
		ERROR;
	}

	public static void updateInfoLabel(Label label, String txt, InfoLabelStyle style) {
		if(label != null) {
			label.setValue(txt);

			switch(style) {
			case OK:
				label.removeStyleName(INFO_LABEL_WARN_CLASS);
				label.removeStyleName(INFO_LABEL_ERROR_CLASS);
				break;
			case WARN:
				label.addStyleName(INFO_LABEL_WARN_CLASS);
				label.removeStyleName(INFO_LABEL_ERROR_CLASS);
				break;
			case ERROR:
				label.removeStyleName(INFO_LABEL_WARN_CLASS);
				label.addStyleName(INFO_LABEL_ERROR_CLASS);
				break;
			}
		}
	}
}