package game.dna.stats;

public enum StatType {
	color("color"),
	size("size"),
	speed("speed");

	String value;
	StatType(String value){
		this.value = value;
	}
}
