package org.sevensource.magnolia.responsivedam.configuration;

import java.util.ArrayList;
import java.util.List;

public class DamVariationSet {
	private final String name;
	private List<DamVariationSpecification> specifications;
	
	public DamVariationSet(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public List<DamVariationSpecification> getSpecifications() {
		return specifications;
	}
	
	public void setSpecifications(List<DamVariationSpecification> specifications) {
		this.specifications = specifications;
	}
	
	public void addSpecification(DamVariationSpecification specification) {
		if(this.specifications == null) {
			this.specifications = new ArrayList<>();
		}
		this.specifications.add(specification);
	}
}
