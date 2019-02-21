package game.world.plantlife;

import java.awt.Color;

/**
 * Used to define the color and energy restoration for a given plant, for each type
 */
public enum PlantType {
	GRASS(Color.GREEN, 5);

	private Color color;
	private double energyRestoration;

	PlantType(Color color, double energyRestoration) {
		this.color = color;
		this.energyRestoration = energyRestoration;
	}

	public Color getColor() {
		return color;
	}

	public double getEnergyRestoration() {
		return energyRestoration;
	}

}
