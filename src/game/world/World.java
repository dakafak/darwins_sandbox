package game.world;

import game.dna.DNABuilder;
import game.dna.DNAString;
import game.dna.traits.TraitPair;
import game.tiles.Tile;
import game.tiles.TileType;
import game.world.creatures.Creature;
import game.dna.stats.Sex;
import game.world.movement.MovementManager;
import game.world.plantlife.Plant;
import game.world.plantlife.PlantType;
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

import static game.dna.stats.Sex.ASEXUAL;
import static game.dna.stats.Sex.FEMALE;
import static game.dna.stats.Sex.MALE;

public class World {

	Tile[][] tileMap;//TODO move this to a worldMap class with methods for retrieving tiles by coordinates
	List<Creature> creatures;
	//TODO create a thread object that contains a list of creatures and have each thread maintain a separate list of creatures.
	// TODO these threads will have to control specific functionality to avoid concurrent exceptions
	// Could also add a mutex lock and have a set of 16 worker threads that run an update when ready
	// Maybe wrap the movement manager with threads and mutex lock the creature list
	// Could multithread the check for potential mating method, after building the coordinate to list of creature map,
	// that map can be read and not modified so multiple threads can run through subsections of the creature list and
	// check what creatures need to be added to the mating list, the mating list can have a mutext lock

	TraitLoader traitLoader;
	WorldStatisticsTool worldStatisticsTool;
	MovementManager movementManager;

	double dayLength = 10000;
	double worldDay;
	short maxPlantsPerTile = 5;
	Location minWorldLocation;

	Location maxWorldLocation;
	Location worldLocation;
	Size worldSize;

	public World(int minWidth, int maxWidth, int minHeight, int maxHeight){
		minWorldLocation = new Location(minWidth, minHeight);
		maxWorldLocation = new Location(maxWidth, maxHeight);
		worldLocation = new Location(0, 0);
		worldSize = new Size(maxWidth - minWidth, maxHeight - minHeight);

		tileMap = new Tile[(int)worldSize.getWidth()][(int)worldSize.getHeight()];//TODO may be more beneficial to create it with height first
		for(int y = 0; y < getTileMap().length; y++) {
			for (int x = 0; x < getTileMap()[0].length; x++) {
				tileMap[y][x] = new Tile(x + minWidth, y + minHeight, TileType.DIRT, worldDay);
			}
		}
		creatures = new ArrayList();
		creaturesToDelete = new LinkedList<>();

		traitLoader = new TraitLoader();
		worldStatisticsTool = new WorldStatisticsTool();
		movementManager = new MovementManager();
	}

	public Tile getTileFromCoordinates(int x, int y){
		try {
			if(coordinateExistsOnMap(x, y)) {
				int adjustedX = x - (int) minWorldLocation.getX();
				int adjustedY = y - (int) minWorldLocation.getY();

				return tileMap[adjustedY][adjustedX];
			}
		} catch (Exception e){
			System.out.println(e.getStackTrace().toString());
		}

		return null;
	}

	private boolean coordinateExistsOnMap(int x, int y){
		return x >= minWorldLocation.getX() && x < maxWorldLocation.getX() && y >= minWorldLocation.getY() && y < maxWorldLocation.getY();
	}

	public void runWorldUpdates(long runningTime, double deltaUpdate){
		Map<Long, List<Creature>> closestTileMapForCreatures = movementManager.getClosestTileMapForCreaturesMap(getCreatures());

		movementManager.setWanderDirectionForCreatures(runningTime, getCreatures());
		movementManager.tellAllCreaturesToWander(getCreatures(), deltaUpdate, minWorldLocation, maxWorldLocation);

		movementManager.checkForCreatureMatingForListOfCreatures(getCreatures(), closestTileMapForCreatures, worldDay, traitLoader.getTraitNameAndValueToCreatureStatModifiers(), traitLoader);
		movementManager.moveAndTryMatingCreatures(traitLoader.getTraitNameAndValueToCreatureStatModifiers(), worldDay, deltaUpdate, minWorldLocation, maxWorldLocation, traitLoader);
		movementManager.addNewChildCreaturesToWorldCreatureList(getCreatures(), worldStatisticsTool);

		movementManager.checkForHerbivoreFeeding(getCreatures(), this);
		movementManager.checkForCarnivoreFeeding(getCreatures(), closestTileMapForCreatures, worldDay);
		movementManager.moveAndTryEatingForHerbivores(deltaUpdate, minWorldLocation, maxWorldLocation);
		creaturesToDelete.addAll(movementManager.moveAndTryEatingForCarnivores(deltaUpdate, minWorldLocation, maxWorldLocation));

		adjustDay(deltaUpdate);
		checkCreatureLifeSpan();
		clearRemovedCreatures();
		addPlantsToTileMap();
	}

	private void addPlantsToTileMap() {
		for(int i = 0; i < tileMap.length; i++){
			for(int j = 0; j < tileMap[0].length; j++){
				Tile currentTile = tileMap[i][j];
				if(currentTile.canGrowPlants(worldDay) && currentTile.getPlants().size() < maxPlantsPerTile){
					boolean shouldGrowPlant = Math.random() < currentTile.getTileFertility();
					PlantType plantTypeToGrow = PlantType.GRASS;
					if(shouldGrowPlant){
						Plant newPlant = new Plant(currentTile.getLocation().getX() + Math.random(), currentTile.getLocation().getY() + Math.random(), plantTypeToGrow, currentTile);
						currentTile.addPlant(newPlant);
						currentTile.removeFertility();
					}
				}
			}
		}
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
			DNAString childString = DNABuilder.getChildDNAString(malesToMate.get(i).getCreatureDNAString(), femalesToMate.get(i).getCreatureDNAString(), traitLoader);
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
			boolean creatureDies = creature.shouldDie(worldDay);
			if(creatureDies){
				creaturesToDelete.add(creature);
			}
		}
	}

	List<Creature> creaturesToDelete;
	public void clearRemovedCreatures(){
		for(int i = 0; i < creaturesToDelete.size(); i++){
			Creature creatureToDelete = creaturesToDelete.get(i);
			Tile tileForCreature = getTileFromCoordinates((int) Math.round(creatureToDelete.getLocation().getX()), (int) Math.round(creatureToDelete.getLocation().getY()));
			if(tileForCreature != null) {
				tileForCreature.addFertility();
			}
		}

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

	public Tile[][] getTileMap() {
		return tileMap;
	}

	public void setTileMap(Tile[][] tileMap) {
		this.tileMap = tileMap;
	}
}
