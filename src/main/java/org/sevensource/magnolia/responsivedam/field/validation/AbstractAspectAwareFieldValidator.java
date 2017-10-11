package org.sevensource.magnolia.responsivedam.field.validation;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.sevensource.magnolia.responsivedam.configuration.DamVariationSet;
import org.sevensource.magnolia.responsivedam.configuration.DamVariationSpecification;
import org.sevensource.magnolia.responsivedam.field.FocusAreas;

import com.vaadin.data.validator.AbstractValidator;

public abstract class AbstractAspectAwareFieldValidator<T> extends AbstractValidator<T>{

	private transient DamVariationSet damVariationSet;
	
	public AbstractAspectAwareFieldValidator(String errorMessage) {
		super(errorMessage);
	}
	
	protected void setDamVariationSet(DamVariationSet damVariationSet) {
		this.damVariationSet = damVariationSet;
	}
	
	protected boolean isValidFocusAreas(FocusAreas focusAreas) {
		if(damVariationSet == null) {
			return true;
		}
		
		final Set<String> requiredAspects = damVariationSet
			.getSpecifications()
			.stream()
			.map(DamVariationSpecification::getName)
			.collect(Collectors.toSet());
		
		if(requiredAspects.isEmpty()) {
			return true;
		}
			
		final Set<String> specifiedAspects;
		if(focusAreas != null && focusAreas.getAreas() != null) {
			specifiedAspects = focusAreas.getAreas().keySet();
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
}
