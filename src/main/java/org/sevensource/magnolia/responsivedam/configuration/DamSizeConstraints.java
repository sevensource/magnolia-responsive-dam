package org.sevensource.magnolia.responsivedam.configuration;

public class DamSizeConstraints {

	private Integer maximumResolutions = null;
	private Integer minimumResolutionSizeStep = null;
	private SizeSpecification minimumSize = null;
	private SizeSpecification maximumSize = null;
	
	
	public void setMinimumSize(SizeSpecification minimumSize) {
		this.minimumSize = minimumSize;
	}

	public SizeSpecification getMaximumSize() {
		return maximumSize;
	}
	
	public void setMaximumSize(SizeSpecification maximumSize) {
		this.maximumSize = maximumSize;
	}
	
	public SizeSpecification getMinimumSize() {
		return minimumSize;
	}

	public Integer getMaximumResolutions() {
		return maximumResolutions;
	}

	public void setMaximumResolutions(Integer maximumResolutions) {
		this.maximumResolutions = maximumResolutions;
	}

	public Integer getMinimumResolutionSizeStep() {
		return minimumResolutionSizeStep;
	}

	public void setMinimumResolutionSizeStep(Integer minimumResolutionSizeStep) {
		this.minimumResolutionSizeStep = minimumResolutionSizeStep;
	}
}