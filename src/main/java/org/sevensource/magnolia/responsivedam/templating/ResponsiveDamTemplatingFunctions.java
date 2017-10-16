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

import info.magnolia.imaging.ImageResponse;
import info.magnolia.imaging.Imaging;
import info.magnolia.imaging.ImagingException;

@Singleton
public class ResponsiveDamTemplatingFunctions {
	
	private final ResponsiveDamConfiguration responsiveDamConfiguration;
	private final Imaging imaging;
	
	@Inject
	public ResponsiveDamTemplatingFunctions(ResponsiveDamConfiguration responsiveDamConfiguration, Imaging imaging) {
		this.responsiveDamConfiguration = responsiveDamConfiguration;
		this.imaging = imaging;
	}
	
	public String getBase64EncodedRendition(ResponsiveDamRendition rendition) throws IOException, ImagingException {
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		imaging.generate(ResponsiveDamImageGenerator.GENERATOR_NAME, rendition, new TemporaryImageResponse(os));
		
		return Base64.getEncoder().encodeToString(os.toByteArray());
	}
	
	public String buildSrcSet(List<ResponsiveDamRendition> renditions) {
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
