package game.world.movement.submanagers.actionperforming;

import game.world.creatures.Creature;
import game.world.creatures.CreatureState;
import game.world.movement.movement_pairs.FeedingTargetCreature;
import game.world.movement.movement_pairs.FeedingTargetPlant;
import game.world.units.Location;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CarnivoreFeedingManager {

	private List<Creature> creaturesDoneEating;
	private List<FeedingTargetCreature> carnivorePairs;
	private List<Creature> eatenCreatures;

	public CarnivoreFeedingManager(){
		creaturesDoneEating = new LinkedList<>();
		carnivorePairs = new LinkedList<>();
		eatenCreatures = new LinkedList<>();
	}

	synchronized public void addCarnivoreFeedingPair(FeedingTargetCreature feedingTargetCreature){
		carnivorePairs.add(feedingTargetCreature);
	}

	synchronized  public void addAllCarnivoreFeedingPairs(List<FeedingTargetCreature> feedingTargetCreatures){
		carnivorePairs.addAll(feedingTargetCreatures);
	}

	/**
	 * For all carnivore pairs, try to eat. If too far, move closer.
	 *
	 * @param deltaUpdate
	 * @param minWorldLocation
	 * @param maxWorldLocation
	 * @return
	 */
	public void moveAndTryEatingForCarnivores(double deltaUpdate, Location minWorldLocation, Location maxWorldLocation, double maxCreatureViewDistance){
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
		eatenCreatures.addAll(creaturestoRemove);
	}

	synchronized public List<Creature> getEatenCreatures(){
		return eatenCreatures;
	}

	public List<Creature> getCreaturesDoneEating() {
		return creaturesDoneEating;
	}

	synchronized public void clearDisposableLists(){
		creaturesDoneEating.clear();
		eatenCreatures.clear();
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

}
