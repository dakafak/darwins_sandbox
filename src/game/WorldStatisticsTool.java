package game;

import game.creatures.Creature;
import game.creatures.Sex;

import java.util.List;
import java.util.stream.Collectors;

public class WorldStatisticsTool {

    long lastRetrieval;
    long cacheReset = 0;//TODO probably not really necessary but may be beneficial at some point to set to the refresh rate

    private List<Creature> creatureCacheList;

    public int getNumberOfCreatures(World world){
        checkCache(world);
        return world.getCreatures().size();
    }

    public int getNumberMaleCreatures(World world){
        checkCache(world);
        List<Creature> maleCreatures;
        maleCreatures = creatureCacheList.stream().filter(creature -> creature.getSexOfCreature().equals(Sex.MALE)).collect(Collectors.toList());
        return maleCreatures.size();
    }

    public int getNumberFemaleCreatures(World world){
        checkCache(world);
        List<Creature> femaleCreatures;
        femaleCreatures = creatureCacheList.stream().filter(creature -> creature.getSexOfCreature().equals(Sex.FEMALE)).collect(Collectors.toList());
        return femaleCreatures.size();
    }

    public int getNumberAsexualCreatures(World world){
        checkCache(world);
        List<Creature> asexualCreatures;
        asexualCreatures = creatureCacheList.stream().filter(creature -> creature.getSexOfCreature().equals(Sex.ASEXUAL)).collect(Collectors.toList());
        return asexualCreatures.size();
    }

    private void checkCache(World world){
        if(creatureCacheList == null || (System.currentTimeMillis() - lastRetrieval) > cacheReset){
            creatureCacheList = world.getCreatures();
            lastRetrieval = System.currentTimeMillis();
        }
    }

}
