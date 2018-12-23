package game;

import game.dna.DNACombiner;
import game.dna.DNAString;
import game.dna.traits.Trait;

import java.util.ArrayList;

public class SimulatorRunner {

	public static void main(String[] args){
		DNAString parent1 = new DNAString();
		parent1.setTraitString(new Trait[]{
				new Trait("Ss"),
				new Trait("CC")
		});

		DNAString parent2 = new DNAString();
		parent2.setTraitString(new Trait[]{
				new Trait("ss"),
				new Trait("cc")
		});

		ArrayList<DNAString> allDNAStrings = new ArrayList<>();
		allDNAStrings.add(parent1);
		allDNAStrings.add(parent2);

		for(int i = 0; i < 5; i++){
			System.out.println(allDNAStrings);

			int parent1ID = (int)Math.floor(Math.random()*allDNAStrings.size());
			DNAString parent1ToCombine = allDNAStrings.get(parent1ID);

			if(allDNAStrings.size() == 1) {
				DNAString childString = DNACombiner.getAsexualDNAString(parent1ToCombine);
				allDNAStrings.add(childString);
			} else {
				int parent2ID = parent1ID > 0 ? parent1ID - 1 : parent1ID + 1;
				DNAString parent2ToCombine = allDNAStrings.get(parent2ID);

				DNAString childString = DNACombiner.getChildDNAString(parent1ToCombine, parent2ToCombine);
				allDNAStrings.add(childString);
			}

		}
		System.out.println(allDNAStrings);
	}

}
