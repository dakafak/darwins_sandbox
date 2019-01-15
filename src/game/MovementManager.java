package game;

import game.dna.DNABuilder;
import game.dna.DNAString;
import game.dna.traits.CreatureStatModifier;
import game.world.creatures.Creature;
import game.world.creatures.CreatureState;
import game.world.creatures.MatingPair;
import game.world.creatures.Sex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static game.world.creatures.Sex.FEMALE;
import static game.world.creatures.Sex.MALE;

public class MovementManager {

	List<MatingPair> matingPairs;
	List<Creature> childCreaturesToAddToWorld;

	int creatureViewDistance = 5;

	public MovementManager(){
		matingPairs = new ArrayList<>();
		childCreaturesToAddToWorld = new LinkedList<>();
	}

	public void moveCreatures(double deltaUpdate, List<Creature> creatures, double currentDay, Map<String, List<CreatureStatModifier>> traitNameAndValueToCreatureStatModifiers){
		Map<String, List<Creature>> closestTileMapForCreatures = new HashMap<>();
		for(int i = 0; i < creatures.size(); i++){
			Creature creature = creatures.get(i);
			String locationString = getLocationStringFromCoordinates(creature.getLocation().getX(), creature.getLocation().getY());
			if(closestTileMapForCreatures.containsKey(locationString) && closestTileMapForCreatures.get(locationString) != null){
				List<Creature> creaturesAtLocation = closestTileMapForCreatures.get(locationString);
				creaturesAtLocation.add(creature);
			} else {
				List<Creature> newCreatureListForLocation = new ArrayList<>();
				newCreatureListForLocation.add(creature);
				closestTileMapForCreatures.put(locationString, newCreatureListForLocation);
			}
		}

		for(int i = 0; i < creatures.size(); i++){
			Creature creature = creatures.get(i);
			if(creature.getCreatureState() == CreatureState.WANDERING) {
				creature.wander(deltaUpdate);
			}
		}

		for(int i = 0; i < creatures.size(); i++){
			Creature creature = creatures.get(i);
			checkForCreatureMating(creature, getCreaturesInRange(creature, creatureViewDistance, closestTileMapForCreatures), currentDay, traitNameAndValueToCreatureStatModifiers);
		}
	}

	private List<Creature> getCreaturesInRange(Creature creature, int range, Map<String, List<Creature>> closestTileMapForCreatures){
		List<Creature> creaturesInRange = new ArrayList<>();
		int creatureX = (int) Math.round(creature.getLocation().getX());
		int creatureY = (int) Math.round(creature.getLocation().getY());

		for(int i = creatureX - range; i < creatureX + range; i++){
			for(int j = creatureY - range; j < creatureY + range; j++){
				String currentLocationString = getLocationStringFromCoordinates(i, j);
				if(closestTileMapForCreatures.containsKey(currentLocationString)) {
					creaturesInRange.addAll(closestTileMapForCreatures.get(currentLocationString));
				}
			}
		}

		return creaturesInRange;
	}

	private String getLocationStringFromCoordinates(double x, double y){
		return String.format("%04d", (int) Math.round(x)) + String.format("%04d", (int) Math.round(y));
	}

	private void checkForCreatureMating(Creature creature, List<Creature> creaturesInRange, double currentDay, Map<String, List<CreatureStatModifier>> traitNameAndValueToCreatureStatModifiers){
		if(creature.getSexOfCreature() == Sex.ASEXUAL && creature.canMate(currentDay)){
			DNAString childString = DNABuilder.getAsexualDNAString(creature.getCreatureDNAString());
			Creature newCreature = new Creature(creature.getLocation().getX(), creature.getLocation().getY(), childString, Math.random() > .5 ? MALE : FEMALE, traitNameAndValueToCreatureStatModifiers, currentDay);
			childCreaturesToAddToWorld.add(newCreature);

			creature.setLastMatingDay(currentDay);
		} else {
			for (Creature creatureInRange : creaturesInRange) {
				checkMatingForCreatureAndCreatureInRange(creature, creatureInRange, currentDay);
			}
		}
	}

