package model.appareil;

import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
public class DisjoncteurDiff extends Disjoncteur {
	@Getter
	private final int sensibilite;

	/**
	 * @param code
	 * @param nom
	 * @param nbPhases
	 * @param svg
	 * @param amperage
	 * @param sensibilite
	 */
	public DisjoncteurDiff(String code, String nom, int nbPhases, String svg, int amperage, int sensibilite) {
		super(code, nom, nbPhases, svg, amperage);
		this.sensibilite = sensibilite;
	}

}
