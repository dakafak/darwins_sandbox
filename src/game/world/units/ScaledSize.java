package game.world.units;

/**
 * The on-screen size
 */
public class ScaledSize {

	double width;
	double height;

	public int getWidth() {
		return (int) Math.ceil(width);
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public int getHeight() {
		return (int) Math.ceil(height);
	}

	public void setHeight(double height) {
		this.height = height;
	}

	double cachedDrawingObjectWidthHalf;
	double cachedDrawingObjectHeightHalf;

	public int getMiddleWidth() {
		if (cachedDrawingObjectWidthHalf == 0) {
			cachedDrawingObjectWidthHalf = width / 2;
		}
		return (int) Math.ceil(cachedDrawingObjectWidthHalf);
	}

	public int getMiddleHeight() {
		if (cachedDrawingObjectHeightHalf == 0) {
			cachedDrawingObjectHeightHalf = height / 2;
		}
		return (int) Math.ceil(cachedDrawingObjectHeightHalf);
	}
}
