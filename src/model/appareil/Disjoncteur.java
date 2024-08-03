package model.appareil;

import lombok.Getter;
import lombok.ToString;
@ToString(callSuper = true)
public class Disjoncteur extends Appareil {
	@Getter
	private final int amperage;// en A

	/**
	 * @param code
	 * @param nom
	 * @param nbPhases
	 * @param svg
	 * @param amperage
	 */
	public Disjoncteur(String code, String nom, int nbPhases, String svg, int amperage) {
		super(code, nom, nbPhases, svg);
		this.amperage = amperage;
	}

}
