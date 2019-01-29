package game.dna.traits;

/**
 * A combination of two traits of the same type, used for holding dominant and recessive traits
 */
public class TraitPair {
	char[] characterRepresentation;
	Trait[] traits;

	public TraitPair(Trait trait1, Trait trait2){
		this.traits = new Trait[]{trait1, trait2};
		this.characterRepresentation = new char[]{traits[0].getTraitCharacter(), traits[1].getTraitCharacter()};
	}

	public char[] getCharacterRepresentation() {
		return characterRepresentation;
	}

	public void setCharacterRepresentation(char[] characterRepresentation) {
		this.characterRepresentation = characterRepresentation;
	}

	public Trait[] getTraits() {
		return traits;
	}

	public void setTraits(Trait[] traits) {
		this.traits = traits;
	}

	@Override
	public String toString(){
		StringBuilder traitStringBuilder = new StringBuilder();
		for(char characters : characterRepresentation){
			traitStringBuilder.append(characters);
		}

		return traitStringBuilder.toString();
	}
}
