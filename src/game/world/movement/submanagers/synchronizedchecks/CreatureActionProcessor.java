package game.world.movement.submanagers.synchronizedchecks;

import game.dna.stats.Diet;
import game.dna.stats.Sex;
import game.tiles.Tile;
import game.world.World;
import game.world.creatures.Creature;
import game.world.creatures.CreatureState;
import game.world.movement.movement_pairs.FeedingTargetCreature;
import game.world.movement.movement_pairs.FeedingTargetPlant;
import game.world.movement.movement_pairs.MatingPair;
import game.world.plantlife.Plant;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static game.dna.stats.Sex.FEMALE;
import static game.dna.stats.Sex.MALE;
import static game.world.movement.CreatureManager.MAX_CREATURE_VIEWING_DISTANCE;
import static game.world.movement.CreatureManager.getLocationLongFromCoordinates;

public class CreatureActionProcessor implements Runnable{

	// Data necessary for processing
	private LinkedList<Creature> creaturesToProcess;
	double currentDay;
	private Map<Long, LinkedList<Creature>> closestTileMapForCreatures;
	private World world;

	// Result data from processing the above data
	private LinkedList<Creature> childCreaturesToAddToWorld;
	private LinkedList<FeedingTargetPlant> herbivorePairs;
	private LinkedList<FeedingTargetCreature> carnivorePairs;
	private LinkedList<MatingPair> matingPairs;

	public CreatureActionProcessor(){
		creaturesToProcess = new LinkedList<>();
		childCreaturesToAddToWorld = new LinkedList<>();
		herbivorePairs = new LinkedList<>();
		carnivorePairs = new LinkedList<>();
		matingPairs = new LinkedList<>();
	}

	public void prepareProcessorWithNewData(double currentDay, LinkedList<Creature> creaturesToProcess, Map<Long, LinkedList<Creature>> closestTileMapForCreatures, World world){
		this.currentDay = currentDay;
		this.creaturesToProcess = creaturesToProcess;
		this.closestTileMapForCreatures = closestTileMapForCreatures;
		this.world = world;
	}

	@Override
	public void run() {
		childCreaturesToAddToWorld.clear();
		herbivorePairs.clear();
		carnivorePairs.clear();
		matingPairs.clear();

		checkForCreatureMatingForListOfCreatures(creaturesToProcess, closestTileMapForCreatures, currentDay);
		checkForNewHerbivoreFeedingPairs(creaturesToProcess, world);
		checkForNewCarnivoreFeedingPairs(creaturesToProcess, closestTileMapForCreatures);
	}

	public LinkedList<Creature> getChildCreaturesToAddToWorld() {
		return childCreaturesToAddToWorld;
	}

	public LinkedList<FeedingTargetPlant> getHerbivorePairs() {
		return herbivorePairs;
	}

	public LinkedList<FeedingTargetCreature> getCarnivorePairs() {
		return carnivorePairs;
	}

	public LinkedList<MatingPair> getMatingPairs() {
		return matingPairs;
	}

	/**
	 * For each creature in a list of creatures, call checkForCreatureMating(creature, ...)
	 *
	 * @param creatures
	 * @param closestTileMapForCreatures
	 * @param currentDay
	 */
	private void checkForCreatureMatingForListOfCreatures(LinkedList<Creature> creatures, Map<Long, LinkedList<Creature>> closestTileMapForCreatures, double currentDay){
		for(int i = 0; i < creatures.size(); i++){
			Creature creature = creatures.get(i);
			checkForCreatureMating(creature, getCreaturesInRange(creature, closestTileMapForCreatures), currentDay);
		}
	}

	/**
	 * For a given creature, iterate through all creatures in range and look for a potential mate
	 *
	 * @param creature
	 * @param creaturesInRange
	 * @param currentDay
	 */
	private void checkForCreatureMating(Creature creature, List<Creature> creaturesInRange, double currentDay){
		if(creature.getSexOfCreature() == Sex.ASEXUAL && creature.canMate(currentDay)){

		} else {
			for(int i = 0; i < creaturesInRange.size(); i++){
				Creature creatureInRange = creaturesInRange.get(i);
				checkMatingForCreatureAndCreatureInRange(creature, creatureInRange, currentDay);
			}
		}
	}

	/**
	 * For a given creature and a potential mate in range, determine if a mating is possible
	 *
	 * @param creature
	 * @param creatureInRange
	 * @param currentDay
	 */
	private void checkMatingForCreatureAndCreatureInRange(Creature creature, Creature creatureInRange, double currentDay){
		if(creature.isDoingNothing() && creatureInRange.isDoingNothing()
				&& creature.canMate(currentDay) && creatureInRange.canMate(currentDay)){
			if(creature.getSpecies() == creatureInRange.getSpecies()) {
				if ((creature.getSexOfCreature() == MALE && creatureInRange.getSexOfCreature() == FEMALE) ||
						(creature.getSexOfCreature() == FEMALE && creatureInRange.getSexOfCreature() == MALE)) {
					creature.setCreatureState(CreatureState.MATING);
					creatureInRange.setCreatureState(CreatureState.MATING);
					matingPairs.add(new MatingPair(creature, creatureInRange));
				}
			}
		}
	}

