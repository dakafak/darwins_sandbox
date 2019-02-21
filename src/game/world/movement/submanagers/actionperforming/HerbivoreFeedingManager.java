package game.world.movement.submanagers.actionperforming;

import game.world.creatures.Creature;
import game.world.creatures.CreatureState;
import game.world.movement.movement_pairs.FeedingTargetPlant;
import game.world.plantlife.Plant;
import game.world.units.Location;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class HerbivoreFeedingManager {

	List<Creature> creaturesDoneEating;
	private List<FeedingTargetPlant> herbivorePairs;

	/**
	 * TODO idea for multithreading
	 * movement in creatures should be synchronized
	 * should have a need-to-be-processed queue and a processed queue
	 * at the start, processed get added to needtoprocess queue as this iterates, all are added to processed
	 * <p>
	 * call the thread at the start of checkWorldUpdates(...)
	 * <p>
	 * <p>
	 * or multithread CreatureManager by having a sublist for each creature manager
	 * build the map of creature to creatures
	 * <p>
	 * multiple threads should process "actions" to take place, then main loop performs actions to avoid concurrency
	 */

	public HerbivoreFeedingManager() {
		creaturesDoneEating = new LinkedList<>();
		herbivorePairs = new LinkedList<>();
	}

	synchronized public void addAllHerbivoreFeedingPairs(List<FeedingTargetPlant> feedingTargetPlants) {
		herbivorePairs.addAll(feedingTargetPlants);
	}

	synchronized public void addHerbivoreFeedingPair(FeedingTargetPlant herbivoreFeedingPair) {
		herbivorePairs.add(herbivoreFeedingPair);
	}

	/**
	 * For all herbivore and plant pairs, try eating. If too far, move closer.
	 *
	 * @param deltaUpdate
	 * @param minWorldLocation
	 * @param maxWorldLocation
	 */
	public void moveAndTryEatingForHerbivores(double deltaUpdate, Location minWorldLocation, Location maxWorldLocation, double maxCreatureViewDistance) {
		List<FeedingTargetPlant> herbivoreFeedingPairsToRemove = new LinkedList<>();
		List<Plant> plantsToRemove = new ArrayList<>();
		for (FeedingTargetPlant feedingTargetPlant : herbivorePairs) {
			Creature creature = feedingTargetPlant.getCreature();
			Plant plant = feedingTargetPlant.getPlant();

			double distanceFromPlant = distanceBetweenCreatureAndPlant(creature, plant);

			if (distanceFromPlant > maxCreatureViewDistance) {
				creature.setCreatureState(CreatureState.WANDERING);
				herbivoreFeedingPairsToRemove.add(feedingTargetPlant);
			} else if (distanceFromPlant > creature.getSize().getWidth() + plant.getSize().getWidth()) {
				creature.moveCloserToPoint(deltaUpdate, plant.getLocation().getX(), plant.getLocation().getY(), minWorldLocation, maxWorldLocation);
			} else {
				creature.addEnergy(plant.getPlantType().getEnergyRestoration());
				plantsToRemove.add(plant);
				creature.setCreatureState(CreatureState.WANDERING);
				herbivoreFeedingPairsToRemove.add(feedingTargetPlant);
			}
		}

		herbivorePairs.removeAll(herbivoreFeedingPairsToRemove);

		for (int i = 0; i < plantsToRemove.size(); i++) {
			Plant plantToRemove = plantsToRemove.get(i);
			List<Plant> plantsForTileForCreature = plantToRemove.getTileForPlant().getPlants();
			plantsForTileForCreature.remove(plantToRemove);
		}
	}

	synchronized public void clearDisposableLists() {
		creaturesDoneEating.clear();
	}

	/**
	 * Determines the distance between a given creature and plant
	 *
	 * @param creature
	 * @param plant
	 * @return
	 */
	private double distanceBetweenCreatureAndPlant(Creature creature, Plant plant) {
		return Math.sqrt(Math.pow(plant.getLocation().getX() - creature.getLocation().getX(), 2) + Math.pow(plant.getLocation().getY() - creature.getLocation().getY(), 2));
	}

	public List<Creature> getCreaturesDoneEating() {
		return creaturesDoneEating;
	}

	synchronized public void clearCreaturesDoneEating() {

	}
}
