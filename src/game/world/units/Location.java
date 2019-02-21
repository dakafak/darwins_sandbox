package game.world.units;

/**
 * World location
 */
public class Location {

	double x;
	double y;
	double cachedStandardScale;
	ScaledLocation cachedScaledLocation;

	public Location(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	/**
	 * Returns a scaled location, which is used for determining the on-screen location
	 *
	 * @param standardScale
	 * @return
	 */
	public ScaledLocation getScaledLocation(double standardScale) {
		if (cachedStandardScale != standardScale) {
			cachedStandardScale = standardScale;

			cachedScaledLocation = new ScaledLocation();
		}

		cachedScaledLocation.setX(x * cachedStandardScale);
		cachedScaledLocation.setY(y * cachedStandardScale);

		return cachedScaledLocation;
	}

}
