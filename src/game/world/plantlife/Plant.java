package game.world.plantlife;

import game.tiles.Tile;
import game.world.units.Location;
import game.world.units.Size;

public class Plant {

	PlantType plantType;
	Location location;
	Size size;
	Tile tileForPlant;

	public Plant(double x, double y, PlantType plantType, Tile tile) {
		location = new Location(x, y);
		size = new Size(.25, .25);
		this.plantType = plantType;
		tileForPlant = tile;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public Size getSize() {
		return size;
	}

	public void setSize(Size size) {
		this.size = size;
	}

	public PlantType getPlantType() {
		return plantType;
	}

	public void setPlantType(PlantType plantType) {
		this.plantType = plantType;
	}

	public Tile getTileForPlant() {
		return tileForPlant;
	}

	public void setTileForPlant(Tile tileForPlant) {
		this.tileForPlant = tileForPlant;
	}
}
