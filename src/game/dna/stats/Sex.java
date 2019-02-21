package game.dna.stats;

/**
 * Used to define a creature's sex. Used for determining trait combining
 */
public enum Sex {
	MALE("Male"), FEMALE("Female"), ASEXUAL("Asexual");

	private String sexString;

	Sex(String sex) {
		this.sexString = sex;
	}

	public String getSexString() {
		return sexString;
	}

	public void setSexString(String sexString) {
		this.sexString = sexString;
	}
}
