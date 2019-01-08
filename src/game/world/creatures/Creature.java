package game.world.creatures;

import game.dna.DNAString;
import game.dna.stats.StatType;
import game.dna.traits.*;
import game.world.units.Direction;
import game.world.units.Location;
import game.world.units.Size;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static game.world.units.Direction.*;
import static game.world.units.Direction.MOVING_SOUTH;

public class Creature {

    DNAString creatureDNAString;
    Map<StatType, Object> creatureStats;//TODO should have a map for trait to value -- or hashset with enums that contain the value
    Sex sexOfCreature;//TODO maybe should be a trait instead, could by x and Y
	Location location;
	Size size;//TODO  some of these traits can be moved into creature stats once that loader is added
	Direction direction = NORTH;
	double speed;

    public Creature(double x, double y, DNAString creatureDNAString, Sex sexOfCreature, Map<String, List<CreatureStatModifier>> traitNameAndValueToCreatureStatModifiers){
    	this.creatureDNAString = creatureDNAString;
    	this.sexOfCreature = sexOfCreature;
		this.location = new Location(x, y);

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

	public Size getSize() {
		return size;
	}

	@Override
    public String toString(){
        return sexOfCreature.getSexString().substring(0, 1) + "{" + location.getX() + ", " + location.getY() + "}" + "(" + creatureDNAString.toString() + ")";
    }

    long nextDirectionChange;
    int wanderTimeInMilliseconds = 1000;
    int wanderTimeAdditionInMilliseconds = 4000;
	public void wander(long currentTime) {
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

	public void move(double deltaUpdate){
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

}
