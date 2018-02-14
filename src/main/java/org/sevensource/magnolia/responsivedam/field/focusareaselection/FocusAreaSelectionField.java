package org.sevensource.magnolia.responsivedam.field.focusareaselection;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.sevensource.magnolia.responsivedam.configuration.DamVariation;
import org.sevensource.magnolia.responsivedam.focusarea.FocusArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Component;

import info.magnolia.ui.mediaeditor.field.image.ImageMediaField;
import info.magnolia.ui.vaadin.editor.JCropField;
import info.magnolia.ui.vaadin.gwt.shared.jcrop.SelectionArea;

public class FocusAreaSelectionField extends ImageMediaField {

	private static final Logger logger = LoggerFactory.getLogger(FocusAreaSelectionField.class);

	private DamVariation selectionVariation;
	private Double aspectRatio;
	private transient FocusArea focusArea;
	private int minWidth = 0;
	private int minHeight = 0;

	private SelectionArea selectedArea = null;

	private final JCropField jcropField = new JCropField();

	public FocusAreaSelectionField(FocusAreaSelectedListener listener) {
		super();
		jcropField.setBackgroundOpacity(0.3);
		jcropField.addValueChangeListener(event -> {
			if (listener != null) {
				selectedArea = (SelectionArea) event.getProperty().getValue();
				listener.onAreaSelected(selectionVariation, selectedArea);
			} else {
				logger.warn("No listener specified");
			}
		});
	}

	@Override
	protected Component createImage() {
		return jcropField;
	}

	@Override
	public void refreshImageSource() {
		try {
			final BufferedImage image = ImageIO.read(new ByteArrayInputStream(getValue()));
			jcropField.setTrueHeight(image.getHeight());
			jcropField.setTrueWidth(image.getWidth());
			jcropField.setImageSource(createResourceFromValue());

			updateJcropOptions();
		} catch (IOException e) {
			logger.error("Error reading the image data: " + e.getMessage(), e);
		}
	}

	private void updateJcropOptions() {
		if (selectionVariation == null) {
			jcropField.setAspectRatio(0);
			jcropField.select(new SelectionArea());
			jcropField.setEnabled(false);
		} else {
			jcropField.setEnabled(true);

			if (aspectRatio != null && aspectRatio > 0) {
				jcropField.setAspectRatio(aspectRatio);
			} else {
				jcropField.setAspectRatio(0);
			}

			if (focusArea != null) {
				SelectionArea preselected = new SelectionArea(focusArea.getX(), focusArea.getY(), focusArea.getWidth(),
						focusArea.getHeight());

				jcropField.select(preselected);
			} else {
				jcropField.select(new SelectionArea());
			}

			jcropField.setMinWidth(minWidth);
			jcropField.setMinHeight(minHeight);
		}
	}

	public void setAreaSelectOptions(DamVariation variation, FocusArea preSelected) {
		this.selectionVariation = variation;
		this.aspectRatio = variation.getRatio();
		this.focusArea = preSelected;

		switch (variation.getConstraints().getMinimumSize().getDimension()) {
		case HEIGHT:
			this.minWidth = 0;
			this.minHeight = variation.getConstraints().getMaximumSize().getValue();
			break;
		case WIDTH:
			this.minWidth = variation.getConstraints().getMaximumSize().getValue();
			this.minHeight = 0;
			break;
		default:
			this.minWidth = 0;
			this.minHeight = 0;
		}

		updateJcropOptions();
	}

	public void setStatusComponent(Component c) {
		jcropField.setStatusComponent(c);
	}

	@Override
	protected BufferedImage executeImageModification() throws IOException {
		return null;
	}
}
