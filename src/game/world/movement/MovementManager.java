package game.world.movement;

import game.dna.DNABuilder;
import game.dna.DNAString;
import game.dna.stats.Diet;
import game.dna.traits.CreatureStatModifier;
import game.tiles.Tile;
import game.world.World;
import game.world.creatures.Creature;
import game.world.creatures.CreatureState;
import game.dna.stats.Sex;
import game.world.movement.movement_pairs.FeedingTargetCreature;
import game.world.movement.movement_pairs.FeedingTargetPlant;
import game.world.movement.movement_pairs.MatingPair;
import game.world.plantlife.Plant;
import game.world.units.Location;
import ui.TraitLoader;
import ui.WorldStatisticsTool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static game.dna.stats.Sex.FEMALE;
import static game.dna.stats.Sex.MALE;

public class MovementManager {

	List<MatingPair> matingPairs;
	List<FeedingTargetPlant> herbivorePairs;
	List<FeedingTargetCreature> carnivorePairs;
	List<Creature> childCreaturesToAddToWorld;


	private final int maxCreatureViewDistance = 5;// Primarily used as the max distance in tiles to check around each creature - when determining collisions

	public MovementManager(){
		matingPairs = new ArrayList<>();
		herbivorePairs = new ArrayList<>();
		carnivorePairs = new ArrayList<>();
		childCreaturesToAddToWorld = new LinkedList<>();
	}

	/**
	 * Creates a map, location->all creatures at that location
	 *
	 * @param creatures
	 * @return
	 */
	public Map<Long, List<Creature>> getClosestTileMapForCreaturesMap(List<Creature> creatures){
		Map<Long, List<Creature>> closestTileMapForCreatures = new HashMap<>();

		for(int i = 0; i < creatures.size(); i++){
			Creature creature = creatures.get(i);
			long locationLong = getLocationLongFromCoordinates(creature.getLocation().getX(), creature.getLocation().getY());
			if(closestTileMapForCreatures.containsKey(locationLong) && closestTileMapForCreatures.get(locationLong) != null){
				List<Creature> creaturesAtLocation = closestTileMapForCreatures.get(locationLong);
				creaturesAtLocation.add(creature);
			} else {
				List<Creature> newCreatureListForLocation = new ArrayList<>();
				newCreatureListForLocation.add(creature);
				closestTileMapForCreatures.put(locationLong, newCreatureListForLocation);
			}
		}

		return closestTileMapForCreatures;
	}

	//TODO create a loop to iterate through a surrounding area STARTING AT THE CENTER
	// 		TODO also consider just returning first plant found
	//			TODO this does not currently choose the nearest plant, needs work

	/**
	 * Searches in the creatures max view distance for the closest plant
	 *
	 * @param creature
	 * @param world
	 * @return
	 */
	private Plant getNearestPlantToCreature(Creature creature, World world) {
		List<Plant> plantsInRange = new ArrayList<>();
		int creatureX = (int) Math.round(creature.getLocation().getX());
		int creatureY = (int) Math.round(creature.getLocation().getY());

		for(int x = creatureX - maxCreatureViewDistance; x < creatureX + maxCreatureViewDistance; x++){
			for(int y = creatureY - maxCreatureViewDistance; y < creatureY + maxCreatureViewDistance; y++){
				Tile tileWithPlants = world.getTileFromCoordinates(x, y);
				if(tileWithPlants != null) {
					plantsInRange.addAll(tileWithPlants.getPlants());
					if (!tileWithPlants.getPlants().isEmpty()) {
						return tileWithPlants.getPlants().get(0);
					}
				}
			}
		}

		return null;
	}

