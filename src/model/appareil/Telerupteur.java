package model.appareil;

import lombok.Getter;

@Getter
public class Telerupteur extends Appareil {
	private final int tension; 


	/**
	 * @param code
	 * @param nom
	 * @param nbPhases
	 * @param svg
	 */
	public Telerupteur(String code, String nom, int nbPhases, String svg, int tension) {
		super(code, nom, nbPhases, svg);
		this.tension = tension;
		
	}

}
