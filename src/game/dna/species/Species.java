package game.dna.species;

import java.util.Objects;
import java.util.UUID;

public class Species {

	UUID id;
	String speciesName;

	public Species(String speciesName) {
		this.speciesName = speciesName;
		id = UUID.randomUUID();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Species species = (Species) o;
		return Objects.equals(id, species.id);
	}

	@Override
	public int hashCode() {

		return Objects.hash(id);
	}

	private static String[] randomSpeciesNames = new String[]{"things", "stuff", "etc"};
	public static Species getRandomNewSpecies(){
		return new Species(randomSpeciesNames[(int)Math.random()*randomSpeciesNames.length]);
	}
}
