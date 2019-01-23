package ui;

import game.World;
import game.world.creatures.Sex;

import java.util.ArrayList;
import java.util.List;

public class SimulatorRunner {

	List<StatisticsSave> allSaves;

	public static void main(String[] args){
		SimulatorRunner runner = new SimulatorRunner();
	}

	public SimulatorRunner(){
		allSaves = new ArrayList<>();

		World newWorld = new World(-50, 50, -50, 50);
		newWorld.addRandomCreature(Sex.MALE);
		newWorld.addRandomCreature(Sex.FEMALE);

		System.out.println(newWorld.getCreatures());

		SimulatorWindow simulatorWindow = new SimulatorWindow("World Simulator", newWorld);
	}

}
