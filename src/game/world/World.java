package game.world;

import game.dna.DNABuilder;
import game.dna.DNAString;
import game.dna.species.Species;
import game.dna.stats.Sex;
import game.tiles.Tile;
import game.tiles.TileType;
import game.world.creatures.Creature;
import game.world.movement.CreatureManager;
import game.world.movement.submanagers.synchronizedchecks.CreatureActionProcessor;
import game.world.plantlife.Plant;
import game.world.plantlife.PlantType;
import game.world.units.Location;
import game.world.units.Size;
import ui.TraitLoader;
import ui.WorldStatisticsTool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static game.dna.stats.Sex.ASEXUAL;
import static game.dna.stats.Sex.FEMALE;
import static game.dna.stats.Sex.MALE;

public class World {

	private Tile[][] tileMap;//TODO move this to a worldMap class with methods for retrieving tiles by coordinates

	private TraitLoader traitLoader;
	private WorldStatisticsTool worldStatisticsTool;
	private CreatureManager movementManager;

	private double dayLength = 10000;
	private double worldDay;
	private short maxPlantsPerTile = 5;
	private Location minWorldLocation;

	private Location maxWorldLocation;
	private Size worldSize;

	Map<Long, CreatureActionProcessor> creatureActionProcessorMap;

	public World(int minWidth, int maxWidth, int minHeight, int maxHeight) {
		minWorldLocation = new Location(minWidth, minHeight);
		maxWorldLocation = new Location(maxWidth, maxHeight);
		worldSize = new Size(maxWidth - minWidth, maxHeight - minHeight);

		traitLoader = new TraitLoader();
		worldStatisticsTool = new WorldStatisticsTool();
		movementManager = new CreatureManager();

		creatureActionProcessorMap = new HashMap<>();
		tileMap = new Tile[(int) worldSize.getWidth()][(int) worldSize.getHeight()];//TODO may be more beneficial to create it with height first
		for (int y = 0; y < getTileMap().length; y++) {
			for (int x = 0; x < getTileMap()[0].length; x++) {
				Tile newTile = new Tile(x + minWidth, y + minHeight, TileType.DIRT, worldDay);
				tileMap[y][x] = newTile;

				CreatureActionProcessor creatureActionProcessor = new CreatureActionProcessor();
				creatureActionProcessorMap.put(CreatureManager.getLocationLongFromCoordinates(x + minWidth, y + minHeight), creatureActionProcessor);
			}
		}
	}

	/**
	 * Given coordinates, returns the corresponding tile, if it exists
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	public Tile getTileFromCoordinates(int x, int y) {
		try {
			if (coordinateExistsOnMap(x, y)) {
				int adjustedX = x - (int) minWorldLocation.getX();
				int adjustedY = y - (int) minWorldLocation.getY();

				return tileMap[adjustedY][adjustedX];
			}
		} catch (Exception e) {
			System.out.println(e.getStackTrace().toString());
		}

		return null;
	}

	/**
	 * Determines whether or not the given coordinates exist on the map (if the coordinates are not out of bounds of the array)
	 *
	 * @param x
	 * @param y
	 * @return
	 */
	private boolean coordinateExistsOnMap(int x, int y) {
		return x >= minWorldLocation.getX() && x < maxWorldLocation.getX() && y >= minWorldLocation.getY() && y < maxWorldLocation.getY();
	}

	/**
	 * Processes all updates for the world and anything within the world. LET'S PLAY GOD. MUAHAHAHAHAHAHA
	 *
	 * @param deltaUpdate
	 */
	public void runWorldUpdates(double deltaUpdate) {
		movementManager.runMovementManagerUpdates(worldDay, deltaUpdate, minWorldLocation, maxWorldLocation, this, worldStatisticsTool, traitLoader, creatureActionProcessorMap);

		adjustDay(deltaUpdate);

		List<Location> locationsToAddFertility = movementManager.clearCreaturesToDeleteAndGetTilesToAddFertility();
		for (int i = 0; i < locationsToAddFertility.size(); i++) {
			Location currentLocationToCheck = locationsToAddFertility.get(i);
			if (coordinateExistsOnMap((int) currentLocationToCheck.getX(), (int) currentLocationToCheck.getY())) {
				getTileFromCoordinates((int) currentLocationToCheck.getX(), (int) currentLocationToCheck.getY()).addFertilityFromDeath();
			}
		}

		addPlantsToTileMap(worldDay);
	}

