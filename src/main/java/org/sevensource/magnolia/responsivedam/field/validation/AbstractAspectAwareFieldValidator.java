package org.sevensource.magnolia.responsivedam.field.validation;

import java.awt.Dimension;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.sevensource.magnolia.responsivedam.configuration.DamVariation;
import org.sevensource.magnolia.responsivedam.configuration.DamVariationSet;
import org.sevensource.magnolia.responsivedam.configuration.ResponsiveDamConfiguration;
import org.sevensource.magnolia.responsivedam.configuration.SizeSpecification.SizeDimension;
import org.sevensource.magnolia.responsivedam.focusarea.FocusAreas;

import com.vaadin.v7.data.validator.AbstractValidator;



public abstract class AbstractAspectAwareFieldValidator<T> extends AbstractValidator<T>{

	/**
	 *
	 */
	private static final long serialVersionUID = -7811330198930824469L;
	private transient List<DamVariationSet> damVariationSets;

	public AbstractAspectAwareFieldValidator(String errorMessage) {
		super(errorMessage);
	}

	protected void setDamVariationSets(List<DamVariationSet> damVariationSets) {
		this.damVariationSets = damVariationSets;
	}

	protected static List<DamVariationSet> transformDamVariationSets(ResponsiveDamConfiguration responsiveDamConfiguration, List<String> variationSets) {
		if(variationSets == null || variationSets.isEmpty()) {
			return Collections.emptyList();
		}

		final List<DamVariationSet> damVariationSets = variationSets
				.stream()
				.map(name -> {
					final DamVariationSet variationSet = responsiveDamConfiguration.getVariationSet(name);
					if(variationSet == null) {
						throw new IllegalArgumentException("Unknown variationSet " + name);
					}
					return variationSet;
				})
				.collect(Collectors.toList());
		return damVariationSets;
	}

	protected boolean isValidFocusAreas(FocusAreas focusAreas) {
		return getMissingVariations(damVariationSets, focusAreas).isEmpty();
	}

	public static List<DamVariation> getMissingVariations(List<DamVariationSet> damVariationSets, FocusAreas focusAreas) {
		if(damVariationSets == null || damVariationSets.isEmpty()) {
			return Collections.emptyList();
		}

		final List<DamVariation> missing = new ArrayList<>();

		for(DamVariationSet damVariationSet : damVariationSets) {

			for(DamVariation damVariation : damVariationSet.getVariations()) {
				if(focusAreas == null || focusAreas.getFocusArea(damVariation) == null) {
					missing.add(damVariation);
				}
			}
		}

		return missing;
	}

	protected boolean hasRequiredSize(InputStream is) {
		final Dimension imageDimensions = getImageDimensions(is);
		final Dimension requiredDimension = getRequiredImageDimensions(damVariationSets);


		return requiredDimension.getHeight() <= imageDimensions.getHeight() &&
				requiredDimension.getWidth() <= imageDimensions.getWidth();
	}

	public static Dimension getRequiredImageDimensions(List<DamVariationSet> damVariationSets) {
		if(damVariationSets == null || damVariationSets.isEmpty()) {
			return new Dimension(0, 0);
		}


		int requiredHeight = 0;
		int requiredWidth = 0;


		for(DamVariationSet set : damVariationSets) {
			for(DamVariation variation : set.getVariations()) {

				final int val = variation.getConstraints().getMaximumSize().getValue();

				if (variation.getConstraints().getMaximumSize().getDimension() == SizeDimension.HEIGHT &&
					val > requiredHeight) {
						requiredHeight = val;
				} else if(variation.getConstraints().getMaximumSize().getDimension() == SizeDimension.WIDTH &&
					val > requiredWidth) {
					requiredWidth = val;
				}
			}
		}

		return new Dimension(requiredWidth, requiredHeight);
	}

	private Dimension getImageDimensions(InputStream is) {
		try(ImageInputStream in = ImageIO.createImageInputStream(is)){
		    final Iterator<ImageReader> readers = ImageIO.getImageReaders(in);
		    if (readers.hasNext()) {
		        ImageReader reader = readers.next();
		        try {
		            reader.setInput(in);
		            return new Dimension(reader.getWidth(0), reader.getHeight(0));
		        } finally {
		            reader.dispose();
		        }
		    }
		} catch(IOException ioe) {
			// do nothing
		}

		return new Dimension();
	}
}
