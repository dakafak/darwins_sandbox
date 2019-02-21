package game.dna;

import game.dna.traits.TraitPair;

public class DNAString {

	TraitPair[] traitString;

	public TraitPair[] getTraitString() {
		return traitString;
	}

	public void setTraitString(TraitPair[] traitString) {
		this.traitString = traitString;
	}

	@Override
	public String toString() {
		StringBuilder traitStringBuilder = new StringBuilder();
		for (TraitPair trait : traitString) {
			traitStringBuilder.append(trait.toString());
		}

		return traitStringBuilder.toString();
	}

}
