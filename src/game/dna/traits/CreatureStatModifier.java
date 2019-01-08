package game.dna.traits;

import game.dna.stats.StatType;

import java.util.Map;

public class CreatureStatModifier {

	Map<StatType, String> statModifiers;

	public Map<StatType, String> getStatModifiers() {
		return statModifiers;
	}

	public void setStatModifiers(Map<StatType, String> statModifiers) {
		this.statModifiers = statModifiers;
	}
}
