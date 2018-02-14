package org.sevensource.magnolia.responsivedam.imaging.operation;
class XYPair {
	private final int x;
	private final int y;

	public XYPair(int x, int y) {
		this.x = x;
		this.y = y;
	}

	int x() { return x; }
	int y() { return y; }
}