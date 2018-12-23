package game.dna;

import game.dna.traits.Trait;

public class DNAString {

	Trait[] traitString;

	public Trait[] getTraitString() {
		return traitString;
	}

	public void setTraitString(Trait[] traitString) {
		this.traitString = traitString;
	}

	@Override
	public String toString(){
		StringBuilder traitStringBuilder = new StringBuilder();
		for(Trait trait : traitString){
			traitStringBuilder.append(trait.toString());
		}

		return traitStringBuilder.toString();
	}

}
