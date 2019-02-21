package game.dna.traits;

/**
 * Defines the type of a trait
 */
public enum TraitType {
	color("color", new String[]{"t=tan", "b=brown", "K=black", "G=green", "B=blue"}),
	size("size", new String[]{"s=small", "m=medium", "L=large", "h=huge"}),
	aggression("aggression", new String[]{"p=passive", "N=neutral", "a=aggressive"}),
	mating_frequency("mating_frequency", new String[]{"l=low", "M=medium", "h=high"}),
	diet("diet", new String[]{"c=CARNIVORE", "h=HERBIVORE"}),
	social("social", new String[]{"i=independent", "S=social", "d=dependent"}),
	speed("speed", new String[]{"s=slow", "m=medium", "f=fast"}),
	lifespan("lifespan", new String[]{"s=short", "m=medium", "l=long"}),
	energy("energy", new String[]{"l=low", "m=medium", "h=high"}),
	hunger_threshold("hunger_threshold", new String[]{"l=low", "m=medium", "h=high"});

	private String value;
	private String[] strings;

	TraitType(String value, String[] strings) {
		this.value = value;
		this.strings = strings;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String[] getStrings() {
		return strings;
	}

	public void setStrings(String[] strings) {
		this.strings = strings;
	}
}
