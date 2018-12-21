package game;

public class World {

	Tile[][] tileMap;

	public World(int width, int height){
		tileMap = new Tile[width][height];//TODO may be more beneficial to create it with height first
	}

}
