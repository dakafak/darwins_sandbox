package game;

import game.dna.DNABuilder;
import game.dna.DNAString;
import game.dna.traits.TraitPair;
import game.world.creatures.Creature;
import game.world.creatures.Sex;
import game.world.units.Location;
import game.world.units.Size;
import ui.StatisticsSave;
import ui.TraitLoader;
import ui.WorldStatisticsTool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static game.world.creatures.Sex.ASEXUAL;
import static game.world.creatures.Sex.FEMALE;
import static game.world.creatures.Sex.MALE;

public class World {

//	Tile[][] tileMap;
	List<Creature> creatures;

	TraitLoader traitLoader;
	WorldStatisticsTool worldStatisticsTool;
	MovementManager movementManager;

	double dayLength = 100;
	double worldDay;

//	int minWidth;
//	int maxWidth;
//	int minHeight;
//	int maxHeight;

	Location minWorldLocation;
	Location maxWorldLocation;
	Location worldLocation;
	Size worldSize;
	Size tileSize = new Size(5, 5);

	public World(int minWidth, int maxWidth, int minHeight, int maxHeight){
//		this.minWidth = minWidth;
//		this.maxWidth = maxWidth;
//		this.minHeight = minHeight;
//		this.maxHeight = maxHeight;

		minWorldLocation = new Location(minWidth, minHeight);
		maxWorldLocation = new Location(maxWidth, maxHeight);
		worldLocation = new Location(0, 0);
		worldSize = new Size(maxWidth - minWidth, maxHeight - minHeight);

//		tileMap = new Tile[width][height];//TODO may be more beneficial to create it with height first
		creatures = new ArrayList();
		creaturesToDelete = new LinkedList<>();

		traitLoader = new TraitLoader();
		worldStatisticsTool = new WorldStatisticsTool();
		movementManager = new MovementManager();
	}

	public void runWorldUpdates(long runningTime, double deltaUpdate){
		movementManager.moveCreatures(deltaUpdate, getCreatures(), worldDay, traitLoader.getTraitNameAndValueToCreatureStatModifiers(), minWorldLocation, maxWorldLocation);
		movementManager.tellCreaturesToWander(runningTime, getCreatures());
		movementManager.moveAndTryMatingCreatures(traitLoader.getTraitNameAndValueToCreatureStatModifiers(), worldDay, deltaUpdate, minWorldLocation, maxWorldLocation);
		movementManager.addNewChildCreaturesToWorldCreatureList(getCreatures(), worldStatisticsTool);
		adjustDay(deltaUpdate);
		checkCreatureLifeSpan();
		clearRemovedCreatures();
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
		Creature newCreature = new Creature(Math.random()*5 - 2.5, Math.random()*5 - 2.5, dnaString, sexOfCreature, traitLoader.getTraitNameAndValueToCreatureStatModifiers(), worldDay);
		getCreatures().add(newCreature);
		worldStatisticsTool.addTraitsForNewCreatures(Collections.singletonList(newCreature));
	}

	public void tryMatingCreatures(){//TODO remove this - temporary method for testing - or modify for range
		List<Creature> malesToMate = new ArrayList<>();
		List<Creature> femalesToMate = new ArrayList<>();
		List<Creature> asexualToMate = new ArrayList<>();

		//TODO eventually this should be changed to just check around the creature
		//TODO add a timer for creatures so they only mate so often, not every cycle
		for(int i = 0; i < getCreatures().size(); i++){
				Creature creature = getCreatures().get(i);
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
			Creature newCreature = new Creature(Math.random()*20 - 10, Math.random()*20 - 10, childString, Math.random() > .5 ? MALE : FEMALE, traitLoader.getTraitNameAndValueToCreatureStatModifiers(), worldDay);
			getCreatures().add(newCreature);
			worldStatisticsTool.addTraitsForNewCreatures(Collections.singletonList(newCreature));
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


	double cachedDeltaUpdate;
	double cachedDayChange;
	public void adjustDay(double deltaUpdate){
		if(deltaUpdate != cachedDeltaUpdate){
			cachedDeltaUpdate = deltaUpdate;
			cachedDayChange = cachedDeltaUpdate / dayLength;
		}
		worldDay += cachedDayChange;
	}

	public double getWorldDay(){
		return worldDay;
	}

	public void checkCreatureLifeSpan(){
		for(int i = 0; i < getCreatures().size(); i++){
			Creature creature = getCreatures().get(i);
			boolean creatureDies = creature.endedLifeSpan(worldDay);
			if(creatureDies){
				creaturesToDelete.add(creature);
			}
		}
	}

	List<Creature> creaturesToDelete;
	public void clearRemovedCreatures(){
		worldStatisticsTool.removeTraitsForNewCreatures(creaturesToDelete);
		getCreatures().removeAll(creaturesToDelete);
		creaturesToDelete.clear();
	}

	public WorldStatisticsTool getWorldStatisticsTool() {
		return worldStatisticsTool;
	}

	public Location getMinWorldLocation() {
		return minWorldLocation;
	}

	public void setMinWorldLocation(Location minWorldLocation) {
		this.minWorldLocation = minWorldLocation;
	}

	public Location getMaxWorldLocation() {
		return maxWorldLocation;
	}

	public void setMaxWorldLocation(Location maxWorldLocation) {
		this.maxWorldLocation = maxWorldLocation;
	}

	public Size getWorldSize() {
		return worldSize;
	}

	public void setWorldSize(Size worldSize) {
		this.worldSize = worldSize;
	}

	public Location getWorldLocation() {
		return worldLocation;
	}

	public void setWorldLocation(Location worldLocation) {
		this.worldLocation = worldLocation;
	}

	public Size getTileSize() {
		return tileSize;
	}

	public void setTileSize(Size tileSize) {
		this.tileSize = tileSize;
	}
}
