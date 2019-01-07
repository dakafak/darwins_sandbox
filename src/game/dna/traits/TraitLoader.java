package game.dna.traits;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TraitLoader {

    private String statsFolderLocation = "";
    private String traitFolderLocation = "traits";

    Map<String, List<Trait>> traitTypeToAllTraits;
    List<String> allTraitTypesInOrder;

    public TraitLoader(){
        loadTraitTypesAndOrder();
        loadTraits();
    }

    private void loadTraitTypesAndOrder(){
        allTraitTypesInOrder = new ArrayList<>();
        for(TraitType traitType : TraitType.values()){
        	allTraitTypesInOrder.add(traitType.getValue());
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
                    String traitType = file.getName().substring(6);

                    try {
                        BufferedReader br = new BufferedReader(new FileReader(file));
                        String readLine = "";
                        while((readLine = br.readLine()) != null){
                            String[] traitProperites = readLine.split("=");
                            if(traitProperites.length == 2){
                                Trait newTrait = new Trait(traitProperites[0].charAt(0), traitProperites[1], traitType);
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

            System.out.println("traits: " + allTraits);
            return allTraits;
        } else {
            System.out.println("trait folder does not exist");
        }

        return null;
    }

    public TraitPair getRandomTraitPair(String traitType){
        Trait trait1 = getRandomTrait(traitType);
        Trait trait2 = getRandomTrait(traitType);
        TraitPair newPair = new TraitPair(trait1, trait2);
        return newPair;
    }

    private Trait getRandomTrait(String traitType){
        if(traitTypeToAllTraits.containsKey(traitType)) {
            List<Trait> traitsForType = traitTypeToAllTraits.get(traitType);
            int randomTraitIndex = (int)Math.floor(Math.random()*traitsForType.size());
            Trait randomTrait = traitsForType.get(randomTraitIndex);
            return new Trait(randomTrait.getTraitCharacter(), randomTrait.getTraitDefinition(), randomTrait.getTraitType());
        }

        return null;
    }

    public List<String> getTraitTypesInOrder(){
        return allTraitTypesInOrder;
    }

}
