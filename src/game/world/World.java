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

	/**
	 * Given coordinates, returns the corresponding tile, if it exists
	 *
	 * @param x
	 * @param y
	 * @return
	 */
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

	/**
	 * Determines whether or not the given coordinates exist on the map (if the coordinates are not out of bounds of the array)
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	private boolean coordinateExistsOnMap(int x, int y){
		return x >= minWorldLocation.getX() && x < maxWorldLocation.getX() && y >= minWorldLocation.getY() && y < maxWorldLocation.getY();
	}

	/**
	 * Processes all updates for the world and anything within the world. LET'S PLAY GOD. MUAHAHAHAHAHAHA
	 *
	 * @param deltaUpdate
	 */
	public void runWorldUpdates(double deltaUpdate){
		Map<Long, List<Creature>> closestTileMapForCreatures = movementManager.getClosestTileMapForCreaturesMap(getCreatures());

		movementManager.setWanderDirectionForCreatures(worldDay, getCreatures());
		movementManager.tellAllCreaturesToWander(getCreatures(), deltaUpdate, minWorldLocation, maxWorldLocation);

		movementManager.checkForCreatureMatingForListOfCreatures(getCreatures(), closestTileMapForCreatures, worldDay, traitLoader.getTraitNameAndValueToCreatureStatModifiers(), traitLoader);
		movementManager.moveAndTryMatingCreatures(traitLoader.getTraitNameAndValueToCreatureStatModifiers(), worldDay, deltaUpdate, minWorldLocation, maxWorldLocation, traitLoader);
		movementManager.addNewChildCreaturesToWorldCreatureList(getCreatures(), worldStatisticsTool);

		movementManager.checkForHerbivoreFeeding(getCreatures(), this);
		movementManager.checkForCarnivoreFeeding(getCreatures(), closestTileMapForCreatures);
		movementManager.moveAndTryEatingForHerbivores(deltaUpdate, minWorldLocation, maxWorldLocation);
		addCreaturesToDelete(movementManager.moveAndTryEatingForCarnivores(deltaUpdate, minWorldLocation, maxWorldLocation), "ate by a carnivore");

		adjustDay(deltaUpdate);
		checkCreatureLifeSpan();
		clearRemovedCreatures();
		addPlantsToTileMap(worldDay);
	}

	/**
	 * Given the current day, iterate through the tile map and attempt to grow plants on each tile
	 *
	 * @param currentDay
	 */
	private void addPlantsToTileMap(double currentDay) {
		for(int i = 0; i < tileMap.length; i++){
			for(int j = 0; j < tileMap[0].length; j++){
				Tile currentTile = tileMap[i][j];
				if(currentTile.canGrowPlants(currentDay) && currentTile.getPlants().size() < maxPlantsPerTile){
					PlantType plantTypeToGrow = PlantType.GRASS;
					if(currentTile.canGrowPlant()){
						Plant newPlant = new Plant(currentTile.getLocation().getX() + Math.random(), currentTile.getLocation().getY() + Math.random(), plantTypeToGrow, currentTile);
						currentTile.addPlant(newPlant);
						currentTile.removeFertility();
					}
				}
			}
		}
	}

	/**
	 * Adds a random creature, of the given sex, to the world
	 *
	 * @param sexOfCreature
	 */
	public void addRandomCreature(Sex sexOfCreature, double x, double y){
		if(coordinateExistsOnMap((int)x, (int)y)) {
			DNAString newDNAStringForCreature = new DNAString();
			TraitPair[] allTraitsForDNAString = new TraitPair[traitLoader.getTraitTypesInOrder().size()];
			for (int i = 0; i < traitLoader.getTraitTypesInOrder().size(); i++) {
				allTraitsForDNAString[i] = traitLoader.getRandomTraitPair(traitLoader.getTraitTypesInOrder().get(i));
			}
			newDNAStringForCreature.setTraitString(allTraitsForDNAString);

			addRandomCreature(newDNAStringForCreature, sexOfCreature, x, y);
		}
	}

	/**
	 * Adds a random creature, of the given sex and DNAString, to the world
	 *
	 * @param dnaString
	 * @param sexOfCreature
	 */
	public void addRandomCreature(DNAString dnaString, Sex sexOfCreature, double x, double y){
		Creature newCreature = new Creature(x, y, dnaString, sexOfCreature, traitLoader.getTraitNameAndValueToCreatureStatModifiers(), worldDay);
		getCreatures().add(newCreature);
		worldStatisticsTool.addTraitsForNewCreatures(Collections.singletonList(newCreature));//TODO STAT determine if this is in the correct location
	}

	/**
	 * TODO remove this - temporary method for testing - or modify for range
	 * A manual method of iterating through all creatures and mating random pairs
	 */
	public void tryMatingCreatures(){//TODO remove this - temporary method for testing - or modify for range
		List<Creature> malesToMate = new ArrayList<>();
		List<Creature> femalesToMate = new ArrayList<>();
		List<Creature> asexualToMate = new ArrayList<>();

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
			worldStatisticsTool.addTraitsForNewCreatures(Collections.singletonList(newCreature));//TODO STAT determine if this is in the correct location
		}
	}

	/**
	 * Returns all creatures for the world
	 *
	 * @return
	 */
	public List<Creature> getCreatures() {
		return creatures;
	}

	double cachedDeltaUpdate;
	double cachedDayChange;

	/**
	 * Given a delta update, adjust the day by the scaled amount
	 *
	 * @param deltaUpdate
	 */
	public void adjustDay(double deltaUpdate){
		if(deltaUpdate != cachedDeltaUpdate){
			cachedDeltaUpdate = deltaUpdate;
			cachedDayChange = cachedDeltaUpdate / dayLength;
		}
		worldDay += cachedDayChange;
	}

	/**
	 * Returns the current day of the world
	 *
	 * @return
	 */
	public double getWorldDay(){
		return worldDay;
	}

	/**
	 * Iterates through all creatures and determines of any should die of old age :(
	 */
	public void checkCreatureLifeSpan(){
		for(int i = 0; i < getCreatures().size(); i++){
			Creature creature = getCreatures().get(i);
			boolean creatureDies = creature.shouldDie(worldDay);
			if(creatureDies){
				addCreatureToDelete(creature, "old age");
			}
		}
	}

	List<Creature> creaturesToDelete;

	public void addCreaturesToDelete(List<Creature> creatures, String reason){
		for(int i = 0; i < creatures.size(); i++){
			addCreatureToDelete(creatures.get(i), reason);
		}
	}

	public void addCreatureToDelete(Creature creature, String reason){
		if(!creaturesToDelete.contains(creature)){
			System.out.println(creature + " | " + reason + " | worldSizeWithCreature: " + getCreatures().size());
			creaturesToDelete.add(creature);
		}
	}

	/**
	 * Removes creatures from the delete queue and updates the trait statistics
	 */
	public void clearRemovedCreatures(){
		for(int i = 0; i < creaturesToDelete.size(); i++){
			Creature creatureToDelete = creaturesToDelete.get(i);
			for(int tileToAddY = (int) creatureToDelete.getLocation().getY() - 1; tileToAddY <= creatureToDelete.getLocation().getY() + 1; tileToAddY++){
				for(int tileToAddX = (int) creatureToDelete.getLocation().getX() - 1; tileToAddX <= creatureToDelete.getLocation().getX() + 1; tileToAddX++){
					if(coordinateExistsOnMap(tileToAddX, tileToAddY)){
						getTileFromCoordinates(tileToAddX, tileToAddY).addFertilityFromDeath();
					}
				}
			}
//			worldStatisticsTool.removeTraitsForCreatures(Collections.singletonList(creatureToDelete));
			creaturesToDelete.remove(creatureToDelete);
			getCreatures().remove(creatureToDelete);
		}
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
