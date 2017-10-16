package org.sevensource.magnolia.responsivedam.imaging;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.sevensource.magnolia.responsivedam.ResponsiveDamNodeUtil;
import org.sevensource.magnolia.responsivedam.configuration.DamSizeConstraints;
import org.sevensource.magnolia.responsivedam.configuration.DamVariation;
import org.sevensource.magnolia.responsivedam.configuration.ResponsiveDamConfiguration;
import org.sevensource.magnolia.responsivedam.configuration.SizeSpecification;
import org.sevensource.magnolia.responsivedam.configuration.SizeSpecification.ResponsiveDamSizeDimension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.magnolia.dam.jcr.AssetNodeTypes;
import info.magnolia.imaging.OutputFormat;
import info.magnolia.jcr.util.PropertyUtil;

public class ResponsiveDamVariation {
	
	private static final Logger logger = LoggerFactory.getLogger(ResponsiveDamVariation.class);
	
	private final Long width;
	private final Long height;
	private final String mimeType;
	private final DamSizeConstraints constraints;
	
	
	private final List<SizeSpecification> sizes;
	private final List<OutputFormat> outputFormats;
	private final List<ResponsiveDamRendition> renditions;
	
	public ResponsiveDamVariation(Node node, DamVariation damVariation, ResponsiveDamConfiguration responsiveDamConfiguration) {
		
		final Node contentNode;
		try {
			contentNode = ResponsiveDamNodeUtil.getContentNode(node);
		} catch(RepositoryException e) {
			throw new RuntimeException(e);
		}
		
        this.width = PropertyUtil.getLong(contentNode, AssetNodeTypes.AssetResource.WIDTH);
        this.height = PropertyUtil.getLong(contentNode, AssetNodeTypes.AssetResource.HEIGHT);
        this.mimeType = PropertyUtil.getString(contentNode, AssetNodeTypes.AssetResource.MIMETYPE);
        
        
        if(this.width == null || this.height == null || this.width.equals(0L) || this.height.equals(0L)) {
        	throw new IllegalArgumentException("Invalid width/height value");
        }
        
        this.constraints = damVariation.getConstraints();
        this.sizes = initSizes();
		this.outputFormats = responsiveDamConfiguration.getOutputFormatsByMimeType(mimeType);
		
		this.renditions = new ArrayList<>();
		
		for(SizeSpecification size : sizes) {
			for(OutputFormat format : outputFormats) {
				renditions.add(new ResponsiveDamRendition(contentNode, damVariation.getVariationSet().getName(), damVariation.getName(), size, format));
			}
		}
	}

	
	public List<ResponsiveDamRendition> getRenditions() {
		return renditions;
	}
	
	public List<ResponsiveDamRendition> getRenditionsByOutputFormat(String outputFormat) {
		return renditions
				.stream()
				.filter(r -> r.getOutputFormat().getFormatName().equals(outputFormat))
				.collect(Collectors.toList());
	}
	
	public ResponsiveDamRendition getDefaultRendition() {
		return renditions.get(0);
	}
	
	public ResponsiveDamRendition getDefaultRenditionForSize(Integer size) {
		final ResponsiveDamRendition rendition = getDefaultRendition();
		final SizeSpecification newSize = new SizeSpecification(size, rendition.getSize().getDimension());
		
		return new ResponsiveDamRendition(
				rendition.getNode(),
				rendition.getVariationSet(),
				rendition.getVariation(),
				newSize,
				rendition.getOutputFormat());
	}
	
	public List<SizeSpecification> getSizes() {
		return sizes;
	}
	
	public List<OutputFormat> getOutputFormats() {
		return outputFormats;
	}

	private List<SizeSpecification> initSizes() {
		final int max = constraints.getMaximumSize().getValue();
		final int min = constraints.getMinimumSize().getValue();
		
		final int steps = constraints.getMaximumResolutions();
		
		int stepValue = (max - min) / steps;
		if(stepValue < constraints.getMinimumResolutionSizeStep())
			stepValue = constraints.getMinimumResolutionSizeStep();
		
		final Set<Integer> sizesSet = new HashSet<>();
		
		int current = max;
		for(int i=0; i<steps; i++) {
			sizesSet.add(current);
			current = current - stepValue;
			if(current < min)
				current = min;
		}
		
		final ResponsiveDamSizeDimension dimension = constraints.getMaximumSize().getDimension();
		return sizesSet
			.stream()
			.sorted(Comparator.reverseOrder())
			.map(v -> new SizeSpecification(v, dimension))
			.collect(Collectors.toList());
	}
}
