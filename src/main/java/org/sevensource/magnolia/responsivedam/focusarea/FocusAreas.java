package org.sevensource.magnolia.responsivedam.focusarea;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.sevensource.magnolia.responsivedam.configuration.DamVariation;
import org.sevensource.magnolia.responsivedam.configuration.DamVariationSet;

public class FocusAreas {

	private List<FocusAreaSet> focusAreaSets = new ArrayList<>();

	public List<FocusAreaSet> getFocusAreaSets() {
		return focusAreaSets;
	}

	public void addFocusAreaSet(FocusAreaSet focusAreaSet) {
		this.focusAreaSets.add(focusAreaSet);
	}

	public FocusAreaSet getFocusAreaSet(String name) {
		for(FocusAreaSet focusAreaSet : focusAreaSets) {
			if(name.equals(focusAreaSet.getName())) {
				return focusAreaSet;
			}
		}

		return null;
	}

	public FocusAreaSet getOrCreateFocusAreaSet(DamVariationSet damVariationSet) {
		FocusAreaSet focusAreaSet = getFocusAreaSet(damVariationSet.getName());
		if(focusAreaSet == null) {
			focusAreaSet = new FocusAreaSet();
			focusAreaSet.setName(damVariationSet.getName());
			addFocusAreaSet(focusAreaSet);
		}
		return focusAreaSet;
	}

	public FocusArea getFocusArea(DamVariation variation) {
		if(variation == null || StringUtils.isEmpty(variation.getName())) {
			throw new IllegalArgumentException("DamVariation has no VariationSet");
		}

		final DamVariationSet variationSet = variation.getVariationSet();
		if(variationSet == null || StringUtils.isEmpty(variationSet.getName())) {
			throw new IllegalArgumentException("DamVariation has no VariationSet");
		}

		final FocusAreaSet focusAreaSet = getFocusAreaSet(variationSet.getName());
		if(focusAreaSet != null) {
			return focusAreaSet.getFocusArea(variation.getName());
		}

		return null;
	}

	public static FocusAreas of(FocusAreas focusAreas) {
		FocusAreas cloned = new FocusAreas();

		for(FocusAreaSet focusAreaSet : focusAreas.getFocusAreaSets()) {
			final FocusAreaSet clonedFocusAreaSet = FocusAreaSet.of(focusAreaSet);
			cloned.addFocusAreaSet(clonedFocusAreaSet);
		}

		return cloned;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((focusAreaSets == null) ? 0 : focusAreaSets.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		FocusAreas other = (FocusAreas) obj;
		if (focusAreaSets == null) {
			if (other.focusAreaSets != null) {
				return false;
			}
		} else if (!focusAreaSets.equals(other.focusAreaSets)) {
			return false;
		}
		return true;
	}
}
