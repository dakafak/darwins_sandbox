package game.dna.traits;

/**
 * A string, combination of a given trait name and value, used as a key for trait-to-stat-modifications
 */
public class TraitNameAndValuePair {

	String key;

	public TraitNameAndValuePair(String traitName, String traitValue){
		key = traitName + "_" + traitValue;
	}

	public TraitNameAndValuePair(Trait trait){
		key = trait.getTraitType() + "_" + trait.getTraitDefinition();
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
}
