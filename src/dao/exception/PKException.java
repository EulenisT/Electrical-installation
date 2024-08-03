package dao.exception;

public class PKException extends InstallationException {
	/**
	 * Problème de clé primaine
	 */
	private static final long serialVersionUID = 1L;
	
	private final String id;// la clé de la PK en erreur

	public PKException(String message, String id) {
		super(message+": "+ id);
		this.id = id;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
}
