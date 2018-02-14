package org.sevensource.magnolia.responsivedam.imaging;

import java.net.URI;
import java.net.URISyntaxException;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.JcrConstants;
import org.sevensource.magnolia.responsivedam.configuration.SizeSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.magnolia.cms.beans.runtime.FileProperties;
import info.magnolia.context.MgnlContext;
import info.magnolia.dam.jcr.AssetNodeTypes;
import info.magnolia.imaging.OutputFormat;
import info.magnolia.jcr.util.PropertyUtil;

public class ResponsiveDamRendition {

	private static final Logger logger = LoggerFactory.getLogger(ResponsiveDamRendition.class);

	private static final String DEFAULT_FILENAME = "image";


	private final Node node;
	private final String variationSet;
	private final String variation;
	private final SizeSpecification size;
	private final OutputFormat outputFormat;

	public ResponsiveDamRendition(Node node, String variationSet, String variation, SizeSpecification size, OutputFormat outputFormat) {
		this.node = node;
		this.variationSet = variationSet;
		this.variation = variation;
		this.size = size;
		this.outputFormat = outputFormat;
	}

	public Node getNode() {
		return node;
	}

	public String getVariationSet() {
		return variationSet;
	}

	public String getVariation() {
		return variation;
	}

	public SizeSpecification getSize() {
		return size;
	}

	public OutputFormat getOutputFormat() {
		return outputFormat;
	}

	public String getLink() {
		String fileName = null;

		try {
			final String workspaceName = node.getSession().getWorkspace().getName();
			final String nodeName = node.getName();

			//remove jcr:content from link
			final String path = (nodeName.equals(JcrConstants.JCR_CONTENT)) ? node.getParent().getPath() : node.getPath();

			if(nodeName.equals(JcrConstants.JCR_CONTENT)) {
				fileName = PropertyUtil.getString(node.getParent(), AssetNodeTypes.AssetResource.FILENAME);
			}

			if(StringUtils.isEmpty(fileName)) {
				fileName = PropertyUtil.getString(node, FileProperties.PROPERTY_FILENAME);
			}

			if(StringUtils.isEmpty(fileName)) {
				fileName = DEFAULT_FILENAME;
			}


			fileName = FilenameUtils.removeExtension(fileName);
			fileName += "." + outputFormat.getFormatName();
			fileName = new URI(null, null, fileName, null).toASCIIString();


			return String.join("/",
					MgnlContext.getContextPath(),
					".imaging",
					ResponsiveDamImageGenerator.GENERATOR_NAME,
					variationSet,
					variation,
					size.toString(),
					workspaceName + path,
					fileName);

		} catch (RepositoryException e) {
			logger.warn("Could not create link for property [{}].", node, e);
		} catch (URISyntaxException e) {
			logger.warn("Could not create link for image due to illegal property file name [{}].", fileName, e);
		}

		return null;
	}
}
