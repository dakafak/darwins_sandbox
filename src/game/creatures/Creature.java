package game.creatures;

import game.dna.DNAString;

import java.util.Map;

public class Creature {

    DNAString creatureDNAString;
    Map<Enum, Object> creatureStats;
    Sex sexOfCreature;//TODO maybe should be a trait instead, could by x and Y

    public Creature(){
    }

    public Creature(DNAString creatureDNAString, Map<Enum, Object> creatureStats, Sex sexOfCreature){
        this.creatureDNAString = creatureDNAString;
        this.creatureStats = creatureStats;
        this.sexOfCreature = sexOfCreature;
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

    @Override
    public String toString(){
        return sexOfCreature.getSexString().substring(0, 1) + "(" + creatureDNAString.toString() + ")";
    }
}
