package game.world.movement.movement_pairs;

import game.world.creatures.Creature;
import game.world.plantlife.Plant;

/**
 * A creature and plant pair, used to determine if a creature is currently moving towards a plant to eat
 */
public class FeedingTargetPlant {

	Creature creature;
	Plant plant;

	public FeedingTargetPlant(Creature creature, Plant plant){
		this.creature = creature;
		this.plant = plant;
	}

	public Creature getCreature() {
		return creature;
	}

	public void setCreature(Creature creature) {
		this.creature = creature;
	}

	public Plant getPlant() {
		return plant;
	}

	public void setPlant(Plant plant) {
		this.plant = plant;
	}
}
