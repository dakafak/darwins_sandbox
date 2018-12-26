package game;

import java.util.Map;

public class StatisticsSave {

    Map<String, Integer> traitPopularityMap;
    int totalNumberOfCreatures;
    int numberMaleCreatures;
    int numberFemaleCreatures;
    int numberAsexualCreatures;

    public Map<String, Integer> getTraitPopularityMap() {
        return traitPopularityMap;
    }

    public void setTraitPopularityMap(Map<String, Integer> traitPopularityMap) {
        this.traitPopularityMap = traitPopularityMap;
    }

    public int getTotalNumberOfCreatures() {
        return totalNumberOfCreatures;
    }

    public void setTotalNumberOfCreatures(int totalNumberOfCreatures) {
        this.totalNumberOfCreatures = totalNumberOfCreatures;
    }

    public int getNumberMaleCreatures() {
        return numberMaleCreatures;
    }

    public void setNumberMaleCreatures(int numberMaleCreatures) {
        this.numberMaleCreatures = numberMaleCreatures;
    }

    public int getNumberFemaleCreatures() {
        return numberFemaleCreatures;
    }

    public void setNumberFemaleCreatures(int numberFemaleCreatures) {
        this.numberFemaleCreatures = numberFemaleCreatures;
    }

    public int getNumberAsexualCreatures() {
        return numberAsexualCreatures;
    }

    public void setNumberAsexualCreatures(int numberAsexualCreatures) {
        this.numberAsexualCreatures = numberAsexualCreatures;
    }
}
