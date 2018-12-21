package game.dna.traits;

public enum BodyColor {
	tan("cc"),
	brown("Cc"),
	black("CC");

	char[] characterRepresentation;

	BodyColor(String traitRepresentation){
		characterRepresentation = traitRepresentation.toCharArray();
	}
}
