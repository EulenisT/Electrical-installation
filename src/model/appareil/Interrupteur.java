package model.appareil;

import lombok.Getter;

@Getter
public class Interrupteur extends Appareil {
	private final int nbContacts;
	private final int nbDirections;

	/**
	 * @param code
	 * @param nom
	 * @param nbPhases
	 * @param svg
	 * @param nbContacts
	 * @param nbDirections
	 */
	public Interrupteur(String code, String nom, int nbPhases, String svg, int nbContacts, int nbDirections) {
		super(code, nom, nbPhases, svg);
		this.nbContacts = nbContacts;
		this.nbDirections = nbDirections;
	}

}
