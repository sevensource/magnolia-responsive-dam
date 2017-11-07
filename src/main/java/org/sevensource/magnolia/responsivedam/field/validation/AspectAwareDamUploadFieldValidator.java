package org.sevensource.magnolia.responsivedam.field.validation;

import java.util.List;
import java.util.stream.Collectors;

import org.sevensource.magnolia.responsivedam.configuration.DamVariationSet;
import org.sevensource.magnolia.responsivedam.configuration.ResponsiveDamConfiguration;
import org.sevensource.magnolia.responsivedam.field.upload.AspectAwareAssetUploadReceiver;
import org.sevensource.magnolia.responsivedam.field.upload.AspectAwareDamUploadFieldDefinition;

public class AspectAwareDamUploadFieldValidator
		extends AbstractAspectAwareFieldValidator<AspectAwareAssetUploadReceiver> {

	public AspectAwareDamUploadFieldValidator(ResponsiveDamConfiguration responsiveDamConfiguration,
			AspectAwareDamUploadFieldDefinition fieldDefinition, String errorMessage) {
		super(errorMessage);

		if(fieldDefinition.getVariationSets() != null) {
			final List<DamVariationSet> damVariationSets = fieldDefinition.getVariationSets()
					.stream()
					.map(name -> responsiveDamConfiguration.getVariationSet(name))
					.collect(Collectors.toList());
			setDamVariationSets(damVariationSets);
		}
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
