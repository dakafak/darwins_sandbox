package game.tiles;

import java.awt.Color;

/**
 * Used to define what type of ground a tile is. Also defines the fertility and color of the tile using this type
 */
public enum TileType {
	DIRT(.01f, Color.decode("#312516")),
	ROCK(.001f, Color.decode("#888C8D")),
	SAND(.001f, Color.decode("#C2B280")),
	CLAY(.001f, Color.decode("#734222")),
	WATER(.001f, Color.decode("#2E6772"));

	private float fertility;
	private Color color;
	TileType(float fertility, Color color){
		this.fertility = fertility;
		this.color = color;
	}

	public float getFertility() {
		return fertility;
	}

	public void setFertility(float fertility) {
		this.fertility = fertility;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}
}
