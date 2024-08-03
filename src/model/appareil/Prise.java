package model.appareil;

import lombok.Getter;

@Getter
public class Prise extends Appareil {
	private final boolean terre;
	private final boolean protectionEnfant;
	private final boolean ip44;

	/**
	 * @param code
	 * @param nom
	 * @param nbPhases
	 * @param svg
	 * @param terre
	 * @param protectionEnfant
	 * @param ip44
	 */
	public Prise(String code, String nom, int nbPhases, String svg, boolean terre, boolean protectionEnfant,
			boolean ip44) {
		super(code, nom, nbPhases, svg);
		this.terre = terre;
		this.protectionEnfant = protectionEnfant;
		this.ip44 = ip44;
	}
}
