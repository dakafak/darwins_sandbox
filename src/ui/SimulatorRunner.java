package ui;

import java.util.ArrayList;
import java.util.List;

public class SimulatorRunner {

	List<StatisticsSave> allSaves;

	public static void main(String[] args) {
		SimulatorRunner runner = new SimulatorRunner();
	}

	public SimulatorRunner() {
		allSaves = new ArrayList<>();
		SimulatorWindow simulatorWindow = new SimulatorWindow("World Simulator");
	}

}
