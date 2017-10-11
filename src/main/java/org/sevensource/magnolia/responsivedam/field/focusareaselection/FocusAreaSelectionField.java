package org.sevensource.magnolia.responsivedam.field.focusareaselection;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.sevensource.magnolia.responsivedam.field.FocusArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Component;

import info.magnolia.ui.mediaeditor.field.image.ImageMediaField;
import info.magnolia.ui.vaadin.editor.JCropField;
import info.magnolia.ui.vaadin.gwt.shared.jcrop.SelectionArea;

public class FocusAreaSelectionField extends ImageMediaField  {

	private static final Logger logger = LoggerFactory.getLogger(FocusAreaSelectionField.class);
	
	private String selectionName;
	private Double aspectRatio;
	private transient FocusArea focusArea;
	
    private SelectionArea selectedArea = null;

    private final JCropField jcropField = new JCropField();
    
    public FocusAreaSelectionField(FocusAreaSelectedListener listener) {
    	super();
    	jcropField.setBackgroundOpacity(0.3);
    	jcropField.addValueChangeListener(event -> {
    		if(listener != null) {
	            selectedArea = (SelectionArea) event.getProperty().getValue();
	            listener.onAreaSelected(selectionName, selectedArea);
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
            log.error("Error reading the image data: " + e.getMessage(), e);
        }
    }
    
    private void updateJcropOptions() {
    	if(StringUtils.isEmpty(selectionName)) {
    		jcropField.setAspectRatio(0);
    		jcropField.select(new SelectionArea());
    		jcropField.setEnabled(false);
    	} else {
    		jcropField.setEnabled(true);
    		
            if(aspectRatio != null && aspectRatio > 0) {
            	jcropField.setAspectRatio(aspectRatio);
            } else {
            	jcropField.setAspectRatio(0);
            }
            
            if(focusArea != null) {
            	SelectionArea preselected = new SelectionArea(
            		focusArea.getX(), focusArea.getY(),
            		focusArea.getWidth(), focusArea.getHeight());
            	
            	jcropField.select(preselected);
            } else {
            	jcropField.select(new SelectionArea());
            }
    	}
    }
    
    public void setAreaSelectOptions(String name, Double aspectRatio, FocusArea preSelected) {
    	this.selectionName = name;
    	this.aspectRatio = aspectRatio;
    	this.focusArea = preSelected;
    	
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
