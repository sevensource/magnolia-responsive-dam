package org.sevensource.magnolia.responsivedam.field.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.sevensource.magnolia.responsivedam.configuration.DamVariation;
import org.sevensource.magnolia.responsivedam.configuration.DamVariationSet;
import org.sevensource.magnolia.responsivedam.configuration.ResponsiveDamConfiguration;
import org.sevensource.magnolia.responsivedam.focusarea.FocusAreas;

import com.vaadin.v7.data.validator.AbstractValidator;



public abstract class AbstractAspectAwareFieldValidator<T> extends AbstractValidator<T>{

	private transient List<DamVariationSet> damVariationSets;

	public AbstractAspectAwareFieldValidator(String errorMessage) {
		super(errorMessage);
	}

	protected void setDamVariationSets(List<DamVariationSet> damVariationSets) {
		this.damVariationSets = damVariationSets;
	}

	protected static List<DamVariationSet> transformDamVariationSets(ResponsiveDamConfiguration responsiveDamConfiguration, List<String> variationSets) {
		if(variationSets == null || variationSets.isEmpty()) {
			return Collections.emptyList();
		}

		final List<DamVariationSet> damVariationSets = variationSets
				.stream()
				.map(name -> {
					final DamVariationSet variationSet = responsiveDamConfiguration.getVariationSet(name);
					if(variationSet == null) {
						throw new IllegalArgumentException("Unknown variationSet " + name);
					}
					return variationSet;
				})
				.collect(Collectors.toList());
		return damVariationSets;
	}

	protected boolean isValidFocusAreas(FocusAreas focusAreas) {
		return getMissingVariations(damVariationSets, focusAreas).isEmpty();
	}

	public static List<DamVariation> getMissingVariations(List<DamVariationSet> damVariationSets, FocusAreas focusAreas) {
		if(damVariationSets == null || damVariationSets.isEmpty()) {
			return Collections.emptyList();
		}

		final List<DamVariation> missing = new ArrayList<>();

		for(DamVariationSet damVariationSet : damVariationSets) {

			for(DamVariation damVariation : damVariationSet.getVariations()) {
				if(focusAreas == null || focusAreas.getFocusArea(damVariation) == null) {
					missing.add(damVariation);
				}
			}
		}

		return missing;
	}
}
