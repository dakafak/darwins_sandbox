package game.world.creatures;

import game.dna.DNAString;
import game.dna.traits.Trait;
import game.dna.traits.TraitPair;
import game.dna.traits.TraitType;
import game.world.units.Direction;
import game.world.units.Location;
import game.world.units.Size;

import java.util.HashMap;
import java.util.Map;

import static game.world.units.Direction.*;
import static game.world.units.Direction.MOVING_SOUTH;

public class Creature {

    DNAString creatureDNAString;
    Map<Enum, Object> creatureStats;//TODO should have a map for trait to value -- or hashset with enums that contain the value
    Sex sexOfCreature;//TODO maybe should be a trait instead, could by x and Y
	Location location;
	Size size;//TODO  some of these traits can be moved into creature stats once that loader is added
	Direction direction = NORTH;
	double speed;
	Map<TraitType, Trait> traitTypeToTraitMap;

    public Creature(double x, double y, DNAString creatureDNAString, Sex sexOfCreature){
    	this.creatureDNAString = creatureDNAString;
    	this.sexOfCreature = sexOfCreature;
		this.location = new Location(x, y);

		//TODO setup trait hashmap
		traitTypeToTraitMap = new HashMap<>();
		for(TraitPair traitPair : creatureDNAString.getTraitString()){
			traitTypeToTraitMap.put(TraitType.valueOf(traitPair.getTraits()[0].getTraitType()), traitPair.getTraits()[0]);
		}

		if(traitTypeToTraitMap.get(TraitType.speed).getTraitDefinition().equals("slow")){
			speed = .005;
		} else if(traitTypeToTraitMap.get(TraitType.speed).getTraitDefinition().equals("medium")){
			speed = .01;
		} else if(traitTypeToTraitMap.get(TraitType.speed).getTraitDefinition().equals("fast")){
			speed = .02;
		}

		if(traitTypeToTraitMap.get(TraitType.size).getTraitDefinition().equals("small")){
			size = new Size(.4, .4);
		} else if(traitTypeToTraitMap.get(TraitType.size).getTraitDefinition().equals("medium")){
			size = new Size(.5, .5);
		} else if(traitTypeToTraitMap.get(TraitType.size).getTraitDefinition().equals("large")){
			size = new Size(.6, .6);
		} else if(traitTypeToTraitMap.get(TraitType.size).getTraitDefinition().equals("huge")){
			size = new Size(.7, .7);
		}
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

	public Map<TraitType, Trait> getTraitTypeToTraitMap() {
		return traitTypeToTraitMap;
	}

	public void setTraitTypeToTraitMap(Map<TraitType, Trait> traitTypeToTraitMap) {
		this.traitTypeToTraitMap = traitTypeToTraitMap;
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
