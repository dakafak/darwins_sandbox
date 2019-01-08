package game.dna.traits;

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
