package game.dna.traits;

import game.dna.stats.StatType;

import java.util.Map;

public class CreatureStatModifier {

	Map<StatType, Object> statModifiers;

	public Map<StatType, Object> getStatModifiers() {
		return statModifiers;
	}

	public void setStatModifiers(Map<StatType, Object> statModifiers) {
		this.statModifiers = statModifiers;
	}
}
