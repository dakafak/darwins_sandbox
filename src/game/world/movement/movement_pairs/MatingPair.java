package game.world.movement.movement_pairs;

import game.world.creatures.Creature;

/**
 * A pair of two creatures, which are going to mate once they reach each other
 */
public class MatingPair {

	Creature creature1;
	Creature creature2;

	public MatingPair(Creature creature1, Creature creature2){
		this.creature1 = creature1;
		this.creature2 = creature2;
	}

	public Creature getCreature1() {
		return creature1;
	}

	public void setCreature1(Creature creature1) {
		this.creature1 = creature1;
	}

	public Creature getCreature2() {
		return creature2;
	}

	public void setCreature2(Creature creature2) {
		this.creature2 = creature2;
	}
}
