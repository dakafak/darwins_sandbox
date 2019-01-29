package game.dna.stats;

import java.awt.Color;

public enum StatType {
	color("color", Color.class),
	size("size", Double.class),
	speed("speed", Double.class),
	mating_frequency("mating_frequency", Double.class),
	life_span("life_span", Double.class),
	diet("diet", Diet.class),
	energy("energy", Double.class),
	hunger_threshold("hunger_threshold", Double.class),
	energy_restoration("energy_restoration", Double.class);

	String value;
	Object classType;
	StatType(String value, Object classtype){
		this.value = value;
		this.classType = classtype;
	}

	StatType(String value){
		this.value = value;
		this.classType = String.class;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Object getClassType() {
		return classType;
	}

	public void setClassType(Object classType) {
		this.classType = classType;
	}
}
