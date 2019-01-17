package ui;

import game.dna.stats.StatType;
import game.dna.traits.CreatureStatModifier;
import game.dna.traits.Trait;
import game.dna.traits.TraitNameAndValuePair;
import game.dna.traits.TraitPair;
import game.dna.traits.TraitType;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TraitLoader {

    private String statsFolderLocation = "";
    private String traitFolderLocation = "traits";

    Map<TraitType, List<Trait>> traitTypeToAllTraits;
    List<TraitType> allTraitTypesInOrder;

    public TraitLoader(){
        loadTraitTypesAndOrder();
        loadTraits();
        loadTraitToCreatureStatDefinitions();
    }

    Map<String, List<CreatureStatModifier>> traitNameAndValueToCreatureStatModifiers;
    private void loadTraitToCreatureStatDefinitions(){
		File traitOrderFile = new File(traitFolderLocation + "/trait_to_creature_stat_definitions");
		if(traitOrderFile.exists()){
			StringBuilder jsonToParse = new StringBuilder();
			try {
				BufferedReader br = new BufferedReader(new FileReader(traitOrderFile));
				String readLine = "";
				while((readLine = br.readLine()) != null){
					jsonToParse.append(readLine);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			JSONObject objectFromJsonString = new JSONObject(jsonToParse.toString());
			if(objectFromJsonString != null) {
				JSONArray traitToCreatureStatDefinitions = objectFromJsonString.getJSONArray("traitToStatDefinitions");
				if(traitToCreatureStatDefinitions != null){
					traitNameAndValueToCreatureStatModifiers = new HashMap<>();

					for(int i = 0; i < traitToCreatureStatDefinitions.length(); i++){
						JSONObject creatureStatDefinition = traitToCreatureStatDefinitions.getJSONObject(i);
						if(creatureStatDefinition != null){
							CreatureStatModifier newStatModifier = new CreatureStatModifier();
							String traitName = creatureStatDefinition.getString("trait");
							String traitValue = creatureStatDefinition.getString("traitValue");

							Map<StatType, Object> statModifiers = new HashMap<>();
							JSONArray statChanges = creatureStatDefinition.getJSONArray("stats");
							for(int j = 0; j < statChanges.length(); j++){
								JSONObject statModifier = statChanges.getJSONObject(j);
								String statName = statModifier.getString("statName");
								String statModifierStringValue = statModifier.getString("statModifier");

								StatType statType = StatType.valueOf(statName);
								Object statModifierValue;
								if(statType.getClassType().equals(Double.class)) {
									statModifierValue = Double.parseDouble(statModifierStringValue);
								} else if(statType.getClassType().equals(Color.class)){
									statModifierValue = Color.decode(statModifierStringValue);
								} else {
									statModifierValue = statModifierStringValue;
								}

								statModifiers.put(statType, statModifierValue);
							}
							newStatModifier.setStatModifiers(statModifiers);

							TraitNameAndValuePair traitNameAndValuePair = new TraitNameAndValuePair(traitName, traitValue);
							if(traitNameAndValueToCreatureStatModifiers.containsKey(traitNameAndValuePair.getKey())) {
								List<CreatureStatModifier> creatureStatList = traitNameAndValueToCreatureStatModifiers.get(traitNameAndValuePair.getKey());
								creatureStatList.add(newStatModifier);
							} else {
								List<CreatureStatModifier> newCreatureStatList = new ArrayList<>();
								newCreatureStatList.add(newStatModifier);
								traitNameAndValueToCreatureStatModifiers.put(traitNameAndValuePair.getKey(), newCreatureStatList);
							}
						}
					}
				}
			}
		}
	}

    private void loadTraitTypesAndOrder(){
    	allTraitTypesInOrder = new ArrayList<>();

		for(TraitType traitType : TraitType.values()){
			allTraitTypesInOrder.add(traitType);
		}
    }

    private void loadTraits(){
        traitTypeToAllTraits = new HashMap<>();

        List<Trait> allTraits = getAllTraits();
        for(Trait trait : allTraits){
            if(traitTypeToAllTraits.containsKey(trait.getTraitType())){
                traitTypeToAllTraits.get(trait.getTraitType()).add(trait);
            } else {
                List<Trait> traits = new ArrayList<>();
                traits.add(trait);
                traitTypeToAllTraits.put(trait.getTraitType(), traits);
            }
        }
    }

    private List<Trait> getAllTraits(){
        File traitFolder = new File(traitFolderLocation);

        if(traitFolder.exists() && traitFolder.isDirectory()){
            List<Trait> allTraits = new ArrayList<>();

            File[] files = traitFolder.listFiles();
            for(File file : files){
                if(file.getName().startsWith("trait_")){
                    String traitTypeString = file.getName().substring(6);

                    try {
                        BufferedReader br = new BufferedReader(new FileReader(file));
                        String readLine = "";
                        while((readLine = br.readLine()) != null){
                            String[] traitProperites = readLine.split("=");
                            if(traitProperites.length == 2){
                                Trait newTrait = new Trait(traitProperites[0].charAt(0), traitProperites[1], TraitType.valueOf(traitTypeString));
                                allTraits.add(newTrait);
                            }
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

//            System.out.println("traits: " + allTraits);
            return allTraits;
        } else {
            System.out.println("trait folder does not exist");
        }

        return null;
    }

    public TraitPair getRandomTraitPair(TraitType traitType){
        Trait trait1 = getRandomTrait(traitType);
        Trait trait2 = getRandomTrait(traitType);
        TraitPair newPair = new TraitPair(trait1, trait2);
        return newPair;
    }

    private Trait getRandomTrait(TraitType traitType){
        if(traitTypeToAllTraits.containsKey(traitType)) {
            List<Trait> traitsForType = traitTypeToAllTraits.get(traitType);
            int randomTraitIndex = (int)Math.floor(Math.random()*traitsForType.size());
            Trait randomTrait = traitsForType.get(randomTraitIndex);
            return new Trait(randomTrait.getTraitCharacter(), randomTrait.getTraitDefinition(), randomTrait.getTraitType());
        }

        return null;
    }

    public List<TraitType> getTraitTypesInOrder(){
        return allTraitTypesInOrder;
    }

	public Map<String, List<CreatureStatModifier>> getTraitNameAndValueToCreatureStatModifiers() {
		return traitNameAndValueToCreatureStatModifiers;
	}

	public void setTraitNameAndValueToCreatureStatModifiers(Map<String, List<CreatureStatModifier>> traitNameAndValueToCreatureStatModifiers) {
		this.traitNameAndValueToCreatureStatModifiers = traitNameAndValueToCreatureStatModifiers;
	}
}
