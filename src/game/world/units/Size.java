package game.world.units;

public class Size {

	double width;
	double height;
	double cachedStandardScale;
	ScaledSize cachedScaledSize;

	public Size(double width, double height) {
		this.width = width;
		this.height = height;
	}

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}

	/**
	 * Used to determine the on-screen size
	 *
	 * @param standardScale
	 * @return
	 */
	public ScaledSize getScaledSize(double standardScale) {
		if (cachedStandardScale != standardScale) {
			cachedStandardScale = standardScale;

			cachedScaledSize = new ScaledSize();
			cachedScaledSize.setWidth(width * standardScale);
			cachedScaledSize.setHeight(height * standardScale);
		}

		return cachedScaledSize;
	}

}
