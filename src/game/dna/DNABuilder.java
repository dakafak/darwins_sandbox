package game.dna;

import game.dna.traits.Trait;
import game.dna.traits.TraitPair;

public class DNABuilder {

	public static DNAString getChildDNAString(DNAString parent1, DNAString parent2){
		DNAString childString = new DNAString();
		TraitPair[] childTraits = new TraitPair[parent1.getTraitString().length];
		for(int i = 0; i < parent1.getTraitString().length; i++){
			TraitPair combinedTraits = combineTraits(parent1.getTraitString()[i], parent2.getTraitString()[i]);
			childTraits[i] = combinedTraits;
		}

		childString.setTraitString(childTraits);
		return childString;
	}

	public static DNAString getAsexualDNAString(DNAString parent){
		DNAString childString = new DNAString();
		TraitPair[] childTraits = new TraitPair[parent.getTraitString().length];

		for(int i = 0; i < parent.getTraitString().length; i++){
			TraitPair combinedTraits = combineTraits(parent.getTraitString()[i], parent.getTraitString()[i]);
			childTraits[i] = combinedTraits;
		}

		childString.setTraitString(childTraits);
		return childString;
	}

	//   C  c
	// c Cc cc
	// C CC Cc
	// combining Cc cC
	// 	results	 Cc cc CC Cc
	//			 11 12 21 22
	private static TraitPair combineTraits(TraitPair traitPair1, TraitPair traitPair2){
		int randomTraitPicker = (int)Math.floor(Math.random()*4);
		//TODO probably a better way of combining these than using if statements
		if(randomTraitPicker == 0){
			return new TraitPair(traitPair1.getTraits()[0], traitPair2.getTraits()[0]);
		} else if(randomTraitPicker == 1){
			return new TraitPair(traitPair1.getTraits()[0], traitPair2.getTraits()[1]);
		} else if(randomTraitPicker == 2){
			return new TraitPair(traitPair1.getTraits()[1], traitPair2.getTraits()[0]);
		} else if(randomTraitPicker == 3){
			return new TraitPair(traitPair1.getTraits()[1], traitPair2.getTraits()[1]);
		}

		return null;
	}
}
