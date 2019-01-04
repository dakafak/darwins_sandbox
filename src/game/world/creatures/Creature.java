package game.world.creatures;

import game.dna.DNAString;
import game.world.units.Location;
import game.world.units.Size;

import java.util.Map;

public class Creature {

    DNAString creatureDNAString;
    Map<Enum, Object> creatureStats;
    Sex sexOfCreature;//TODO maybe should be a trait instead, could by x and Y
	Location location;
	Size size;

    public Creature(double x, double y){
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
}
