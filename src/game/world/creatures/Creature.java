package game.world.creatures;

import game.dna.DNAString;
import game.dna.stats.StatType;
import game.dna.traits.CreatureStatModifier;
import game.dna.traits.TraitNameAndValuePair;
import game.dna.traits.TraitPair;
import game.world.units.Direction;
import game.world.units.Location;
import game.world.units.Size;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static game.world.units.Direction.MOVING_EAST;
import static game.world.units.Direction.MOVING_NORTH;
import static game.world.units.Direction.MOVING_SOUTH;
import static game.world.units.Direction.MOVING_WEST;
import static game.world.units.Direction.NORTH;

public class Creature {

    DNAString creatureDNAString;
    Map<StatType, Object> creatureStats;//TODO should have a map for trait to value -- or hashset with enums that contain the value
    Sex sexOfCreature;//TODO maybe should be a trait instead, could by x and Y
	CreatureState creatureState;
	Location location;
	Size size;//TODO  some of these traits can be moved into creature stats once that loader is added
	Direction direction = NORTH;
	double speed;
	double lifeSpan;
	double mating_frequency;
	double lastMatingDay;
	double daySpawned;

    public Creature(double x, double y, DNAString creatureDNAString, Sex sexOfCreature, Map<String, List<CreatureStatModifier>> traitNameAndValueToCreatureStatModifiers, double currentDay){
    	this.creatureDNAString = creatureDNAString;
    	this.sexOfCreature = sexOfCreature;
		this.location = new Location(x, y);
		this.daySpawned = currentDay;
		creatureState = CreatureState.WANDERING;

		creatureStats = new HashMap<>();

		for(TraitPair traitPair : creatureDNAString.getTraitString()){

			TraitNameAndValuePair traitNameAndValuePair = new TraitNameAndValuePair(traitPair.getTraits()[0]);//Get displayed trait for creature
			if(traitNameAndValueToCreatureStatModifiers.containsKey(traitNameAndValuePair.getKey())) {
				for (CreatureStatModifier statModifierForTrait : traitNameAndValueToCreatureStatModifiers.get(traitNameAndValuePair.getKey())) {

					Map<StatType, Object> statModifiers = statModifierForTrait.getStatModifiers();
					for(StatType statType : statModifiers.keySet()){
						if(creatureStats.containsKey(statType)){
							if(statType.getClassType().equals(Double.class)){
								Double newValue = (Double)creatureStats.get(statType) + (Double)statModifiers.get(statType);
								creatureStats.put(statType, newValue);
							} else {
								creatureStats.put(statType, statModifiers.get(statType));
							}
						} else {
							creatureStats.put(statType, statModifiers.get(statType));
						}
					}
				}
			}
		}

		speed = .01;
		speed = (Double) creatureStats.get(StatType.speed);
		double sizeFromStats = (Double) creatureStats.get(StatType.size);
		size = new Size(sizeFromStats, sizeFromStats);
		lifeSpan = (Double) creatureStats.get(StatType.life_span);
		mating_frequency = (Double) creatureStats.get(StatType.mating_frequency);
		lastMatingDay = currentDay;
    }

    public boolean canMate(double currentDay){
    	if((currentDay - lastMatingDay) > mating_frequency){
    		return true;
		}

		return false;
	}

    public DNAString getCreatureDNAString() {
        return creatureDNAString;
    }

    public Sex getSexOfCreature() {
        return sexOfCreature;
    }

	public Location getLocation() {
		return location;
	}

	public CreatureState getCreatureState() {
		return creatureState;
	}

	public void setCreatureState(CreatureState creatureState) {
		this.creatureState = creatureState;
	}

	public Size getSize() {
		return size;
	}

	public Map<StatType, Object> getCreatureStats() {
		return creatureStats;
	}

	public void setCreatureStats(Map<StatType, Object> creatureStats) {
		this.creatureStats = creatureStats;
	}

	public double getLastMatingDay() {
		return lastMatingDay;
	}

	public void setLastMatingDay(double currentDay) {
		this.lastMatingDay = currentDay;
	}

	@Override
    public String toString(){
        return sexOfCreature.getSexString().substring(0, 1) + "{" + location.getX() + ", " + location.getY() + "}" + "(" + creatureDNAString.toString() + ")";
    }

    long nextDirectionChange;
    int wanderTimeInMilliseconds = 1000;
    int wanderTimeAdditionInMilliseconds = 4000;
	public void setWanderDirection(long currentTime) {
		if(currentTime > nextDirectionChange){
			setRandomDirection();
			nextDirectionChange = currentTime + wanderTimeInMilliseconds + (int)(Math.random()*wanderTimeAdditionInMilliseconds);
		}
	}

	private void setRandomDirection(){
		Direction[] allDirections = Direction.values();
		int randomDirection = (int)Math.floor(Math.random()*allDirections.length);
		direction = allDirections[randomDirection];
	}

	public void wander(double deltaUpdate){
		double dx = 0;
		double dy = 0;

		if(direction == MOVING_NORTH){
			dy = -1;
		} else if(direction == MOVING_EAST){
			dx = 1;
		} else if(direction == MOVING_SOUTH){
			dy = 1;
		} else if(direction == MOVING_WEST){
			dx = -1;
		}

		location.setY(location.getY() + (dy * speed * deltaUpdate));
		location.setX(location.getX() + (dx * speed * deltaUpdate));
	}

	public boolean endedLifeSpan(double day){
		if(day - daySpawned > lifeSpan){
			return true;
		}

		return false;
	}

	public void moveCloserToPoint(double deltaUpdate, double x, double y) {
		double dx = 0;
		double dy = 0;

		double distanceY = y - location.getY();
		double distanceX = x - location.getX();
		if(Math.abs(distanceY) > Math.abs(distanceX)){
			if(distanceY > 0){
				dy = 1;
			} else if(distanceY < 0){
				dy = -1;
			}
		} else {
			if(distanceX > 0){
				dx = 1;
			} else if(distanceX < 0){
				dx = -1;
			}
		}

		location.setX(location.getX() + (dx * speed * deltaUpdate));
		location.setY(location.getY() + (dy * speed * deltaUpdate));
	}
}
