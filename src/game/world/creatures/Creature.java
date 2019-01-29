package game.world.creatures;

import game.dna.DNAString;
import game.dna.stats.Diet;
import game.dna.stats.Sex;
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
	CreatureState creatureState;
	double daySpawned;
	private double energyRestoration = 150;

	Sex sexOfCreature;//TODO maybe should be a trait instead, could by x and Y
	Location location;
	Size size;//TODO  some of these traits can be moved into creature stats once that loader is added
	Diet diet;
	Direction direction = NORTH;
	double speed;
	double lifeSpan;
	double mating_frequency;
	double lastMatingDay;
	double energy;
	double hungerThreshold;

    public Creature(double x,
					double y,
					DNAString creatureDNAString,
					Sex sexOfCreature,
					Map<String, List<CreatureStatModifier>> traitNameAndValueToCreatureStatModifiers,
					double currentDay){
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
		diet =  Diet.valueOf(creatureStats.get(StatType.diet).toString());
		energy = (Double) creatureStats.get(StatType.energy);
		hungerThreshold = (Double) creatureStats.get(StatType.hunger_threshold);
    }

    public boolean canMate(double currentDay){
    	if((currentDay - lastMatingDay) > mating_frequency){
    		return true;
		}

		return false;
	}

	public boolean isHungry(){
    	return energy < hungerThreshold;
	}

	public void addEnergy(double energyAddition){
    	energy += energyAddition;
	}

	@Override
    public String toString(){
        return sexOfCreature.getSexString().substring(0, 1) + "{" + location.getX() + ", " + location.getY() + "}" + "(" + creatureDNAString.toString() + ")";
    }

    long nextDirectionChange;
    int wanderTimeInMilliseconds = 500;
    int wanderTimeAdditionInMilliseconds = 1000;
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

	public void wander(double deltaUpdate, Location minWorldLocation, Location maxWorldLocation){
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

		move(deltaUpdate, dx, dy, minWorldLocation, maxWorldLocation);
	}

	public boolean shouldDie(double day){
		if(energy <= 0 || day - daySpawned > lifeSpan){
			return true;
		}

		return false;
	}

	public void moveCloserToPoint(double deltaUpdate, double x, double y, Location minWorldLocation, Location maxWorldLocation) {
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

		move(deltaUpdate, dx, dy, minWorldLocation, maxWorldLocation);
	}

	double cachedLocationXChange;

	double cachedLocationYChange;
	double cachedDx;
	double cachedDy;
	double cachedDeltaUpdate;
	private void move(double deltaUpdate, double dx, double dy, Location minWorldLocation, Location maxWorldLocation){
		if(dx != cachedDx || dy != cachedDy || deltaUpdate != cachedDeltaUpdate){
			cachedLocationXChange = dx * speed * deltaUpdate;
			cachedLocationYChange = dy * speed * deltaUpdate;
		}

		double nextX = location.getX() + cachedLocationXChange;
		double nextY = location.getY() + cachedLocationYChange;

		if(nextX < minWorldLocation.getX()){
			location.setX(minWorldLocation.getX());
			direction = MOVING_EAST;
		} else if(nextX > maxWorldLocation.getX()){
			location.setX(maxWorldLocation.getX());
			direction = MOVING_WEST;
		} else if(nextX >= minWorldLocation.getX() && nextX <= maxWorldLocation.getX()){
			location.setX(nextX);
		}

		if(nextY < minWorldLocation.getY()){
			location.setY(minWorldLocation.getY());
			direction = MOVING_SOUTH;
		} else if(nextY > maxWorldLocation.getY()){
			location.setY(maxWorldLocation.getY());
			direction = MOVING_NORTH;
		} else if(nextY >= minWorldLocation.getY() && nextY <= maxWorldLocation.getY()){
			location.setY(nextY);
		}

		energy -= .005 * deltaUpdate;//TODO placeholder, should create something that scales off of distance traveled
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

	public void setCreatureDNAString(DNAString creatureDNAString) {
		this.creatureDNAString = creatureDNAString;
	}

	public void setSexOfCreature(Sex sexOfCreature) {
		this.sexOfCreature = sexOfCreature;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public void setSize(Size size) {
		this.size = size;
	}

	public Diet getDiet() {
		return diet;
	}

	public void setDiet(Diet diet) {
		this.diet = diet;
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public double getLifeSpan() {
		return lifeSpan;
	}

	public void setLifeSpan(double lifeSpan) {
		this.lifeSpan = lifeSpan;
	}

	public double getMating_frequency() {
		return mating_frequency;
	}

	public void setMating_frequency(double mating_frequency) {
		this.mating_frequency = mating_frequency;
	}

	public double getDaySpawned() {
		return daySpawned;
	}

	public void setDaySpawned(double daySpawned) {
		this.daySpawned = daySpawned;
	}

	public long getNextDirectionChange() {
		return nextDirectionChange;
	}

	public void setNextDirectionChange(long nextDirectionChange) {
		this.nextDirectionChange = nextDirectionChange;
	}

	public int getWanderTimeInMilliseconds() {
		return wanderTimeInMilliseconds;
	}

	public void setWanderTimeInMilliseconds(int wanderTimeInMilliseconds) {
		this.wanderTimeInMilliseconds = wanderTimeInMilliseconds;
	}

	public int getWanderTimeAdditionInMilliseconds() {
		return wanderTimeAdditionInMilliseconds;
	}

	public void setWanderTimeAdditionInMilliseconds(int wanderTimeAdditionInMilliseconds) {
		this.wanderTimeAdditionInMilliseconds = wanderTimeAdditionInMilliseconds;
	}

	public double getCachedLocationXChange() {
		return cachedLocationXChange;
	}

	public void setCachedLocationXChange(double cachedLocationXChange) {
		this.cachedLocationXChange = cachedLocationXChange;
	}

	public double getCachedLocationYChange() {
		return cachedLocationYChange;
	}

	public void setCachedLocationYChange(double cachedLocationYChange) {
		this.cachedLocationYChange = cachedLocationYChange;
	}

	public double getCachedDx() {
		return cachedDx;
	}

	public void setCachedDx(double cachedDx) {
		this.cachedDx = cachedDx;
	}

	public double getCachedDy() {
		return cachedDy;
	}

	public void setCachedDy(double cachedDy) {
		this.cachedDy = cachedDy;
	}

	public double getCachedDeltaUpdate() {
		return cachedDeltaUpdate;
	}

	public void setCachedDeltaUpdate(double cachedDeltaUpdate) {
		this.cachedDeltaUpdate = cachedDeltaUpdate;
	}

	public double getEnergy() {
		return energy;
	}

	public void setEnergy(double energy) {
		this.energy = energy;
	}

	public double getHungerThreshold() {
		return hungerThreshold;
	}

	public void setHungerThreshold(double hungerThreshold) {
		this.hungerThreshold = hungerThreshold;
	}

	public double getEnergyRestoration() {
		return energyRestoration;
	}

	public void setEnergyRestoration(double energyRestoration) {
		this.energyRestoration = energyRestoration;
	}
}
