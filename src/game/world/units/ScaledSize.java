package game.world.units;

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
}
