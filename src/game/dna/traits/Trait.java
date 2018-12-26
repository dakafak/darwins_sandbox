package game.dna.traits;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trait trait = (Trait) o;
        return traitCharacter == trait.traitCharacter &&
                Objects.equals(traitType, trait.traitType) &&
                Objects.equals(traitDefinition, trait.traitDefinition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(traitCharacter, traitType, traitDefinition);
    }
}
