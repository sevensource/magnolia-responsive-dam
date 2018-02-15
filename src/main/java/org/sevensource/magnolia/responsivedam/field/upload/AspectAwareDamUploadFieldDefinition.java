package org.sevensource.magnolia.responsivedam.field.upload;

import java.util.List;

import info.magnolia.dam.app.ui.field.definition.DamUploadFieldDefinition;
import info.magnolia.ui.form.field.transformer.Transformer;

public class AspectAwareDamUploadFieldDefinition extends DamUploadFieldDefinition {

	private List<String> variationSets;
	private boolean useExistingFocusAreas = false;

	public AspectAwareDamUploadFieldDefinition() {
		super();
		setTransformerClass((Class<? extends Transformer<?>>) (Object) AspectAwareAssetTransformer.class);
	}

	public List<String> getVariationSets() {
		return variationSets;
	}

	public void setVariationSets(List<String> variationSets) {
		this.variationSets = variationSets;
	}

	public void setUseExistingFocusAreas(boolean useExistingFocusAreas) {
		this.useExistingFocusAreas = useExistingFocusAreas;
	}

	public boolean isUseExistingFocusAreas() {
		return useExistingFocusAreas;
	}
}
