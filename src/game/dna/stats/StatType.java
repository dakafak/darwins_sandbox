package game.dna.stats;

public enum StatType {
	color("color"),
	size("size", Double.class),
	speed("speed", Double.class);

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
