package org.sevensource.magnolia.responsivedam.imaging;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.jcr.Node;

import org.sevensource.magnolia.responsivedam.ResponsiveDamNodeUtil;
import org.sevensource.magnolia.responsivedam.configuration.DamSizeConstraints;
import org.sevensource.magnolia.responsivedam.configuration.DamVariation;
import org.sevensource.magnolia.responsivedam.configuration.ResponsiveDamConfiguration;
import org.sevensource.magnolia.responsivedam.configuration.SizeSpecification;
import org.sevensource.magnolia.responsivedam.configuration.SizeSpecification.SizeDimension;

import info.magnolia.dam.jcr.AssetNodeTypes;
import info.magnolia.imaging.OutputFormat;
import info.magnolia.jcr.util.PropertyUtil;

public class ResponsiveDamVariation {

	private final Long width;
	private final Long height;
	private final String mimeType;
	private final DamSizeConstraints constraints;

	private final List<SizeSpecification> sizes;
	private final List<OutputFormat> outputFormats;
	private final List<ResponsiveDamRendition> renditions;

	/**
	 *
	 * @param node
	 * @param damVariation
	 * @param responsiveDamConfiguration
	 *
	 * @throws RuntimeException if the node contains no responsive image
	 */
	public ResponsiveDamVariation(Node node, DamVariation damVariation, ResponsiveDamConfiguration responsiveDamConfiguration) {

		final Node contentNode = ResponsiveDamNodeUtil.getContentNode(node);

		if(contentNode == null) {
			throw new IllegalArgumentException("Cannot get contentNode");
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

	public List<ResponsiveDamRendition> getRenditionsByOutputFormat(OutputFormat outputFormat) {
		return getRenditionsByOutputFormat(outputFormat.getFormatName());
	}

	public List<ResponsiveDamRendition> getRenditionsByOutputFormat(String outputFormatName) {
		return renditions
				.stream()
				.filter(r -> r.getOutputFormat().getFormatName().equalsIgnoreCase(outputFormatName))
				.collect(Collectors.toList());
	}

	public ResponsiveDamRendition getDefaultRendition() {
		final OutputFormat primaryOutputFormat = getPrimaryOutputFormat();
		return getDefaultRendition(primaryOutputFormat);
	}

	public ResponsiveDamRendition getDefaultRendition(OutputFormat outputFormat) {
		for(ResponsiveDamRendition rendition : renditions) {
			if(rendition.getOutputFormat().getFormatName().equalsIgnoreCase(outputFormat.getFormatName())) {
				return rendition;
			}
		}
		return null;
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

	public OutputFormat getPrimaryOutputFormat() {
		return outputFormats
			.stream()
			.filter(f -> ! "webp".equalsIgnoreCase(f.getFormatName()))
			.findFirst()
			.orElse(null);
	}

	private List<SizeSpecification> initSizes() {
		final int max = constraints.getMaximumSize().getValue();
		final int min = constraints.getMinimumSize().getValue();

		final int steps = constraints.getMaximumResolutions();

		final Set<Integer> sizesSet = new HashSet<>();
		sizesSet.add(max);
		sizesSet.add(min);

		if(steps > 2) {
			int stepValue = (int) Math.round( (double) (max - min) / (double) (steps - 1));
			if(stepValue < constraints.getMinimumResolutionSizeStep()) {
				stepValue = constraints.getMinimumResolutionSizeStep();
			}

			int currentSize = max - stepValue;
			int i = 1;

			while(currentSize > min && i++ < steps) {
				sizesSet.add(currentSize);
				currentSize -= stepValue;
			}
		}


		final SizeDimension dimension = constraints.getMaximumSize().getDimension();
		return sizesSet
				.stream()
				.sorted(Comparator.reverseOrder())
				.map(v -> new SizeSpecification(v, dimension))
				.collect(Collectors.toList());
	}
}
