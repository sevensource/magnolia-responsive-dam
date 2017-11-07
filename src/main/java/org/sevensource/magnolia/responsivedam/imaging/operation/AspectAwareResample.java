package org.sevensource.magnolia.responsivedam.imaging.operation;

import java.awt.image.BufferedImage;

import org.sevensource.magnolia.responsivedam.configuration.SizeSpecification;
import org.sevensource.magnolia.responsivedam.focusarea.FocusArea;
import org.sevensource.magnolia.responsivedam.imaging.parameter.AspectAwareParameter;

import com.twelvemonkeys.image.ResampleOp;

import info.magnolia.imaging.ImagingException;
import info.magnolia.imaging.ParameterProvider;
import info.magnolia.imaging.operations.ImageOperation;
import info.magnolia.imaging.operations.cropresize.Size;

public class AspectAwareResample implements ImageOperation<ParameterProvider<AspectAwareParameter>>  {

	private static final int FILTER_TYPE = ResampleOp.FILTER_LANCZOS;

	@Override
	public BufferedImage apply(BufferedImage source, ParameterProvider<AspectAwareParameter> params)
			throws ImagingException {

		final Size size = getTargetSize(params);
		final ResampleOp resampler = new ResampleOp(size.getWidth(), size.getHeight(), FILTER_TYPE);
		return resampler.filter(source, null);
	}

	private Size getTargetSize(ParameterProvider<AspectAwareParameter> params) {

		final FocusArea focusArea = params.getParameter().getFocusArea();
		final SizeSpecification spec = params.getParameter().getSize();

		int width = 0;
		int height = 0;

		switch(spec.getDimension()) {
		case WIDTH:
			width = spec.getValue();
			height = (int) Math.round( (double) focusArea.getHeight() / ((double) focusArea.getWidth() / (double) width) );
			break;
		case HEIGHT:
			height = spec.getValue();
			width = (int) Math.round( (double) focusArea.getWidth() / ((double) focusArea.getHeight() / (double) height) );
			break;
		default:
			throw new IllegalArgumentException("Unknown dimension");
		}

		return new Size(width, height);
	}
}
