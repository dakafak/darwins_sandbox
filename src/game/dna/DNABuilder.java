package game.dna;

import game.dna.traits.Trait;
import game.dna.traits.TraitPair;
import ui.TraitLoader;

public class DNABuilder {

	/**
	 * Generates a new DNA string, for a new child creature, by combining two parent's traits
	 *
	 * @param parent1
	 * @param parent2
	 * @param traitLoader
	 * @return
	 */
	public static DNAString getChildDNAString(DNAString parent1, DNAString parent2, TraitLoader traitLoader) {
		DNAString childString = new DNAString();
		TraitPair[] childTraits = new TraitPair[parent1.getTraitString().length];
		for (int i = 0; i < parent1.getTraitString().length; i++) {
			TraitPair combinedTraits = combineTraits(parent1.getTraitString()[i], parent2.getTraitString()[i], traitLoader);
			childTraits[i] = combinedTraits;
		}

		childString.setTraitString(childTraits);
		return childString;
	}

	/**
	 * Generates a new DNA string, for a new child creature, based on its parent's DNAString
	 *
	 * @param parent
	 * @param traitLoader
	 * @return
	 */
	public static DNAString getAsexualDNAString(DNAString parent, TraitLoader traitLoader) {
		DNAString childString = new DNAString();
		TraitPair[] childTraits = new TraitPair[parent.getTraitString().length];

		for (int i = 0; i < parent.getTraitString().length; i++) {
			TraitPair combinedTraits = combineTraits(parent.getTraitString()[i], parent.getTraitString()[i], traitLoader);
			childTraits[i] = combinedTraits;
		}

		childString.setTraitString(childTraits);
		return childString;
	}

	/**
	 * Combines the traits between two trait pairs to generate a new TraitPair, by using punnett square logic
	 *
	 * @param traitPair1
	 * @param traitPair2
	 * @param traitLoader
	 * @return
	 */
	private static TraitPair combineTraits(TraitPair traitPair1, TraitPair traitPair2, TraitLoader traitLoader) {
		int randomTraitPicker = (int) Math.floor(Math.random() * 4);
		//TODO probably a better way of combining these than using if statements
		if (randomTraitPicker == 0) {
			return new TraitPair(getTraitWithPossibilityOfMutation(traitPair1.getTraits()[0], traitLoader), getTraitWithPossibilityOfMutation(traitPair2.getTraits()[1], traitLoader));
		} else if (randomTraitPicker == 1) {
			return new TraitPair(getTraitWithPossibilityOfMutation(traitPair1.getTraits()[0], traitLoader), getTraitWithPossibilityOfMutation(traitPair2.getTraits()[1], traitLoader));
		} else if (randomTraitPicker == 2) {
			return new TraitPair(getTraitWithPossibilityOfMutation(traitPair1.getTraits()[1], traitLoader), getTraitWithPossibilityOfMutation(traitPair2.getTraits()[1], traitLoader));
		} else if (randomTraitPicker == 3) {
			return new TraitPair(getTraitWithPossibilityOfMutation(traitPair1.getTraits()[1], traitLoader), getTraitWithPossibilityOfMutation(traitPair2.getTraits()[1], traitLoader));
		}

		return null;
	}

	/**
	 * Checks if the given trait should mutate, if so, it will return a new random trait
	 *
	 * @param originalTrait
	 * @param traitLoader
	 * @return
	 */
	private static Trait getTraitWithPossibilityOfMutation(Trait originalTrait, TraitLoader traitLoader) {
		if (Math.random() < .01) {
			return traitLoader.getRandomTrait(originalTrait.getTraitType());
		}

		return originalTrait;
	}
}
