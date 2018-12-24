package game;

import game.creatures.Creature;
import game.creatures.Sex;
import game.dna.DNABuilder;
import game.dna.DNAString;
import game.dna.traits.TraitLoader;
import game.dna.traits.TraitPair;

import java.util.ArrayList;
import java.util.List;

public class World {

	Tile[][] tileMap;
	List<Creature> creatures;

	TraitLoader traitLoader;

	public World(int width, int height){
		tileMap = new Tile[width][height];//TODO may be more beneficial to create it with height first
		creatures = new ArrayList();

		traitLoader = new TraitLoader();
	}

	public void addRandomCreature(Sex sexOfCreature){
		DNAString newDNAStringForCreature = new DNAString();
		TraitPair[] allTraitsForDNAString = new TraitPair[traitLoader.getTraitTypesInOrder().size()];
		for(int i = 0; i < traitLoader.getTraitTypesInOrder().size(); i++){
			allTraitsForDNAString[i] = traitLoader.getRandomTraitPair(traitLoader.getTraitTypesInOrder().get(i));
		}
		newDNAStringForCreature.setTraitString(allTraitsForDNAString);

		addRandomCreature(newDNAStringForCreature, sexOfCreature);
	}

	public void addRandomCreature(DNAString dnaString, Sex sexOfCreature){
		Creature newCreature = new Creature();
		newCreature.setCreatureDNAString(dnaString);
		newCreature.setSexOfCreature(sexOfCreature);
		//TODO should also set stats
		creatures.add(newCreature);
	}

	public void tryMatingCreatures(){
		int parent1ID = (int)Math.floor(Math.random()*creatures.size());
		Creature parent1ToCombine = creatures.get(parent1ID);

		//TODO change to check creatures mating type trait when that's added
		//TODO

		if(creatures.size() == 1) {
			DNAString childString = DNABuilder.getAsexualDNAString(parent1ToCombine.getCreatureDNAString());
			Creature newCreature = new Creature();
			newCreature.setCreatureDNAString(childString);
			creatures.add(newCreature);
		} else {
			List<Creature> malesToMate = new ArrayList<>();
			List<Creature> femalesToMate = new ArrayList<>();

			int parent2ID = parent1ID > 0 ? parent1ID - 1 : parent1ID + 1;
			Creature parent2ToCombine = creatures.get(parent2ID);
			DNAString childString = DNABuilder.getChildDNAString(parent1ToCombine.getCreatureDNAString(), parent2ToCombine.getCreatureDNAString());
			Creature newCreature = new Creature();
			newCreature.setCreatureDNAString(childString);
			creatures.add(newCreature);
		}
	}

}
