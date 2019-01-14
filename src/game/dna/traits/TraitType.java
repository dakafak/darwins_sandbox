package game.dna.traits;

public enum TraitType {
	color("color"),
	size("size"),
	aggression("aggression"),
	mating_frequency("mating_frequency"),
	diet("diet"),
	social("social"),
	speed("speed"),
	lifespan("lifespan");

	private String value;
	TraitType(String value){
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