	/**
	 * For a list of herbivore creatures, check if any can eat and are nearby a plant, if so create a new FeedingTargetPlant
	 *
	 * @param creatures
	 * @param world
	 */
	private void checkForNewHerbivoreFeedingPairs(List<Creature> creatures, World world){
		for(int i = 0; i < creatures.size(); i++){
			Creature creature = creatures.get(i);
			if(creature.getDiet() == Diet.HERBIVORE && creature.isDoingNothing() && creature.isHungry()) {
				Plant nearestPlantToCreature = getNearestPlantToCreature(creature, world);
				if(nearestPlantToCreature != null) {
					creature.setCreatureState(CreatureState.EATING);
					herbivorePairs.add(new FeedingTargetPlant(creature, nearestPlantToCreature));
					break;
				}
			}
		}
	}

	/**
	 * For a list of carnivore creatures, check if any can eat and are nearby another creature
	 *
	 * @param creatures
	 * @param closestTileMapForCreatures
	 */
	private void checkForNewCarnivoreFeedingPairs(LinkedList<Creature> creatures, Map<Long, LinkedList<Creature>> closestTileMapForCreatures){
		for(int i = 0; i < creatures.size(); i++){
			Creature creature = creatures.get(i);
			if(creature.getDiet() == Diet.CARNIVORE && creature.isHungry()) {
				checkForCarnvioreFeedingPairsInRange(creature, getCreaturesInRange(creature, closestTileMapForCreatures));
			}
		}
	}

	/**
	 * For a given carnivore, check if any creatures within range are potential food
	 *
	 * @param creature
	 * @param creatures
	 */
	private void checkForCarnvioreFeedingPairsInRange(Creature creature, List<Creature> creatures){
		for(int i = 0; i < creatures.size(); i++){
			Creature potentialPrey = creatures.get(i);
			//TODO add if checks for species once that's added
			if(creature != null && potentialPrey != null && creature.getSpecies() != potentialPrey.getSpecies() && creature != potentialPrey){
				creature.setCreatureState(CreatureState.EATING);
				potentialPrey.setCreatureState(CreatureState.FLEEING);
				carnivorePairs.add(new FeedingTargetCreature(creature, potentialPrey));
				break;
			}
		}
	}

	/**
	 * Searches in the creatures max view distance for the closest plant
	 *
	 * @param creature
	 * @param world
	 * @return
	 */
	private Plant getNearestPlantToCreature(Creature creature, World world) {
		int creatureX = (int) Math.round(creature.getLocation().getX());
		int creatureY = (int) Math.round(creature.getLocation().getY());

		// center is (h, k)
		//(x – h)2 + (y – k)2 = r2
		//TODO consider adding a circle check instead of square
		for(int radius = 0; radius < MAX_CREATURE_VIEWING_DISTANCE; radius++){
			// Top and bottom bar of square
			for(int y = creatureY - radius; y <= creatureY + radius; y += radius != 0 ? radius*2 : 1) {
				for (int x = creatureX - radius; x <= creatureX + radius; x++) {
					Tile currentTile = world.getTileFromCoordinates(x, y);
					if (currentTile != null && !currentTile.getPlants().isEmpty()) {
						return currentTile.getPlants().get(0);
					}
				}
			}

			for(int y = creatureY - radius + 1; y < creatureY + radius; y ++) {
				for (int x = creatureX - radius + 1; x < creatureX + radius; x += radius != 0 ? radius*2 : 1) {
					Tile currentTile = world.getTileFromCoordinates(x, y);
					if (currentTile != null && !currentTile.getPlants().isEmpty()) {
						return currentTile.getPlants().get(0);
					}
				}
			}

		}

		return null;
	}

	/**
	 * For a given creature, return a list of creatures within the max view distance range
	 *
	 * @param creature
	 * @param closestTileMapForCreatures
	 * @return
	 */
	private List<Creature> getCreaturesInRange(Creature creature, Map<Long, LinkedList<Creature>> closestTileMapForCreatures){
		List<Creature> creaturesInRange = new ArrayList<>();
		int creatureX = (int) Math.round(creature.getLocation().getX());
		int creatureY = (int) Math.round(creature.getLocation().getY());

		for(int i = creatureX - MAX_CREATURE_VIEWING_DISTANCE; i < creatureX + MAX_CREATURE_VIEWING_DISTANCE; i++){
			for(int j = creatureY - MAX_CREATURE_VIEWING_DISTANCE; j < creatureY + MAX_CREATURE_VIEWING_DISTANCE; j++){
				long locationLong = getLocationLongFromCoordinates(i, j);
				if(closestTileMapForCreatures.containsKey(locationLong)) {
					creaturesInRange.addAll(closestTileMapForCreatures.get(locationLong));
				}
			}
		}

		return creaturesInRange;
	}
}