	/**
	 * Given the current day, iterate through the tile map and attempt to grow plants on each tile
	 *
	 * @param currentDay
	 */
	private void addPlantsToTileMap(double currentDay) {
		for (int i = 0; i < tileMap.length; i++) {
			for (int j = 0; j < tileMap[0].length; j++) {
				Tile currentTile = tileMap[i][j];
				if (currentTile.plantGrowthFrequencyAllowsCheckToGrow(currentDay) && currentTile.getPlants().size() < maxPlantsPerTile) {
					PlantType plantTypeToGrow = PlantType.GRASS;
					if (currentTile.canGrowPlant()) {
						Plant newPlant = new Plant(currentTile.getLocation().getX() + Math.random(), currentTile.getLocation().getY() + Math.random(), plantTypeToGrow, currentTile);
						currentTile.addPlant(newPlant);
						currentTile.removeFertility();
					}
				}
			}
		}
	}

	/**
	 * TODO remove this - temporary method for testing - or modify for range
	 * A manual method of iterating through all creatures and mating random pairs
	 */
	public void tryMatingCreatures() {//TODO remove this - temporary method for testing - or modify for range
		List<Creature> malesToMate = new ArrayList<>();
		List<Creature> femalesToMate = new ArrayList<>();
		List<Creature> asexualToMate = new ArrayList<>();

		for (int i = 0; i < getCreatures().size(); i++) {
			Creature creature = getCreatures().get(i);
			if (creature.getSexOfCreature().equals(MALE)) {
				malesToMate.add(creature);
			} else if (creature.getSexOfCreature().equals(FEMALE)) {
				femalesToMate.add(creature);
			} else if (creature.getSexOfCreature().equals(ASEXUAL)) {
				asexualToMate.add(creature);
			}
		}

		int numberOfMatings = malesToMate.size() <= femalesToMate.size() ? malesToMate.size() : femalesToMate.size();
		for (int i = 0; i < numberOfMatings; i++) {
			DNAString childString = DNABuilder.getChildDNAString(malesToMate.get(i).getCreatureDNAString(), femalesToMate.get(i).getCreatureDNAString(), traitLoader);
			Creature newCreature = new Creature(Math.random() * 20 - 10, Math.random() * 20 - 10, childString, Math.random() > .5 ? MALE : FEMALE, traitLoader.getTraitNameAndValueToCreatureStatModifiers(), worldDay, malesToMate.get(i).getSpecies());
			getCreatures().add(newCreature);
			worldStatisticsTool.addTraitsForNewCreatures(Collections.singletonList(newCreature));//TODO STAT determine if this is in the correct location
		}
	}

	/**
	 * Returns all creatures for the world
	 *
	 * @return
	 */
	public List<Creature> getCreatures() {
		return movementManager.getCreatures();
	}

	double cachedDeltaUpdate;
	double cachedDayChange;

	/**
	 * Given a delta update, adjust the day by the scaled amount
	 *
	 * @param deltaUpdate
	 */
	public void adjustDay(double deltaUpdate) {
		if (deltaUpdate != cachedDeltaUpdate) {
			cachedDeltaUpdate = deltaUpdate;
			cachedDayChange = cachedDeltaUpdate / dayLength;
		}
		worldDay += cachedDayChange;
	}

	/**
	 * Adds a random creature, of the given sex, to the world
	 *
	 * @param sexOfCreature
	 */
	public void addRandomCreature(Sex sexOfCreature, double x, double y, Species species) {
		if (coordinateExistsOnMap((int) x, (int) y)) {
			movementManager.addRandomCreature(sexOfCreature, x, y, traitLoader, worldDay, worldStatisticsTool, species);
		}
	}

	/**
	 * Returns the current day of the world
	 *
	 * @return
	 */
	public double getWorldDay() {
		return worldDay;
	}

	public WorldStatisticsTool getWorldStatisticsTool() {
		return worldStatisticsTool;
	}

	public Location getMinWorldLocation() {
		return minWorldLocation;
	}

	public Location getMaxWorldLocation() {
		return maxWorldLocation;
	}

	public Tile[][] getTileMap() {
		return tileMap;
	}
}
