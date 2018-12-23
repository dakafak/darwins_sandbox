package game.dna.traits;

public class Trait {
	char[] characterRepresentation;

	public Trait(String traitRepresentation){
		characterRepresentation = traitRepresentation.toCharArray();
	}

	public Trait(char[] characterRepresentation){
		this.characterRepresentation = characterRepresentation;
	}

	public char[] getCharacterRepresentation() {
		return characterRepresentation;
	}

	public void setCharacterRepresentation(char[] characterRepresentation) {
		this.characterRepresentation = characterRepresentation;
	}

	@Override
	public String toString(){
		StringBuilder traitStringBuilder = new StringBuilder();
		for(char characters : characterRepresentation){
			traitStringBuilder.append(characters);
		}

		return traitStringBuilder.toString();
	}
}
