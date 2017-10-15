package org.sevensource.magnolia.responsivedam.imaging.operation;

import java.awt.image.BufferedImage;

import org.sevensource.magnolia.responsivedam.configuration.SizeSpecification;
import org.sevensource.magnolia.responsivedam.focusarea.FocusArea;
import org.sevensource.magnolia.responsivedam.imaging.parameter.AspectAwareParameter;

import info.magnolia.imaging.ImagingException;
import info.magnolia.imaging.ParameterProvider;
import info.magnolia.imaging.operations.cropresize.AbstractCropAndResize;
import info.magnolia.imaging.operations.cropresize.Coords;
import info.magnolia.imaging.operations.cropresize.Size;
import info.magnolia.imaging.operations.cropresize.resizers.MultiStepResizer;

public class AspectAwareCrop extends AbstractCropAndResize<ParameterProvider<AspectAwareParameter>>  {

	public AspectAwareCrop() {
		final MultiStepResizer resizer = new MultiStepResizer();
		resizer.setInterpolation("bilinear");
		setResizer(resizer);
	}
	
	@Override
	protected Coords getCroopCoords(BufferedImage source, ParameterProvider<AspectAwareParameter> params) throws ImagingException {
		final FocusArea focusArea = params.getParameter().getFocusArea();

		return new Coords(
				focusArea.getX(),
				focusArea.getY(),
				focusArea.getX() + focusArea.getWidth(),
				focusArea.getY() + focusArea.getHeight());
	}

	@Override
	protected Size getEffectiveTargetSize(BufferedImage source, Coords cropCoords,
			ParameterProvider<AspectAwareParameter> params) {
		
		final FocusArea focusArea = params.getParameter().getFocusArea();
		final SizeSpecification spec = params.getParameter().getRequestedSize();
		
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
