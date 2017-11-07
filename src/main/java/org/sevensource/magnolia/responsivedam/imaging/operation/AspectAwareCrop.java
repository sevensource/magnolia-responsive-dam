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

		final XYPair cropLocation;
		final XYPair cropDimension;

		if(focusArea == null) {
			// use the center of the image
			final XYPair currentDimension = new XYPair(source.getWidth(), source.getHeight());
			cropDimension = getCropDimensions(currentDimension, params.getParameter().getDamVariation().getRatio());
			cropLocation = getCropLocation(currentDimension, cropDimension);
		} else {
			cropLocation = new XYPair(focusArea.getX(), focusArea.getY());
			cropDimension = new XYPair(focusArea.getWidth(), focusArea.getHeight());
		}
		return source.getSubimage(cropLocation.x(), cropLocation.y(), cropDimension.x(), cropDimension.y());
	}

	private class XYPair {
		private final int x;
		private final int y;

		public XYPair(int x, int y) {
			this.x = x;
			this.y = y;
		}

		int x() { return x; }
		int y() { return y; }
	}


	private XYPair getCropLocation(XYPair currentDimension, XYPair cropDimension) {

		// calculate the relative focalpoint coordinates
		int focalX = (int) (currentDimension.x() * 0.5);
		int focalY = (int) (currentDimension.y() * 0.5);

		// set the x,y value of the top-left crop corner so that the focal point
		// is at its center
		int cropX = focalX - (cropDimension.x() / 2);
		int cropY = focalY - (cropDimension.y() / 2);

		// if any of the values exceed the dimensions of the image, fix it
		if (cropX < 0) {
			cropX = 0;
		}
		if (cropY < 0) {
			cropY = 0;
		}

		if (cropX + cropDimension.x() > currentDimension.x()) {
			cropX = currentDimension.x() - cropDimension.x();
		}
		if (cropY + cropDimension.y() > currentDimension.y()) {
			cropY = currentDimension.y() - cropDimension.y();
		}

		return new XYPair(cropX, cropY);
	}

	/**
	 * calculates the largest possible width & height
	 *
	 * @param currentDimensions
	 *            the px dimensions of the image to be cropped
	 * @param aspectRatio
	 *            the aspect ratio
	 * @return
	 */
	private XYPair getCropDimensions(XYPair currentDimensions, double aspectRatio) {

		int targetWidth = 0;
		int targetHeight = 0;

		if (aspectRatio == 1) {
			// square aspect ratio
			targetWidth = currentDimensions.x() < currentDimensions.y() ? currentDimensions.x() : currentDimensions.y();
			targetHeight = targetWidth;
		} else if (aspectRatio > 1) {
			/**
			 * landscape
			 *
			 * start with the current width, calculate the target-height and
			 * reduce until the target-height fits in the current height
			 */
			int deduct = 0;
			do {
				targetWidth = currentDimensions.x() - deduct++;
				targetHeight = (int) Math.round(targetWidth / aspectRatio);
			} while (targetHeight > currentDimensions.y());
		} else {
			/**
			 * portrait
			 *
			 * start with the current height, calculate the target width and
			 * reduce until the target width fits in the current width
			 */
			int deduct = 0;
			do {
				targetHeight = currentDimensions.y() - deduct++;
				targetWidth = (int) Math.round(targetHeight * aspectRatio);
			} while (targetWidth > currentDimensions.x());
		}

		return new XYPair(targetWidth, targetHeight);
	}
}