package game;

import game.creatures.Creature;
import game.creatures.Sex;
import game.dna.DNABuilder;
import game.dna.DNAString;
import game.dna.traits.TraitLoader;
import game.dna.traits.TraitPair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static game.creatures.Sex.*;

public class World {

	Tile[][] tileMap;
	List<Creature> creatures;

	TraitLoader traitLoader;
	WorldStatisticsTool worldStatisticsTool;

	public World(int width, int height){
		tileMap = new Tile[width][height];//TODO may be more beneficial to create it with height first
		creatures = new ArrayList();

		traitLoader = new TraitLoader();
		worldStatisticsTool = new WorldStatisticsTool();
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
		List<Creature> malesToMate = new ArrayList<>();
		List<Creature> femalesToMate = new ArrayList<>();
		List<Creature> asexualToMate = new ArrayList<>();

		//TODO eventually this should be changed to just check around the creature
		//TODO add a timer for creatures so they only mate so often, not every cycle
		for(Creature creature : creatures){
			if(creature.getSexOfCreature().equals(MALE)){
				malesToMate.add(creature);
			} else if(creature.getSexOfCreature().equals(FEMALE)){
				femalesToMate.add(creature);
			} else if(creature.getSexOfCreature().equals(ASEXUAL)){
				asexualToMate.add(creature);
			}
		}

		int numberOfMatings = malesToMate.size() <= femalesToMate.size() ? malesToMate.size() : femalesToMate.size();
		for(int i = 0; i < numberOfMatings; i++){
			DNAString childString = DNABuilder.getChildDNAString(malesToMate.get(i).getCreatureDNAString(), femalesToMate.get(i).getCreatureDNAString());
			Creature newCreature = new Creature();
			newCreature.setCreatureDNAString(childString);
			newCreature.setSexOfCreature(Math.random() > .5 ? MALE : FEMALE);
			creatures.add(newCreature);
		}

		//TODO add loop for reproducing the asexual group -- after adding the asexual reproduce function
	}

	public List<Creature> getCreatures() {
		return creatures;
	}

	public void setCreatures(List<Creature> creatures) {
		this.creatures = creatures;
	}

	public void printWorldStatistics(StatisticsSave statisticsSave){
		System.out.println("---- World Statistics ----");
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(statisticsSave.getTotalNumberOfCreatures());
		stringBuilder.append("(");
		stringBuilder.append(statisticsSave.getNumberMaleCreatures());
		stringBuilder.append("M ");
		stringBuilder.append(statisticsSave.getNumberFemaleCreatures());
		stringBuilder.append("F ");
		stringBuilder.append(statisticsSave.getNumberAsexualCreatures());
		stringBuilder.append("A");
		stringBuilder.append(")");
		stringBuilder.append(System.lineSeparator());

		Map<String, Integer> traitPopularityMap = statisticsSave.getTraitPopularityMap();
		for(String traitKey : traitPopularityMap.keySet()){
			stringBuilder.append(traitKey);
			stringBuilder.append("\t");
			stringBuilder.append(traitPopularityMap.get(traitKey));
			stringBuilder.append(System.lineSeparator());
		}

		System.out.println(stringBuilder.toString());
	}

	public StatisticsSave getStatisticsSaveForCurrentWorld(){
		StatisticsSave save = new StatisticsSave();

		save.setTotalNumberOfCreatures(worldStatisticsTool.getNumberOfCreatures(this));
		save.setNumberMaleCreatures(worldStatisticsTool.getNumberMaleCreatures(this));
		save.setNumberFemaleCreatures(worldStatisticsTool.getNumberFemaleCreatures(this));
		save.setNumberAsexualCreatures(worldStatisticsTool.getNumberAsexualCreatures(this));
		save.setTraitPopularityMap(worldStatisticsTool.getTraitPopularityMap(this, true));

		return save;
	}
}
