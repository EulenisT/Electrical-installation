package dao.exception;

import lombok.Getter;
import model.Ligne;

public class LigneBlocSizeException extends InstallationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Getter
	private Ligne ligne;

	public LigneBlocSizeException(String message, Ligne ligne) {
		super(message);
		this.ligne = ligne;
	}
}
