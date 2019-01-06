package game.world.creatures;

import game.dna.DNAString;
import game.world.units.Direction;
import game.world.units.Location;
import game.world.units.Size;

import java.util.Map;

import static game.world.units.Direction.*;
import static game.world.units.Direction.MOVING_SOUTH;

public class Creature {

    DNAString creatureDNAString;
    Map<Enum, Object> creatureStats;
    Sex sexOfCreature;//TODO maybe should be a trait instead, could by x and Y
	Location location;
	Size size;//TODO  some of these traits can be moved into creature stats once that loader is added
	Direction direction = NORTH;
	double speed;

    public Creature(double x, double y){
    	speed = .01;
    	location = new Location(x, y);
    	size = new Size(.5, .5);
    }

    public Creature(DNAString creatureDNAString, Map<Enum, Object> creatureStats, Sex sexOfCreature, double x, double y){
        this.creatureDNAString = creatureDNAString;
        this.creatureStats = creatureStats;
        this.sexOfCreature = sexOfCreature;
        location = new Location(x, y);
    }

    public DNAString getCreatureDNAString() {
        return creatureDNAString;
    }

    public void setCreatureDNAString(DNAString creatureDNAString) {
        this.creatureDNAString = creatureDNAString;
    }

    public Map<Enum, Object> getCreatureStats() {
        return creatureStats;
    }

    public void setCreatureStats(Map<Enum, Object> creatureStats) {
        this.creatureStats = creatureStats;
    }

    public Sex getSexOfCreature() {
        return sexOfCreature;
    }

    public void setSexOfCreature(Sex sexOfCreature) {
        this.sexOfCreature = sexOfCreature;
    }

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public Size getSize() {
		return size;
	}

	public void setSize(Size size) {
		this.size = size;
	}

	@Override
    public String toString(){
        return sexOfCreature.getSexString().substring(0, 1) + "(" + creatureDNAString.toString() + ")";
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
