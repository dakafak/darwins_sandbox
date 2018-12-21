package game.dna.traits;

public enum BodyShape {
	small("ss"),
	medium("Ss"),
	large("SS");

	char[] characterRepresentation;

	BodyShape(String traitRepresentation){
		characterRepresentation = traitRepresentation.toCharArray();
	}
}
