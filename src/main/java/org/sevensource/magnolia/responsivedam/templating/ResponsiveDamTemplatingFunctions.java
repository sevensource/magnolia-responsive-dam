package org.sevensource.magnolia.responsivedam.templating;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.jcr.Node;

import org.sevensource.magnolia.responsivedam.configuration.DamVariation;
import org.sevensource.magnolia.responsivedam.configuration.ResponsiveDamConfiguration;
import org.sevensource.magnolia.responsivedam.imaging.ResponsiveDamImageGenerator;
import org.sevensource.magnolia.responsivedam.imaging.ResponsiveDamRendition;
import org.sevensource.magnolia.responsivedam.imaging.ResponsiveDamVariation;

import com.google.common.net.MediaType;

import info.magnolia.cms.beans.config.MIMEMapping;
import info.magnolia.cms.beans.config.ServerConfiguration;
import info.magnolia.dam.jcr.JcrAsset;
import info.magnolia.imaging.ImageResponse;
import info.magnolia.imaging.Imaging;
import info.magnolia.imaging.ImagingException;
import info.magnolia.imaging.OutputFormat;

@Singleton
public class ResponsiveDamTemplatingFunctions {


	private final ResponsiveDamConfiguration responsiveDamConfiguration;
	private final Imaging imaging;
	private final ServerConfiguration serverConfiguration;


	@Inject
	public ResponsiveDamTemplatingFunctions(ResponsiveDamConfiguration responsiveDamConfiguration, Imaging imaging, ServerConfiguration serverConfiguration) {
		this.responsiveDamConfiguration = responsiveDamConfiguration;
		this.imaging = imaging;
		this.serverConfiguration = serverConfiguration;
	}

	public String getBase64EncodedRendition(ResponsiveDamRendition rendition) throws IOException, ImagingException {
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		imaging.generate(ResponsiveDamImageGenerator.GENERATOR_NAME, rendition, new TemporaryImageResponse(os));

		return Base64.getEncoder().encodeToString(os.toByteArray());
	}

	public String getDataUriEncodedRendition(ResponsiveDamRendition rendition) throws IOException, ImagingException {
		return "data:" +
				getMimeTypeByOutputFormat(rendition.getOutputFormat()) +
				";base64," +
				getBase64EncodedRendition(rendition);
	}

	public String generateSrcSet(List<ResponsiveDamRendition> renditions) {
		StringBuilder builder = new StringBuilder();

		final Iterator<ResponsiveDamRendition> it = renditions.iterator();
		while(it.hasNext()) {
			final ResponsiveDamRendition rendition = it.next();
			builder.append(rendition.getLink());
			builder.append(" ");
			builder.append(rendition.getSize().toString());
			if(it.hasNext()) {
				builder.append(", ");
			}
		}

		return builder.toString();
	}

	public ResponsiveDamVariation getResponsiveVariation(Node node, String variationSetName, String variationName) {
		final DamVariation variation = responsiveDamConfiguration.getVariation(variationSetName, variationName);
		if(variation == null) {
			final String msg = String.format("No damVariation with name [%s] found for variationSet [%s]", variationName, variationSetName);
			throw new IllegalArgumentException(msg);
		}

		return new ResponsiveDamVariation(node, variation, responsiveDamConfiguration);
	}

	public ResponsiveDamVariation getResponsiveVariation(JcrAsset asset, String variationSetName, String variationName) {
		if(asset == null) {
			throw new IllegalArgumentException("Asset is null");
		}
		return getResponsiveVariation(asset.getNode(), variationSetName, variationName);
	}

	public String getExternalLink(ResponsiveDamRendition rendition) {
		final String linkStr = rendition.getLink();
		final String base = serverConfiguration.getDefaultBaseUrl();

		if (base.endsWith("/") && linkStr.startsWith("/")) {
			return base + linkStr.substring(1);
		} else {
			return base + linkStr;
		}
	}

	public String getMimeTypeByOutputFormat(OutputFormat outputFormat) {
		return MIMEMapping.getMIMEType(outputFormat.getFormatName());
	}

	private static class TemporaryImageResponse implements ImageResponse {
		private final OutputStream tempOut;

		public TemporaryImageResponse(OutputStream tempOut) {
			this.tempOut = tempOut;
		}

		@Override
		public void setMediaType(MediaType mediaType) throws IOException {
			// Do nothing - in the case of CachingImageStreamer, we rely on the "real" ImageResponse to set the mime-type on the response
		}

		@Override
		public OutputStream getOutputStream() throws IOException {
			return tempOut;
		}
	}
}
