package org.sevensource.magnolia.responsivedam.imaging.operation;

import java.awt.image.BufferedImage;

import org.sevensource.magnolia.responsivedam.focusarea.FocusArea;
import org.sevensource.magnolia.responsivedam.imaging.parameter.AspectAwareParameter;

import info.magnolia.imaging.ImagingException;
import info.magnolia.imaging.ParameterProvider;
import info.magnolia.imaging.operations.ImageOperation;

public class AspectAwareCrop implements ImageOperation<ParameterProvider<AspectAwareParameter>>  {

	@Override
	public BufferedImage apply(BufferedImage source, ParameterProvider<AspectAwareParameter> params)
			throws ImagingException {
		final FocusArea focusArea = params.getParameter().getFocusArea();
		return source.getSubimage(focusArea.getX(), focusArea.getY(), focusArea.getWidth(), focusArea.getHeight());
	}
}