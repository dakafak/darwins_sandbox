package game.tiles;

import game.world.units.Location;
import game.world.units.Size;

public class Tile {

	private TileType tileType;
	Location location;
	int tileFertility;
	Size size;

	public Tile(int x, int y, TileType tileType){
		size = new Size(1, 1);
		location = new Location(x, y);
		this.tileType = tileType;
		if(tileType.equals(TileType.DIRT)){
			tileFertility = 10;
		} else if(tileType.equals(TileType.ROCK)){
			tileFertility = 1;
		} else if(tileType.equals(TileType.SAND)){
			tileFertility = 1;
		} else if(tileType.equals(TileType.CLAY)){
			tileFertility = 1;
		} else if(tileType.equals(TileType.WATER)){
			tileFertility = 1;
		} else {
			tileFertility = 0;
		}
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

	public int getTileFertility() {
		return tileFertility;
	}

	public void setTileFertility(int tileFertility) {
		this.tileFertility = tileFertility;
	}

	public Size getSize() {
		return size;
	}

	public void setSize(Size size) {
		this.size = size;
	}
}
