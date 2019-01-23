package game.world.plantlife;

import java.awt.Color;

public enum PlantType {
	GRASS(Color.GREEN);

	private Color color;

	PlantType(Color color){
		this.color = color;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}
}
