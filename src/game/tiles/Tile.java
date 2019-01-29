package game.tiles;

import game.world.plantlife.Plant;
import game.world.units.Location;
import game.world.units.Size;

import java.util.ArrayList;
import java.util.List;

/**
 * A singular square area on the map
 */
public class Tile {

	private TileType tileType;
	Location location;
	float tileFertility;
	Size size;
	List<Plant> plants;
	double growthFrequency = .1;

	public Tile(int x, int y, TileType tileType, double currentDay){
		plants = new ArrayList<>();
		size = new Size(1, 1);
		location = new Location(x, y);
		this.tileType = tileType;
		tileFertility = tileType.getFertility();
		lastGrowthCheck = Math.random();
	}

	double lastGrowthCheck;
	public boolean canGrowPlants(double worldTime){
		if(worldTime - lastGrowthCheck >= growthFrequency){
			lastGrowthCheck = worldTime;
			return true;
		}

		return false;
	}

	public TileType getType() {
		return tileType;
	}

	public void setType(TileType type) {
		this.tileType = type;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public float getTileFertility() {
		return tileFertility;
	}

	public void setTileFertility(float tileFertility) {
		this.tileFertility = tileFertility;
	}

	public Size getSize() {
		return size;
	}

	public void setSize(Size size) {
		this.size = size;
	}

	public TileType getTileType() {
		return tileType;
	}

	public void setTileType(TileType tileType) {
		this.tileType = tileType;
	}

	public List<Plant> getPlants() {
		return plants;
	}

	public void setPlants(List<Plant> plants) {
		this.plants = plants;
	}

	public void addPlant(Plant plant){
		plants.add(plant);
	}

	float tileFertilityModifier = .01f;
	public void removeFertility() {
		tileFertility -= tileFertilityModifier;
		if(tileFertility < 0f){
			tileFertility = 0f;
		}
	}

	public void addFertility() {
		tileFertility += tileFertilityModifier;
		if(tileFertility > 1f){
			tileFertility = 1f;
		}
	}
}
