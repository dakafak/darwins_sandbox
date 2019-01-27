package game.world.movement;

import game.world.creatures.Creature;

public class FeedingTargetCreature {

	Creature predator;
	Creature prey;

	public FeedingTargetCreature(Creature predator, Creature prey){
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
