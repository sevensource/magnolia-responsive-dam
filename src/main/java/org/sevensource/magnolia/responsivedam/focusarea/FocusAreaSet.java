package org.sevensource.magnolia.responsivedam.focusarea;

import java.util.HashSet;
import java.util.Set;

public class FocusAreaSet {
	private String name;
	private Set<FocusArea> focusAreas = new HashSet<>();


	public FocusAreaSet() {
	}

	public FocusAreaSet(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<FocusArea> getFocusAreas() {
		return focusAreas;
	}

	public void setFocusAreas(Set<FocusArea> focusAreas) {
		this.focusAreas = focusAreas;
	}

	public void addFocusArea(FocusArea focusArea) {
		final FocusArea existing = getFocusArea(focusArea.getName());
		if(existing != null) {
			focusAreas.remove(existing);
		}

		this.focusAreas.add(focusArea);
	}

	public FocusArea getFocusArea(String name) {
		return focusAreas
			.stream()
			.filter(i -> i.getName().equals(name))
			.findFirst()
			.orElse(null);
	}

	public static FocusAreaSet of(FocusAreaSet focusAreaSet) {
		final FocusAreaSet cloned = new FocusAreaSet(focusAreaSet.getName());

		for(FocusArea area : focusAreaSet.getFocusAreas()) {
			final FocusArea clonedArea = FocusArea.of(area);
			cloned.addFocusArea(clonedArea);
		}

		return cloned;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((focusAreas == null) ? 0 : focusAreas.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		FocusAreaSet other = (FocusAreaSet) obj;
		if (focusAreas == null) {
			if (other.focusAreas != null) {
				return false;
			}
		} else if (!focusAreas.equals(other.focusAreas)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}
}
