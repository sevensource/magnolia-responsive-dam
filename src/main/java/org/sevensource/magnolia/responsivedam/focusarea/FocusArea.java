package org.sevensource.magnolia.responsivedam.focusarea;

public class FocusArea {
	private Integer x;
	private Integer y;
	private Integer width;
	private Integer height;

	public FocusArea() {}
	
	public FocusArea(Integer x, Integer y, Integer width, Integer height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public static FocusArea of(FocusArea focusArea)  {
		return new FocusArea(focusArea.getX(), focusArea.getY(), focusArea.getWidth(), focusArea.getHeight());
	}
	
	public boolean isValid() {
		if(x == null || y == null || width == null || height == null) {
			return false;
		} else if(width < 1 || height < 1 || x < 0 || y < 0) {
			return false;
		} else {
			return true;
		}
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
		FocusArea other = (FocusArea) obj;
		if (height == null) {
			if (other.height != null) {
				return false;
			}
		} else if (!height.equals(other.height)) {
			return false;
		}
		if (width == null) {
			if (other.width != null) {
				return false;
			}
		} else if (!width.equals(other.width)) {
			return false;
		}
		if (x == null) {
			if (other.x != null) {
				return false;
			}
		} else if (!x.equals(other.x)) {
			return false;
		}
		if (y == null) {
			if (other.y != null) {
				return false;
			}
		} else if (!y.equals(other.y)) {
			return false;
		}
		return true;
	}

	
	public Integer getX() {
		return x;
	}
	
	public void setX(Integer x) {
		this.x = x;
	}

	public Integer getY() {
		return y;
	}

	public void setY(Integer y) {
		this.y = y;
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}
}