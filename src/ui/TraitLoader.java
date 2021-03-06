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

	private String traitFolderLocation = "traits";

	Map<TraitType, List<Trait>> traitTypeToAllTraits;
	List<TraitType> allTraitTypesInOrder;

	public TraitLoader() {
		loadTraitTypesAndOrder();
		loadTraits();
		loadTraitToStatDefinitions();
	}

	Map<String, List<CreatureStatModifier>> traitNameAndValueToCreatureStatModifiers;

	private void loadTraitToStatDefinitions() {
		traitNameAndValueToCreatureStatModifiers = loadTraitToCreatureStatDefinitions(traitFolderLocation + "/trait_to_creature_stat_definitions.json");
	}

	private Map<String, List<CreatureStatModifier>> loadTraitToCreatureStatDefinitions(String traitOrderFileName) {
		Map<String, List<CreatureStatModifier>> traitNameAndValueToStatModifiers = new HashMap<>();
		File traitOrderFile = new File(traitOrderFileName);

		if (traitOrderFile.exists()) {
			StringBuilder jsonToParse = new StringBuilder();
			try {
				BufferedReader br = new BufferedReader(new FileReader(traitOrderFile));
				String readLine = "";
				while ((readLine = br.readLine()) != null) {
					jsonToParse.append(readLine);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			JSONObject objectFromJsonString = new JSONObject(jsonToParse.toString());
			if (objectFromJsonString != null) {
				JSONArray traitToCreatureStatDefinitions = objectFromJsonString.getJSONArray("traitToStatDefinitions");
				if (traitToCreatureStatDefinitions != null) {
					traitNameAndValueToStatModifiers = new HashMap<>();

					for (int i = 0; i < traitToCreatureStatDefinitions.length(); i++) {
						JSONObject creatureStatDefinition = traitToCreatureStatDefinitions.getJSONObject(i);
						if (creatureStatDefinition != null) {
							CreatureStatModifier newStatModifier = new CreatureStatModifier();
							String traitName = creatureStatDefinition.getString("trait");
							String traitValue = creatureStatDefinition.getString("traitValue");

							Map<StatType, Object> statModifiers = new HashMap<>();
							JSONArray statChanges = creatureStatDefinition.getJSONArray("stats");
							for (int j = 0; j < statChanges.length(); j++) {
								JSONObject statModifier = statChanges.getJSONObject(j);
								String statName = statModifier.getString("statName");
								String statModifierStringValue = statModifier.getString("statModifier");

								StatType statType = StatType.valueOf(statName);
								Object statModifierValue;
								if (statType.getClassType().equals(Double.class)) {
									statModifierValue = Double.parseDouble(statModifierStringValue);
								} else if (statType.getClassType().equals(Color.class)) {
									statModifierValue = Color.decode(statModifierStringValue);
								} else {
									statModifierValue = statModifierStringValue;
								}

								statModifiers.put(statType, statModifierValue);
							}
							newStatModifier.setStatModifiers(statModifiers);

							TraitNameAndValuePair traitNameAndValuePair = new TraitNameAndValuePair(traitName, traitValue);
							if (traitNameAndValueToStatModifiers.containsKey(traitNameAndValuePair.getKey())) {
								List<CreatureStatModifier> creatureStatList = traitNameAndValueToStatModifiers.get(traitNameAndValuePair.getKey());
								creatureStatList.add(newStatModifier);
							} else {
								List<CreatureStatModifier> newCreatureStatList = new ArrayList<>();
								newCreatureStatList.add(newStatModifier);
								traitNameAndValueToStatModifiers.put(traitNameAndValuePair.getKey(), newCreatureStatList);
							}
						}
					}
				}
			}
		}

		return traitNameAndValueToStatModifiers;
	}

	private void loadTraitTypesAndOrder() {
		allTraitTypesInOrder = new ArrayList<>();

		for (TraitType traitType : TraitType.values()) {
			allTraitTypesInOrder.add(traitType);
		}
	}

	private void loadTraits() {
		traitTypeToAllTraits = new HashMap<>();

		List<Trait> allTraits = getAllTraits();
		for (Trait trait : allTraits) {
			if (traitTypeToAllTraits.containsKey(trait.getTraitType())) {
				traitTypeToAllTraits.get(trait.getTraitType()).add(trait);
			} else {
				List<Trait> traits = new ArrayList<>();
				traits.add(trait);
				traitTypeToAllTraits.put(trait.getTraitType(), traits);
			}
		}
	}

	private List<Trait> getAllTraits() {
		File traitFolder = new File(traitFolderLocation);

		if (traitFolder.exists() && traitFolder.isDirectory()) {
			List<Trait> allTraits = new ArrayList<>();

			TraitType[] traitTypes = TraitType.values();
			for (TraitType traitType : traitTypes) {
				for (String traitTypeDefinition : traitType.getStrings()) {
					String[] traitTypeDefinitionSplit = traitTypeDefinition.split("=");
					char traitTypeCharacter = traitTypeDefinitionSplit[0].charAt(0);
					String traitTypeDefinitionText = traitTypeDefinitionSplit[1];
					Trait newTrait = new Trait(traitTypeCharacter, traitTypeDefinitionText, traitType);
					allTraits.add(newTrait);
				}
			}

			return allTraits;
		} else {
			System.out.println("trait folder does not exist");
		}

		return null;
	}

	public TraitPair getRandomTraitPair(TraitType traitType) {
		Trait trait1 = getRandomTrait(traitType);
		Trait trait2 = getRandomTrait(traitType);
		TraitPair newPair = new TraitPair(trait1, trait2);
		return newPair;
	}

	public Trait getRandomTrait(TraitType traitType) {
		if (traitTypeToAllTraits.containsKey(traitType)) {
			List<Trait> traitsForType = traitTypeToAllTraits.get(traitType);
			int randomTraitIndex = (int) Math.floor(Math.random() * traitsForType.size());
			Trait randomTrait = traitsForType.get(randomTraitIndex);
			return new Trait(randomTrait.getTraitCharacter(), randomTrait.getTraitDefinition(), randomTrait.getTraitType());
		}

		return null;
	}

	public List<TraitType> getTraitTypesInOrder() {
		return allTraitTypesInOrder;
	}

	public Map<String, List<CreatureStatModifier>> getTraitNameAndValueToCreatureStatModifiers() {
		return traitNameAndValueToCreatureStatModifiers;
	}

}