	/**
	 * For a list of herbivore creatures, check if any can eat and are nearby a plant, if so create a new FeedingTargetPlant
	 *
	 * @param creatures
	 * @param world
	 */
	public void checkForHerbivoreFeeding(List<Creature> creatures, World world){
		for(int i = 0; i < creatures.size(); i++){
			Creature creature = creatures.get(i);
			if(creature.getDiet() == Diet.HERBIVORE && creature.getCreatureState() == CreatureState.WANDERING && creature.isHungry()) {
				Plant nearestPlantToCreature = getNearestPlantToCreature(creature, world);
				if(nearestPlantToCreature != null) {
					creature.setCreatureState(CreatureState.EATING);
					herbivorePairs.add(new FeedingTargetPlant(creature, nearestPlantToCreature));
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
	public void checkForCarnivoreFeeding(List<Creature> creatures, Map<Long, List<Creature>> closestTileMapForCreatures){
		for(int i = 0; i < creatures.size(); i++){
			Creature creature = creatures.get(i);
			if(creature.getDiet() == Diet.CARNIVORE && creature.isHungry()) {
				checkForCarnvioreFeedingPairsInRange(creature, getCreaturesInRange(creature, closestTileMapForCreatures));
			}
		}
	}

	/**
	 * For all creatures in a list, checks whether a creature is in a wandering state and then move that creature
	 *
	 * @param creatures
	 * @param deltaUpdate
	 * @param minWorldLocation
	 * @param maxWorldLocation
	 */
	public void tellAllCreaturesToWander(List<Creature> creatures, double deltaUpdate, Location minWorldLocation, Location maxWorldLocation){
		for(int i = 0; i < creatures.size(); i++){
			Creature creature = creatures.get(i);
			if(creature.getCreatureState() == CreatureState.WANDERING) {
				creature.wander(deltaUpdate, minWorldLocation, maxWorldLocation);
			}
		}
	}

	/**
	 * For each creature in a list of creatures, call checkForCreatureMating(creature, ...)
	 *
	 * @param creatures
	 * @param closestTileMapForCreatures
	 * @param currentDay
	 * @param traitNameAndValueToCreatureStatModifiers
	 * @param traitLoader
	 */
	public void checkForCreatureMatingForListOfCreatures(List<Creature> creatures, Map<Long, List<Creature>> closestTileMapForCreatures, double currentDay, Map<String, List<CreatureStatModifier>> traitNameAndValueToCreatureStatModifiers, TraitLoader traitLoader){
		for(int i = 0; i < creatures.size(); i++){
			Creature creature = creatures.get(i);
			checkForCreatureMating(creature, getCreaturesInRange(creature, closestTileMapForCreatures), currentDay, traitNameAndValueToCreatureStatModifiers, traitLoader);
		}
	}

	/**
	 * For a given creature, return a list of creatures within the max view distance range
	 *
	 * @param creature
	 * @param closestTileMapForCreatures
	 * @return
	 */
	private List<Creature> getCreaturesInRange(Creature creature, Map<Long, List<Creature>> closestTileMapForCreatures){
		List<Creature> creaturesInRange = new ArrayList<>();
		int creatureX = (int) Math.round(creature.getLocation().getX());
		int creatureY = (int) Math.round(creature.getLocation().getY());

		for(int i = creatureX - maxCreatureViewDistance; i < creatureX + maxCreatureViewDistance; i++){
			for(int j = creatureY - maxCreatureViewDistance; j < creatureY + maxCreatureViewDistance; j++){
				long locationLong = getLocationLongFromCoordinates(i, j);
				if(closestTileMapForCreatures.containsKey(locationLong)) {
					creaturesInRange.addAll(closestTileMapForCreatures.get(locationLong));
				}
			}
		}

		return creaturesInRange;
	}

	/**
	 * Given an x and y coordinate, return a long with the two coordinates appended together in a unique key
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	private long getLocationLongFromCoordinates(double x, double y){
		long xLong = (int) x;
		long yLong = (int) y;

		long locationLong = (xLong << 32) | yLong;
		return locationLong;
	}

	//TODO update this to grab the nearest creature and pass that to checkMatingForCreatureAndCreatureInRange

	/**
	 * For a given creature, iterate through all creatures in range and look for a potential mate
	 *
	 * @param creature
	 * @param creaturesInRange
	 * @param currentDay
	 * @param traitNameAndValueToCreatureStatModifiers
	 * @param traitLoader
	 */
	private void checkForCreatureMating(Creature creature, List<Creature> creaturesInRange, double currentDay, Map<String, List<CreatureStatModifier>> traitNameAndValueToCreatureStatModifiers, TraitLoader traitLoader){
		if(creature.getSexOfCreature() == Sex.ASEXUAL && creature.canMate(currentDay)){
			DNAString childString = DNABuilder.getAsexualDNAString(creature.getCreatureDNAString(), traitLoader);
			Creature newCreature = new Creature(creature.getLocation().getX(), creature.getLocation().getY(), childString, Math.random() > .5 ? MALE : FEMALE, traitNameAndValueToCreatureStatModifiers, currentDay);
			childCreaturesToAddToWorld.add(newCreature);
			creature.reduceEnergyFromMating();
			creature.setLastMatingDay(currentDay);
		} else {
			for (Creature creatureInRange : creaturesInRange) {
				checkMatingForCreatureAndCreatureInRange(creature, creatureInRange, currentDay);
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
			if(potentialPrey != null && creature != potentialPrey){
				creature.setCreatureState(CreatureState.EATING);
				potentialPrey.setCreatureState(CreatureState.FLEEING);
				carnivorePairs.add(new FeedingTargetCreature(creature, potentialPrey));
				break;
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
		if(creature.getCreatureState() == CreatureState.WANDERING && creatureInRange.getCreatureState() == CreatureState.WANDERING
			&& creature.canMate(currentDay) && creatureInRange.canMate(currentDay)){
			if(	(creature.getSexOfCreature() == MALE && creatureInRange.getSexOfCreature() == FEMALE) ||
				(creature.getSexOfCreature() == FEMALE && creatureInRange.getSexOfCreature() == MALE)){
					matingPairs.add(new MatingPair(creature, creatureInRange));
					creature.setCreatureState(CreatureState.MATING);
					creatureInRange.setCreatureState(CreatureState.MATING);
			}
		}
	}

	/**
	 * For all creatures, try to set a new wandering direction
	 *
	 * @param currentTime
	 * @param creatures
	 */
	public void setWanderDirectionForCreatures(double currentTime, List<Creature> creatures){//TODO change this to use game time (world day) which is modified by deltaUpdate
		for(int i = 0; i < creatures.size(); i++){
			Creature creature = creatures.get(i);
			creature.setWanderDirection(currentTime);
		}
	}

	/**
	 * For all mating pairs, try mating. If the creatures are too far apart, move closer.
	 *
	 * @param traitNameAndValueToCreatureStatModifiers
	 * @param currentDay
	 * @param deltaUpdate
	 * @param minWorldLocation
	 * @param maxWorldLocation
	 * @param traitLoader
	 */
	public void moveAndTryMatingCreatures(Map<String, List<CreatureStatModifier>> traitNameAndValueToCreatureStatModifiers, double currentDay, double deltaUpdate, Location minWorldLocation, Location maxWorldLocation, TraitLoader traitLoader){
		List<MatingPair> matingPairsToRemove = new LinkedList<>();
		for(MatingPair matingPair : matingPairs){
			Creature maleCreature = matingPair.getCreature1().getSexOfCreature() == MALE ? matingPair.getCreature1() : matingPair.getCreature2();
			Creature femaleCreature = matingPair.getCreature1().getSexOfCreature() == FEMALE ? matingPair.getCreature1() : matingPair.getCreature2();
			double distanceBetweenCreatures = distanceBetweenCreatures(maleCreature, femaleCreature);

			if(distanceBetweenCreatures > maxCreatureViewDistance){
				maleCreature.setCreatureState(CreatureState.WANDERING);
				femaleCreature.setCreatureState(CreatureState.WANDERING);
				matingPairsToRemove.add(matingPair);
			} else if(distanceBetweenCreatures > maleCreature.getSize().getWidth() + femaleCreature.getSize().getWidth()){
				maleCreature.moveCloserToPoint(deltaUpdate, femaleCreature.getLocation().getX(), femaleCreature.getLocation().getY(), minWorldLocation, maxWorldLocation);
			} else {
				DNAString childString = DNABuilder.getChildDNAString(maleCreature.getCreatureDNAString(), femaleCreature.getCreatureDNAString(), traitLoader);
				Creature newCreature = new Creature(femaleCreature.getLocation().getX(), femaleCreature.getLocation().getY(), childString, Math.random() > .5 ? MALE : FEMALE, traitNameAndValueToCreatureStatModifiers, currentDay);
				childCreaturesToAddToWorld.add(newCreature);

				maleCreature.reduceEnergyFromMating();
				femaleCreature.reduceEnergyFromMating();
				maleCreature.setLastMatingDay(currentDay);
				femaleCreature.setLastMatingDay(currentDay);
				maleCreature.setCreatureState(CreatureState.WANDERING);
				femaleCreature.setCreatureState(CreatureState.WANDERING);
				matingPairsToRemove.add(matingPair);
			}
		}

		matingPairs.removeAll(matingPairsToRemove);
	}

	/**
	 * For all herbivore and plant pairs, try eating. If too far, move closer.
	 *
	 * @param deltaUpdate
	 * @param minWorldLocation
	 * @param maxWorldLocation
	 */
	public void moveAndTryEatingForHerbivores(double deltaUpdate, Location minWorldLocation, Location maxWorldLocation){
		List<FeedingTargetPlant> herbivoreFeedingPairsToRemove = new LinkedList<>();
		List<Plant> plantsToRemove = new ArrayList<>();
		for(FeedingTargetPlant feedingTargetPlant : herbivorePairs){
			Creature creature = feedingTargetPlant.getCreature();
			Plant plant = feedingTargetPlant.getPlant();

			double distanceFromPlant = distanceBetweenCreatureAndPlant(creature, plant);

			if(distanceFromPlant > maxCreatureViewDistance){
				creature.setCreatureState(CreatureState.WANDERING);
				herbivoreFeedingPairsToRemove.add(feedingTargetPlant);
			} else if (distanceFromPlant > creature.getSize().getWidth() + plant.getSize().getWidth()){
				creature.moveCloserToPoint(deltaUpdate, plant.getLocation().getX(), plant.getLocation().getY(), minWorldLocation, maxWorldLocation);
			} else {
				creature.addEnergy(plant.getPlantType().getEnergyRestoration());
				plantsToRemove.add(plant);
				creature.setCreatureState(CreatureState.WANDERING);
				herbivoreFeedingPairsToRemove.add(feedingTargetPlant);
			}
		}

		herbivorePairs.removeAll(herbivoreFeedingPairsToRemove);

		for(int i = 0; i < plantsToRemove.size(); i++){
			Plant plantToRemove = plantsToRemove.get(i);
			List<Plant> plantsForTileForCreature =plantToRemove.getTileForPlant().getPlants();
			plantsForTileForCreature.remove(plantToRemove);
		}
	}

	/**
	 * For all carnivore pairs, try to eat. If too far, move closer.
	 *
	 * @param deltaUpdate
	 * @param minWorldLocation
	 * @param maxWorldLocation
	 * @return
	 */
	public List<Creature> moveAndTryEatingForCarnivores(double deltaUpdate, Location minWorldLocation, Location maxWorldLocation){
		List<FeedingTargetCreature> carnivoreFeedingPairsToRemove = new LinkedList<>();
		List<Creature> creaturestoRemove = new ArrayList<>();
		for(FeedingTargetCreature feedingTargetCreature : carnivorePairs){
			Creature predator = feedingTargetCreature.getPredator();
			Creature prey = feedingTargetCreature.getPrey();

			double distanceBetweenCreatures = distanceBetweenCreatures(predator, prey);

			if(distanceBetweenCreatures > maxCreatureViewDistance){
				predator.setCreatureState(CreatureState.WANDERING);
				prey.setCreatureState(CreatureState.WANDERING);
				carnivoreFeedingPairsToRemove.add(feedingTargetCreature);
			} else if (distanceBetweenCreatures > predator.getSize().getWidth() + prey.getSize().getWidth()){
				predator.moveCloserToPoint(deltaUpdate, prey.getLocation().getX(), prey.getLocation().getY(), minWorldLocation, maxWorldLocation);
				prey.moveAwayFromPoint(deltaUpdate, predator.getLocation().getX(), predator.getLocation().getY(), minWorldLocation, maxWorldLocation);
			} else {
				predator.addEnergy(prey.getEnergyRestoration());
				creaturestoRemove.add(prey);
				predator.setCreatureState(CreatureState.WANDERING);
				carnivoreFeedingPairsToRemove.add(feedingTargetCreature);
			}
		}

		carnivorePairs.removeAll(carnivoreFeedingPairsToRemove);
		return creaturestoRemove;
	}

	/**
	 * Adds a list of creatures to the queue childCreaturesToAddToWorld
	 *
	 * @param creatures
	 * @param worldStatisticsTool
	 */
	public void addNewChildCreaturesToWorldCreatureList(List<Creature> creatures, WorldStatisticsTool worldStatisticsTool){
		worldStatisticsTool.addTraitsForNewCreatures(childCreaturesToAddToWorld);//TODO STAT determine if this is in the correct location
		creatures.addAll(childCreaturesToAddToWorld);
		childCreaturesToAddToWorld.clear();
	}

	/**
	 * Determines the distance between two creatures
	 *
	 * @param creature1
	 * @param creature2
	 * @return
	 */
	private double distanceBetweenCreatures(Creature creature1, Creature creature2){
		return Math.sqrt(Math.pow(creature2.getLocation().getX() - creature1.getLocation().getX(), 2) + Math.pow(creature2.getLocation().getY() - creature1.getLocation().getY(), 2));
	}

	/**
	 * Determines the distance between a given creature and plant
	 *
	 * @param creature
	 * @param plant
	 * @return
	 */
	private double distanceBetweenCreatureAndPlant(Creature creature, Plant plant){
		return Math.sqrt(Math.pow(plant.getLocation().getX() - creature.getLocation().getX(), 2) + Math.pow(plant.getLocation().getY() - creature.getLocation().getY(), 2));
	}

}
