package game;

import game.creatures.Sex;

public class SimulatorRunner {

	public static void main(String[] args){
		World newWorld = new World(50, 50);
		newWorld.addRandomCreature(Sex.MALE);
		newWorld.addRandomCreature(Sex.FEMALE);

		System.out.println(newWorld.creatures);

		for(int i = 0; i < 20; i++){
			System.out.println("[" + i + "]");
			newWorld.tryMatingCreatures();
			newWorld.printWorldStatistics();
//			System.out.println(newWorld.creatures);
		}
	}

}
