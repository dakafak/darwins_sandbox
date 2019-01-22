package game.tiles;

import game.world.units.Location;

public class Tile {

	private TileType type;
	Location location;
	int tileFertility;

	public Tile(int x, int y, TileType tileType){
		location = new Location(x, y);
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
		return type;
	}

	public void setType(TileType type) {
		this.type = type;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}
}
