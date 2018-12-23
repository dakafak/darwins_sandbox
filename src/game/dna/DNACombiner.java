package game.dna;

import game.dna.traits.Trait;

public class DNACombiner {

	public static DNAString getChildDNAString(DNAString parent1, DNAString parent2){
		DNAString childString = new DNAString();
		Trait[] childTraits = new Trait[parent1.getTraitString().length];
		for(int i = 0; i < parent1.getTraitString().length; i++){
			char[] combinedTraits = combineTraits(parent1.getTraitString()[i].getCharacterRepresentation(), parent2.getTraitString()[i].getCharacterRepresentation());
			childTraits[i] = new Trait(combinedTraits);
		}

		childString.setTraitString(childTraits);
		return childString;
	}

	public static DNAString getAsexualDNAString(DNAString parent){
		return null;
	}

	//   C  c
	// c Cc cc
	// C CC Cc
	// combining Cc cC
	// 	results	 Cc cc CC Cc
	//			 11 12 21 22
	private static char[] combineTraits(char[] trait1, char[] trait2){
		int randomTraitPicker = (int)Math.floor(Math.random()*4);
		//TODO probably a better way of combining these than using if statements
		if(randomTraitPicker == 0){
			return ("" + trait1[0] + trait2[0]).toCharArray();
		} else if(randomTraitPicker == 1){
			return ("" + trait1[0] + trait2[1]).toCharArray();
		} else if(randomTraitPicker == 2){
			return ("" + trait1[1] + trait2[0]).toCharArray();
		} else if(randomTraitPicker == 3){
			return ("" + trait1[1] + trait2[1]).toCharArray();
		}

		return null;
	}
}
