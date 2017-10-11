package org.sevensource.magnolia.responsivedam;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang3.StringUtils;
import org.sevensource.magnolia.responsivedam.configuration.DamSizeConstraints;
import org.sevensource.magnolia.responsivedam.configuration.DamVariationSet;
import org.sevensource.magnolia.responsivedam.configuration.DamVariationSpecification;
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
	
	private Set<DamVariationSet> configuredVariations;

	
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
	
	public void setConfiguredVariations(Set<DamVariationSet> configuredVariations) {
		this.configuredVariations = configuredVariations;
	}
	
	public Set<DamVariationSet> getConfiguredVariations() {
		return configuredVariations;
	}
	
	public DamVariationSet getConfiguredVariation(String name) {
		if(StringUtils.isEmpty(name)) {
			return null;
		}
		
		return configuredVariations
			.stream()
			.filter(i -> name.equals(i.getName()))
			.findFirst()
			.orElse(null);		
	}
	
	
	@Override
	public void start(ModuleLifecycleContext moduleLifecycleContext) {
		
		if(configuredVariations != null) {
			this.configuredVariations.clear();
		} else {
			this.configuredVariations = new HashSet<>();
		}

		this.configuredVariations.addAll(convertVariations(this.variations, this.defaultConstraint));
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
					DamVariationSpecification specification = new DamVariationSpecification();
					BeanUtils.populate(specification, variationEntry.getValue());
					
					if(StringUtils.isEmpty(specification.getName())) {
						specification.setName(variationId);
					}
					
					assignResponsiveSizeConstraintsDefaults(specification.getConstraints(), defaultConstraints);
					
					damVariationSet.addSpecification(specification);
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