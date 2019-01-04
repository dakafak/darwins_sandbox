package game.world.units;

public class ScaledLocation {

	double x;
	double y;

	double cachedDrawingObjectWidthHalf;
	double cachedDrawingObjectHeightHalf;
	public int getX(double cameraX, double cameraOffsetX, double drawingObjectWidth) {
		if(cachedDrawingObjectWidthHalf == 0){
			cachedDrawingObjectWidthHalf = drawingObjectWidth / 2;
		}
		return (int) Math.ceil(x + cameraX + cameraOffsetX - cachedDrawingObjectWidthHalf);
	}

	public void setX(double x) {
		this.x = x;
	}

	public int getY(double cameraY, double cameraOffsetY, double drawingObjectHeight) {
		if(cachedDrawingObjectHeightHalf == 0){
			cachedDrawingObjectHeightHalf = drawingObjectHeight / 2;
		}
		return (int) Math.ceil(y + cameraY + cameraOffsetY - cachedDrawingObjectHeightHalf);
	}

	public void setY(double y) {
		this.y = y;
	}
}
