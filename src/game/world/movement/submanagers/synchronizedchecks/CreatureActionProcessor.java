package game.world.movement.submanagers.synchronizedchecks;

import game.dna.stats.Diet;
import game.world.World;
import game.world.creatures.Creature;
import game.world.creatures.CreatureState;
import game.world.movement.movement_pairs.FeedingTargetPlant;
import game.world.plantlife.Plant;

import java.util.List;
import java.util.Map;

public class CreatureActionProcessor implements Runnable{

	@Override
	public void run() {

	}

	/**
	 * For a list of herbivore creatures, check if any can eat and are nearby a plant, if so create a new FeedingTargetPlant
	 *
	 * @param creatures
	 * @param world
	 */
	public void checkForNewHerbivoreFeedingPairs(List<Creature> creatures, World world){
		for(int i = 0; i < creatures.size(); i++){
			Creature creature = creatures.get(i);
			if(creature.getDiet() == Diet.HERBIVORE && creature.isDoingNothing() && creature.isHungry()) {
				Plant nearestPlantToCreature = getNearestPlantToCreature(creature, world);
				if(nearestPlantToCreature != null) {
					creature.setCreatureState(CreatureState.EATING);
					herbivoreFeedingManager.addHerbivoreFeedingPair(new FeedingTargetPlant(creature, nearestPlantToCreature));
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
	public void checkForNewCarnivoreFeedingPairs(List<Creature> creatures, Map<Long, List<Creature>> closestTileMapForCreatures){
		for(int i = 0; i < creatures.size(); i++){
			Creature creature = creatures.get(i);
			if(creature.getDiet() == Diet.CARNIVORE && creature.isHungry()) {
				checkForCarnvioreFeedingPairsInRange(creature, getCreaturesInRange(creature, closestTileMapForCreatures));
			}
		}
	}
}
