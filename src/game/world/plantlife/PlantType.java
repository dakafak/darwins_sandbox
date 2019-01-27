package game.world.plantlife;

import java.awt.Color;

public enum PlantType {
	GRASS(Color.GREEN, 50);

	private Color color;
	private double energyRestoration;

	PlantType(Color color, double energyRestoration){
		this.color = color;
		this.energyRestoration = energyRestoration;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public double getEnergyRestoration() {
		return energyRestoration;
	}

	public void setEnergyRestoration(double energyRestoration) {
		this.energyRestoration = energyRestoration;
	}
}
