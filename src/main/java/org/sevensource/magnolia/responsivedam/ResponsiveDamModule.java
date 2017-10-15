package org.sevensource.magnolia.responsivedam;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang3.StringUtils;
import org.sevensource.magnolia.responsivedam.configuration.DamSizeConstraints;
import org.sevensource.magnolia.responsivedam.configuration.DamVariation;
import org.sevensource.magnolia.responsivedam.configuration.DamVariationSet;
import org.sevensource.magnolia.responsivedam.configuration.SizeSpecification;
import org.sevensource.magnolia.responsivedam.configuration.SizeSpecification.SizeSpecificationConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.magnolia.module.ModuleLifecycle;
import info.magnolia.module.ModuleLifecycleContext;

public class ResponsiveDamModule implements ModuleLifecycle {
	
	private static final Logger logger = LoggerFactory.getLogger(ResponsiveDamModule.class);
	

	private Map<String, Map<String, Map<String, String>>> variations;
	private DamSizeConstraints defaultConstraint;
	
	private Set<DamVariationSet> configuredVariationSets;

	
	public DamSizeConstraints getDefaultConstraint() {
		return defaultConstraint;
	}

	public void setDefaultConstraint(DamSizeConstraints defaultConstraint) {
		this.defaultConstraint = defaultConstraint;
	}
	
	public Map<String, Map<String, Map<String, String>>> getVariations() {
		return variations;
	}
	
	public void setVariations(Map<String, Map<String, Map<String, String>>> variations) {
		this.variations = variations;
	}
	
	///////
	
	public void setConfiguredVariationSets(Set<DamVariationSet> variationSets) {
		this.configuredVariationSets = variationSets;
	}
	
	public Set<DamVariationSet> getConfiguredVariationSets() {
		return configuredVariationSets;
	}
	
	public DamVariationSet getConfiguredVariationSet(String name) {
		if(StringUtils.isEmpty(name)) {
			return null;
		}
		
		return configuredVariationSets
			.stream()
			.filter(i -> name.equals(i.getName()))
			.findFirst()
			.orElse(null);		
	}
	
	public DamVariation getConfiguredVariation(String variationSet, String variation) {
		final DamVariationSet set = getConfiguredVariationSet(variationSet);
		return set==null ? null : set.getVariation(variation);
	}
	
	public DamVariation getAnyConfiguredVariation(String variationName) {
		
		DamVariation selectedVariation = null;
		
		for(DamVariationSet variationSet : configuredVariationSets) {
			final DamVariation variation = variationSet.getVariation(variationName);
			if(variation != null) {
				if(selectedVariation == null) {
					selectedVariation = variation;
				} else {
					final int selectedSize = selectedVariation.getConstraints().getMinimumSize().getValue();
					final int thisSize = variation.getConstraints().getMinimumSize().getValue();
					if(selectedSize < thisSize) {
						selectedVariation = variation;	
					}
				}
			}
		}
		
		return selectedVariation;
	}
	
	
	@Override
	public void start(ModuleLifecycleContext moduleLifecycleContext) {
		
		if(configuredVariationSets != null) {
			this.configuredVariationSets.clear();
		} else {
			this.configuredVariationSets = new HashSet<>();
		}

		this.configuredVariationSets.addAll(convertVariations(this.variations, this.defaultConstraint));
	}
	
	
	@Override
	public void stop(ModuleLifecycleContext moduleLifecycleContext) {
		//no-op
	}
	
	
	private static Set<DamVariationSet> convertVariations(Map<String, Map<String, Map<String, String>>> variations, DamSizeConstraints defaultConstraints) {
		
		if(null == ConvertUtils.lookup(SizeSpecification.class)) {
			ConvertUtils.register(new SizeSpecificationConverter(), SizeSpecification.class);
		}
		
		final Set<DamVariationSet> variationSets = new HashSet<>();
		
		for(Entry<String, Map<String, Map<String, String>>> entry : variations.entrySet()) {
			final String variationSetId = entry.getKey();
			final DamVariationSet damVariationSet = new DamVariationSet(variationSetId);
			variationSets.add(damVariationSet);
			
			final Map<String, Map<String, String>> variationSet = entry.getValue();
			
			for(Entry<String, Map<String, String>> variationEntry : variationSet.entrySet()) {
				final String variationId = variationEntry.getKey();
				
				try {
					DamVariation variation = new DamVariation();
					BeanUtils.populate(variation, variationEntry.getValue());
					
					if(StringUtils.isEmpty(variation.getName())) {
						variation.setName(variationId);
					}
					
					variation.setVariationSet(damVariationSet);
					
					assignResponsiveSizeConstraintsDefaults(variation.getConstraints(), defaultConstraints);
					
					damVariationSet.addVariation(variation);
				} catch (Exception e) {
					logger.error("Error whole parsing responsive dam variations", e);
					throw new IllegalArgumentException(e);
				}
			}
		}
		
		return variationSets;
	}
	
	private static void assignResponsiveSizeConstraintsDefaults(DamSizeConstraints target, DamSizeConstraints source) {
		if(source == null) {
			return;
		}
		
		if(target.getMaximumResolutions() == null) {
			target.setMaximumResolutions(source.getMaximumResolutions());
		}
		
		if(target.getMinimumResolutionSizeStep() == null) {
			target.setMinimumResolutionSizeStep(source.getMinimumResolutionSizeStep());
		}
		
		if(target.getMaximumSize() == null) {
			target.setMaximumSize(source.getMaximumSize());
		}
		
		if(target.getMinimumSize() == null) {
			target.setMinimumSize(source.getMinimumSize());
		}
	}
}