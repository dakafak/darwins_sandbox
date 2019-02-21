package game.world.movement.movement_pairs;

import game.world.creatures.Creature;

/**
 * A predator and prey pair, used to determine whether a predator is currently hunting
 */
public class FeedingTargetCreature {

	Creature predator;
	Creature prey;

	public FeedingTargetCreature(Creature predator, Creature prey) {
		this.predator = predator;
		this.prey = prey;
	}

	public Creature getPredator() {
		return predator;
	}

	public void setPredator(Creature predator) {
		this.predator = predator;
	}

	public Creature getPrey() {
		return prey;
	}

	public void setPrey(Creature prey) {
		this.prey = prey;
	}
}
