package dummy.ptixiaki;

/** A dot: the coordinates, color and size. */
public final class Dot {
	private final float x, y, z;
	private final int color;
	private final int diameter;
	/**
	* @param x horizontal coordinate. TODO Handle as Azimuth
	* @param y vertical coordinate.   TODO Handle as Pitch
	* @param z sideways coordinate.   TODO Handle as Roll
	* @param color the color.
	* @param diameter dot diameter.
	*/
	public Dot(float x, float y, float z, int color, int diameter) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.color = color;
		this.diameter = diameter;
	}
	
	/** @return the horizontal coordinate. */
	public float getX() {
		return x;
	}
	/** @return the vertical coordinate. */
	public float getY() {
		return y;
	}
	/** @return the sideways coordinate. */
	public float getZ() {
		return z;
	}
	/** @return the color. */
	public int getColor() {
		return color;
	}
	/** @return the dot diameter. */
	public int getDiameter() {
		return diameter;
	}
}

