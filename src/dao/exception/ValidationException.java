package dao.exception;
/**
 * Problème de validation de données (hors PK)
 */
public class ValidationException extends InstallationException {
	
	private static final long serialVersionUID = 1L;
	
	private String champ;
	
	public ValidationException(String message, String champ) {
		super(message);
		this.champ = champ;
	}

	/**
	 * @return the champ
	 */
	public String getChamp() {
		return champ;
	}
}
