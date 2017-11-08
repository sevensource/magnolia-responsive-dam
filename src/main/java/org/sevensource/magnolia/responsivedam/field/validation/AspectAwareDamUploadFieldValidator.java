package org.sevensource.magnolia.responsivedam.field.validation;

import java.util.List;

import org.sevensource.magnolia.responsivedam.configuration.DamVariationSet;
import org.sevensource.magnolia.responsivedam.configuration.ResponsiveDamConfiguration;
import org.sevensource.magnolia.responsivedam.field.upload.AspectAwareAssetUploadReceiver;
import org.sevensource.magnolia.responsivedam.field.upload.AspectAwareDamUploadFieldDefinition;

public class AspectAwareDamUploadFieldValidator
		extends AbstractAspectAwareFieldValidator<AspectAwareAssetUploadReceiver> {

	public AspectAwareDamUploadFieldValidator(ResponsiveDamConfiguration responsiveDamConfiguration,
			AspectAwareDamUploadFieldDefinition fieldDefinition, String errorMessage) {
		super(errorMessage);

		final List<DamVariationSet> damVariationSets = transformDamVariationSets(responsiveDamConfiguration, fieldDefinition.getVariationSets());
		setDamVariationSets(damVariationSets);
	}

	@Override
	protected boolean isValidValue(AspectAwareAssetUploadReceiver value) {
		if (value == null || !value.isImage()) {
			return true;
		}

		return isValidFocusAreas(value.getFocusAreas());
	}

	@Override
	public Class<AspectAwareAssetUploadReceiver> getType() {
		return AspectAwareAssetUploadReceiver.class;
	}
}
