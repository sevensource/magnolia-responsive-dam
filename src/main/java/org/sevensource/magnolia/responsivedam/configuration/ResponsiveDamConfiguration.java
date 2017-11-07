package org.sevensource.magnolia.responsivedam.configuration;

import java.util.List;
import java.util.Set;

import javax.inject.Singleton;

import org.apache.commons.lang3.StringUtils;

import info.magnolia.imaging.OutputFormat;

@Singleton
public class ResponsiveDamConfiguration {

	private final static String DEFAULT_MIME_TYPE = "image/*";

	private Set<DamVariationSet> variationSets;
	private List<OutputFormatMapping> outputFormatMappings;

	public DamVariationSet getVariationSet(String name) {
		if(StringUtils.isEmpty(name)) {
			return null;
		}

		return variationSets
			.stream()
			.filter(i -> name.equals(i.getName()))
			.findFirst()
			.orElse(null);
	}

	public DamVariation getVariation(String variationSet, String variation) {
		final DamVariationSet damVariationSet = getVariationSet(variationSet);
		return damVariationSet==null ? null : damVariationSet.getVariation(variation);
	}


	public List<OutputFormat> getOutputFormatsByMimeType(String mimeType) {
		final String sourceMimeType = StringUtils.isEmpty(mimeType) ? DEFAULT_MIME_TYPE : mimeType;

		final List<OutputFormat> retVal = outputFormatMappings
			.stream()
			.filter(m -> sourceMimeType.equalsIgnoreCase(m.getSourceMimeType()))
			.map(OutputFormatMapping::getOutputFormats)
			.findFirst()
			.orElse(null);

		if(retVal == null && ! DEFAULT_MIME_TYPE.equalsIgnoreCase(mimeType)) {
			return getOutputFormatsByMimeType(DEFAULT_MIME_TYPE);
		}

		return retVal;
	}

	public void setVariationSets(Set<DamVariationSet> variationSets) {
		this.variationSets = variationSets;
	}

	public Set<DamVariationSet> getVariationSets() {
		return variationSets;
	}

	public List<OutputFormatMapping> getOutputFormatMappings() {
		return outputFormatMappings;
	}

	public void setOutputFormatMappings(List<OutputFormatMapping> outputFormatMappings) {
		this.outputFormatMappings = outputFormatMappings;
	}
}
