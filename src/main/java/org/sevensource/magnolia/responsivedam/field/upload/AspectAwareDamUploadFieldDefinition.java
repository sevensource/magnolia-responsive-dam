package org.sevensource.magnolia.responsivedam.field.upload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.magnolia.dam.app.ui.field.definition.DamUploadFieldDefinition;
import info.magnolia.ui.form.field.transformer.Transformer;

public class AspectAwareDamUploadFieldDefinition extends DamUploadFieldDefinition {
	
	private static final Logger logger = LoggerFactory.getLogger(AspectAwareDamUploadFieldDefinition.class);
	
	private String variationSet;
	private boolean useExistingFocusAreas = false;
	
	public AspectAwareDamUploadFieldDefinition() {
		super();
		setTransformerClass((Class<? extends Transformer<?>>) (Object) AspectAwareAssetTransformer.class);
	}
	
	public String getVariationSet() {
		return variationSet;
	}
	
	public void setVariationSet(String variationSet) {
		this.variationSet = variationSet;
	}
	
	public void setUseExistingFocusAreas(boolean useExistingFocusAreas) {
		this.useExistingFocusAreas = useExistingFocusAreas;
	}
	
	public boolean isUseExistingFocusAreas() {
		return useExistingFocusAreas;
	}
}
