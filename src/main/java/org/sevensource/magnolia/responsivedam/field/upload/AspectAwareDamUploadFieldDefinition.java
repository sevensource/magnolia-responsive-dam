package org.sevensource.magnolia.responsivedam.field.upload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.magnolia.dam.app.ui.field.definition.DamUploadFieldDefinition;
import info.magnolia.ui.form.field.transformer.Transformer;

public class AspectAwareDamUploadFieldDefinition extends DamUploadFieldDefinition {
	
	private static final Logger logger = LoggerFactory.getLogger(AspectAwareDamUploadFieldDefinition.class);
	
	private String variation;
	
	public AspectAwareDamUploadFieldDefinition() {
		super();
		setTransformerClass((Class<? extends Transformer<?>>) (Object) AspectAwareAssetTransformer.class);
	}
	
	public String getVariation() {
		return variation;
	}
	
	public void setVariation(String variation) {
		this.variation = variation;
	}
}
