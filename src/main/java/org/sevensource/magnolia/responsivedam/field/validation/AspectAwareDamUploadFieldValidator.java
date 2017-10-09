package org.sevensource.magnolia.responsivedam.field.validation;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.sevensource.magnolia.responsivedam.ResponsiveDamModule;
import org.sevensource.magnolia.responsivedam.configuration.DamVariationSet;
import org.sevensource.magnolia.responsivedam.configuration.DamVariationSpecification;
import org.sevensource.magnolia.responsivedam.field.upload.AspectAwareAssetUploadReceiver;
import org.sevensource.magnolia.responsivedam.field.upload.AspectAwareDamUploadFieldDefinition;

import com.vaadin.data.validator.AbstractValidator;

public class AspectAwareDamUploadFieldValidator extends AbstractValidator<AspectAwareAssetUploadReceiver> {
	
	private final transient AspectAwareDamUploadFieldDefinition definition;
	private final transient DamVariationSet damVariationSet;
	
	public AspectAwareDamUploadFieldValidator(ResponsiveDamModule responsiveDamModule, AspectAwareDamUploadFieldDefinition fieldDefinition, String errorMessage) {
		super(errorMessage);
		
		this.definition = fieldDefinition;
		this.damVariationSet = responsiveDamModule.getConfiguredVariation(definition.getVariation());
	}

	@Override
	protected boolean isValidValue(AspectAwareAssetUploadReceiver value) {
		if(value == null || ! value.isImage()) {
			return true;
		}
		
		final Set<String> requiredAspects = damVariationSet
			.getSpecifications()
			.stream()
			.map(DamVariationSpecification::getName)
			.collect(Collectors.toSet());
		
		//final Set<String> requiredAspects = definition.getRequiredAspects().keySet();
		if(requiredAspects.isEmpty()) {
			return true;
		}
			
		final Set<String> specifiedAspects;
		if(value.getFocusAreas() != null && value.getFocusAreas().getAreas() != null) {
			specifiedAspects = value.getFocusAreas().getAreas().keySet();
		} else {
			specifiedAspects = Collections.emptySet();
		}
		
		for(String requiredAspect : requiredAspects) {
			if(!specifiedAspects.contains(requiredAspect)) {
				return false;
			}
		}
		
		return true;
	}
	

	@Override
	public Class<AspectAwareAssetUploadReceiver> getType() {
		return AspectAwareAssetUploadReceiver.class;
	}
}
