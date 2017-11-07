package org.sevensource.magnolia.responsivedam.imaging.parameter;

import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.jackrabbit.JcrConstants;
import org.sevensource.magnolia.responsivedam.configuration.DamVariation;
import org.sevensource.magnolia.responsivedam.configuration.SizeSpecification;
import org.sevensource.magnolia.responsivedam.focusarea.FocusArea;

import info.magnolia.imaging.OutputFormat;


public class AspectAwareParameter {

	private final Node node;
	private final DamVariation damVariation;
	private final FocusArea focusArea;
	private final SizeSpecification size;
	private final OutputFormat outputFormat;


	public AspectAwareParameter(Node node, DamVariation variationSpecification, FocusArea focusArea, SizeSpecification requestedSize, OutputFormat outputFormat) {
		this.node = node;
		this.damVariation = variationSpecification;
		this.focusArea = focusArea;
		this.size = requestedSize;
		this.outputFormat = outputFormat;
	}

	public Node getNode() {
		return node;
	}

	public OutputFormat getOutputFormat() {
		return outputFormat;
	}

	public Binary getBinary() throws RepositoryException {
		return node.getProperty(JcrConstants.JCR_DATA).getBinary();
	}

	public FocusArea getFocusArea() {
		return focusArea;
	}

	public SizeSpecification getSize() {
		return size;
	}

	public DamVariation getDamVariation() {
		return damVariation;
	}

	public String getCachePath() {
		try {

			return "/" + String.join("/",
					damVariation.getVariationSet().getName(),
					damVariation.getName(),
					size.toString(),
					outputFormat.getFormatName(),
					node.getSession().getWorkspace().getName()
					) +
					node.getPath();

		} catch (RepositoryException e) {
			throw new RuntimeException("Cannot generate cache path", e);
		}
	}
}
