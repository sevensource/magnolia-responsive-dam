package org.sevensource.magnolia.responsivedam.field.upload;

import java.io.OutputStream;

import javax.inject.Inject;

import org.sevensource.magnolia.responsivedam.focusarea.FocusAreas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.magnolia.dam.app.DamAppConfiguration;
import info.magnolia.dam.app.ui.field.upload.AssetUploadReceiver;
import info.magnolia.i18nsystem.SimpleTranslator;

public class AspectAwareAssetUploadReceiver extends AssetUploadReceiver {

	/**
	 *
	 */
	private static final long serialVersionUID = 3215719844406375059L;

	private static final Logger logger = LoggerFactory.getLogger(AspectAwareAssetUploadReceiver.class);

	private transient FocusAreas focusAreas = null;

	@Inject
	public AspectAwareAssetUploadReceiver(SimpleTranslator i18n, DamAppConfiguration damAppConfig) {
		super(i18n, damAppConfig);
	}

	@Override
	public OutputStream receiveUpload(String filename, String MIMEType) {
		if (this.getFile() != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("New Upload received, resetting Focus Area");
			}
			setFocusArea(null);
		}

		return super.receiveUpload(filename, MIMEType);
	}

	@Override
	public void setValue(Object newValue) {
		if (newValue == null) {
			setFocusArea(null);
		}

		super.setValue(newValue);
	}

	public void setFocusArea(FocusAreas focusAreas) {
		this.focusAreas = focusAreas;
	}

	public FocusAreas getFocusAreas() {
		return focusAreas;
	}
}
