package org.sevensource.magnolia.responsivedam.focusarea;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class FocusAreas {
	
	private Map<String, FocusArea> areas = new HashMap<>();
	
	
	public Map<String, FocusArea> getAreas() {
		return areas;
	}
	
	public void setAreas(Map<String, FocusArea> areas) {
		this.areas = areas;
	}
	
	public void addArea(String key, FocusArea area) {
		areas.put(key, area);
	}
	
	public static FocusAreas of(FocusAreas focusAreas) {
		FocusAreas cloned = new FocusAreas();
		for(Entry<String, FocusArea> entry : focusAreas.getAreas().entrySet()) {
			final String clonedId = entry.getKey(); // String is immutable
			final FocusArea clonedArea = FocusArea.of(entry.getValue());
			cloned.addArea(clonedId, clonedArea);
		}
		return cloned;
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
		if (areas == null) {
			if (other.areas != null) {
				return false;
			}
		} else if (!areas.equals(other.areas)) {
			return false;
		}
		return true;
	}
}
