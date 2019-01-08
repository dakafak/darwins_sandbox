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
    Map<StatType, String> creatureStats;//TODO should have a map for trait to value -- or hashset with enums that contain the value
    Sex sexOfCreature;//TODO maybe should be a trait instead, could by x and Y
	Location location;
	Size size;//TODO  some of these traits can be moved into creature stats once that loader is added
	Direction direction = NORTH;
	double speed;
//	Map<TraitType, String> traitTypeToTraitValueMap;

    public Creature(double x, double y, DNAString creatureDNAString, Sex sexOfCreature, Map<String, List<CreatureStatModifier>> traitNameAndValueToCreatureStatModifiers){
    	this.creatureDNAString = creatureDNAString;
    	this.sexOfCreature = sexOfCreature;
		this.location = new Location(x, y);

		creatureStats = new HashMap<>();

		for(TraitPair traitPair : creatureDNAString.getTraitString()){
			TraitNameAndValuePair traitNameAndValuePair = new TraitNameAndValuePair(traitPair.getTraits()[0]);//Get displayed trait for creature
//			traitTypeToTraitValueMap.put(TraitType.valueOf(traitPair.getTraits()[0].getTraitType()), traitPair.getTraits()[0].getTraitDefinition());
			if(traitNameAndValueToCreatureStatModifiers.containsKey(traitNameAndValuePair.getKey())) {
				for (CreatureStatModifier statModifierForTrait : traitNameAndValueToCreatureStatModifiers.get(traitNameAndValuePair.getKey())) {
					Map<StatType, String> statModifiers = statModifierForTrait.getStatModifiers();
					for(StatType statType : statModifiers.keySet()){
						if(creatureStats.containsKey(statType)){
							creatureStats.put(statType, statModifiers.get(statType));
							//TODO should determine if the stat type is a numeric or string value. If the value is numeric, it should add to the value instead of replacing it
							//TODO 			or determine if the class matches the stat type class, for example Size.class, Integer.class, etc.
						} else {
							creatureStats.put(statType, statModifiers.get(statType));
						}
					}
				}
			}
		}


//		if(traitTypeToTraitMap.get(TraitType.speed).getTraitDefinition().equals("slow")){
//			speed = .005;
//		} else if(traitTypeToTraitMap.get(TraitType.speed).getTraitDefinition().equals("medium")){
//			speed = .01;
//		} else if(traitTypeToTraitMap.get(TraitType.speed).getTraitDefinition().equals("fast")){
//			speed = .02;
//		}
//
//		if(traitTypeToTraitMap.get(TraitType.size).getTraitDefinition().equals("small")){
//			size = new Size(.4, .4);
//		} else if(traitTypeToTraitMap.get(TraitType.size).getTraitDefinition().equals("medium")){
//			size = new Size(.5, .5);
//		} else if(traitTypeToTraitMap.get(TraitType.size).getTraitDefinition().equals("large")){
//			size = new Size(.6, .6);
//		} else if(traitTypeToTraitMap.get(TraitType.size).getTraitDefinition().equals("huge")){
//			size = new Size(.7, .7);
//		}
		//TODO once creature stats are created, use creature stats to determine these stat values
		speed = .01;
		size = new Size(.5, .5);
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
