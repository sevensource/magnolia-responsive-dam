package org.sevensource.magnolia.responsivedam;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang3.StringUtils;
import org.sevensource.magnolia.responsivedam.configuration.DamSizeConstraints;
import org.sevensource.magnolia.responsivedam.configuration.DamVariation;
import org.sevensource.magnolia.responsivedam.configuration.DamVariationSet;
import org.sevensource.magnolia.responsivedam.configuration.OutputFormatMapping;
import org.sevensource.magnolia.responsivedam.configuration.ResponsiveDamConfiguration;
import org.sevensource.magnolia.responsivedam.configuration.SizeSpecification;
import org.sevensource.magnolia.responsivedam.configuration.SizeSpecification.SizeSpecificationConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import info.magnolia.imaging.OutputFormat;
import info.magnolia.module.ModuleLifecycle;
import info.magnolia.module.ModuleLifecycleContext;

public class ResponsiveDamModule implements ModuleLifecycle {
	
	private static final Logger logger = LoggerFactory.getLogger(ResponsiveDamModule.class);
	
	private Map<String, Map<String, Map<String, String>>> variations;
	private DamSizeConstraints defaultConstraint;
	private Map<String, Map<String, Object>> outputFormatMappings;
	
	private final ResponsiveDamConfiguration responsiveDamConfiguration;

	@Inject
	public ResponsiveDamModule(ResponsiveDamConfiguration responsiveDamConfiguration) {
		this.responsiveDamConfiguration = responsiveDamConfiguration;
	}
	
	@Override
	public void start(ModuleLifecycleContext moduleLifecycleContext) {
		final Set<DamVariationSet> variationSets = convertVariations(this.variations, this.defaultConstraint);
		responsiveDamConfiguration.setVariationSets(variationSets);
		
		final List<OutputFormatMapping> outputFormatMappingsList = convertOutputFormatMappings(outputFormatMappings);
		responsiveDamConfiguration.setOutputFormatMappings(outputFormatMappingsList);
	}
	
	@Override
	public void stop(ModuleLifecycleContext moduleLifecycleContext) {
		//no-op
	}
	
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
	

	public void setOutputFormatMappings(Map<String, Map<String, Object>> outputFormatMappings) {
		this.outputFormatMappings = outputFormatMappings;
	}
	
	public Map<String, Map<String, Object>> getOutputFormatMappings() {
		return outputFormatMappings;
	}
	
	private static List<OutputFormatMapping> convertOutputFormatMappings(Map<String, Map<String, Object>> outputFormatMappings) {
		final List<OutputFormatMapping> retVal = new ArrayList<>(5);
		
		for(Entry<String, Map<String, Object>> entry : outputFormatMappings.entrySet()) {
			final String name = entry.getKey();
			final Map<String, Object> mappingEntry = entry.getValue();
			
			final String sourceMimeType = (String) mappingEntry.get("source");
			OutputFormatMapping mapping = new OutputFormatMapping(name, sourceMimeType);
			retVal.add(mapping);
			
			final Map<String, Map<String, String>> outputMappings = (Map<String, Map<String, String>>) mappingEntry.get("output");
			for(Entry<String, Map<String, String>> outputMappingEntry : outputMappings.entrySet()) {
				final String outputName = outputMappingEntry.getKey();
				try {				
					OutputFormat outputFormat = new OutputFormat();
					BeanUtils.populate(outputFormat, outputMappingEntry.getValue());
					mapping.addOutputFormat(outputFormat);
				} catch(Exception e) {
					logger.error("Error whole parsing responsive outputFormatMappings with name {}", outputName, e);
					throw new IllegalArgumentException(e);
				}
			}
		}
		
		return retVal;
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