	private void checkMatingForCreatureAndCreatureInRange(Creature creature, Creature creatureInRange, double currentDay){
		//TODO loop through and choose creatures to move towards each other and mate
		if(creature.getCreatureState() == CreatureState.WANDERING && creatureInRange.getCreatureState() == CreatureState.WANDERING
			&& creature.canMate(currentDay) && creatureInRange.canMate(currentDay)){
			if(	(creature.getSexOfCreature() == MALE && creatureInRange.getSexOfCreature() == FEMALE) ||
				(creature.getSexOfCreature() == FEMALE && creatureInRange.getSexOfCreature() == MALE)){
					matingPairs.add(new MatingPair(creature, creatureInRange));
					creature.setCreatureState(CreatureState.MATING);
					creatureInRange.setCreatureState(CreatureState.MATING);
			}
		}
	}

	public void tellCreaturesToWander(long currentTime, List<Creature> creatures){
		for(int i = 0; i < creatures.size(); i++){
			Creature creature = creatures.get(i);
			creature.setWanderDirection(currentTime);
		}
	}

	public void moveAndTryMatingCreatures(Map<String, List<CreatureStatModifier>> traitNameAndValueToCreatureStatModifiers, double currentDay, double deltaUpdate){
		List<MatingPair> matingPairsToRemove = new LinkedList<>();
		for(MatingPair matingPair : matingPairs){
			Creature maleCreature = matingPair.getCreature1().getSexOfCreature() == MALE ? matingPair.getCreature1() : matingPair.getCreature2();
			Creature femaleCreature = matingPair.getCreature1().getSexOfCreature() == FEMALE ? matingPair.getCreature1() : matingPair.getCreature2();
			double distanceBetweenCreatures = distanceBetweenCreatures(maleCreature, femaleCreature);

			if(distanceBetweenCreatures > creatureViewDistance){
				maleCreature.setCreatureState(CreatureState.WANDERING);
				femaleCreature.setCreatureState(CreatureState.WANDERING);
				matingPairsToRemove.add(matingPair);
			} else if(distanceBetweenCreatures > maleCreature.getSize().getWidth() + femaleCreature.getSize().getWidth()){
				maleCreature.moveCloserToPoint(deltaUpdate, femaleCreature.getLocation().getX(), femaleCreature.getLocation().getY());
			} else {
				DNAString childString = DNABuilder.getChildDNAString(maleCreature.getCreatureDNAString(), femaleCreature.getCreatureDNAString());
				Creature newCreature = new Creature(femaleCreature.getLocation().getX(), femaleCreature.getLocation().getY(), childString, Math.random() > .5 ? MALE : FEMALE, traitNameAndValueToCreatureStatModifiers, currentDay);
				childCreaturesToAddToWorld.add(newCreature);

				maleCreature.setLastMatingDay(currentDay);
				femaleCreature.setLastMatingDay(currentDay);
				maleCreature.setCreatureState(CreatureState.WANDERING);
				femaleCreature.setCreatureState(CreatureState.WANDERING);
				matingPairsToRemove.add(matingPair);
			}
		}

		matingPairs.removeAll(matingPairsToRemove);
		matingPairsToRemove.clear();
	}

	public void addNewChildCreaturesToWorldCreatureList(List<Creature> creatures){
		creatures.addAll(childCreaturesToAddToWorld);
		childCreaturesToAddToWorld.clear();
	}

	private double distanceBetweenCreatures(Creature creature1, Creature creature2){
		return Math.sqrt(Math.pow(creature2.getLocation().getX() - creature1.getLocation().getX(), 2) + Math.pow(creature2.getLocation().getY() - creature1.getLocation().getY(), 2));
	}

}
