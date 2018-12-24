package game.dna.traits;

public class Trait {

    char traitCharacter;
    String traitType;
    String traitDefinition;

    public Trait(char traitCharacter, String traitDefinition, String traitType){
        this.traitCharacter = traitCharacter;
        this.traitDefinition = traitDefinition;
        this.traitType = traitType;
    }

    public char getTraitCharacter() {
        return traitCharacter;
    }

    public void setTraitCharacter(char traitCharacter) {
        this.traitCharacter = traitCharacter;
    }

    public String getTraitType() {
        return traitType;
    }

    public void setTraitType(String traitType) {
        this.traitType = traitType;
    }

    public String getTraitDefinition() {
        return traitDefinition;
    }

    public void setTraitDefinition(String traitDefinition) {
        this.traitDefinition = traitDefinition;
    }

    @Override
    public String toString(){
        return traitType + "(" + traitCharacter + ")";
    }

}
