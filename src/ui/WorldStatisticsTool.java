package ui;

import game.World;
import game.dna.traits.TraitType;
import game.world.creatures.Creature;
import game.world.creatures.Sex;
import game.dna.traits.Trait;
import game.dna.traits.TraitPair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class WorldStatisticsTool {

    long lastRetrieval;
    long cacheReset = 0;//TODO probably not really necessary but may be beneficial at some point to set to the refresh rate

	Map<TraitType, Map<Trait, Integer>> traitsToNumberCreaturesWithTrait;

    private List<Creature> creatureCacheList;

    public WorldStatisticsTool(){
    	traitsToNumberCreaturesWithTrait = new HashMap<>();
	}

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

    public Map<String, Integer> getTraitPopularityMap(World world, boolean onlyAddVisibleTraits){
        checkCache(world);
        Map<String, Integer> traitsToUsageCount = new HashMap<>();
		for(int i = 0; i < world.getCreatures().size(); i++){
			Creature creature = world.getCreatures().get(i);
            Set<Trait> traitsForCreature = new HashSet<>();

            for(TraitPair traitPair : creature.getCreatureDNAString().getTraitString()){
                if(onlyAddVisibleTraits) {
                    traitsForCreature.add(traitPair.getTraits()[0]);
                } else {
                    for(Trait trait : traitPair.getTraits()){
                        traitsForCreature.add(trait);
                    }
                }
            }

            for(Trait trait : traitsForCreature){
                String keyForTrait = trait.getTraitType() + ":" + trait.getTraitDefinition();
                if (traitsToUsageCount.containsKey(keyForTrait)) {
                    traitsToUsageCount.put(keyForTrait, traitsToUsageCount.get(keyForTrait) + 1);
                } else {
                    traitsToUsageCount.put(keyForTrait, 1);
                }
            }
        }

        return traitsToUsageCount;
    }

    private void checkCache(World world){
        if(creatureCacheList == null || (System.currentTimeMillis() - lastRetrieval) > cacheReset){
            creatureCacheList = world.getCreatures();
            lastRetrieval = System.currentTimeMillis();
        }
    }

    public Map<TraitType, Map<Trait, Integer>> getTraitsToNumberCreaturesWithTrait(){
    	return traitsToNumberCreaturesWithTrait;
	}

	public void addTraitsForNewCreatures(List<Creature> creatures){
		for(int i = 0; i < creatures.size(); i++){
			TraitPair[] traitPairsForCreature = creatures.get(i).getCreatureDNAString().getTraitString();
			for(int j = 0; j < traitPairsForCreature.length; j++) {
				Trait trait = traitPairsForCreature[j].getTraits()[0];
				if(!traitsToNumberCreaturesWithTrait.containsKey(trait.getTraitType())){
					traitsToNumberCreaturesWithTrait.put(trait.getTraitType(), new HashMap<>());
				}

				if(traitsToNumberCreaturesWithTrait.get(trait.getTraitType()).containsKey(trait)) {
					traitsToNumberCreaturesWithTrait.get(trait.getTraitType()).put(trait, traitsToNumberCreaturesWithTrait.get(trait.getTraitType()).get(trait) + 1);
				} else {
					traitsToNumberCreaturesWithTrait.get(trait.getTraitType()).put(trait, 1);
				}
			}
		}
	}

	public void removeTraitsForNewCreatures(List<Creature> creatures){
		for(int i = 0; i < creatures.size(); i++){
			TraitPair[] traitPairsForCreature = creatures.get(i).getCreatureDNAString().getTraitString();
			for(int j = 0; j < traitPairsForCreature.length; j++) {
				Trait trait = traitPairsForCreature[j].getTraits()[0];
				if(traitsToNumberCreaturesWithTrait.containsKey(trait)) {
					traitsToNumberCreaturesWithTrait.get(trait.getTraitType()).put(trait, traitsToNumberCreaturesWithTrait.get(trait.getTraitType()).get(trait) - 1);
				} else {
					traitsToNumberCreaturesWithTrait.get(trait.getTraitType()).put(trait, -1);
				}
			}
		}
	}

}
