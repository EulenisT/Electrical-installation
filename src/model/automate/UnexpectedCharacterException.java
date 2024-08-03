package model.automate;

import lombok.Getter;

@Getter
public class UnexpectedCharacterException extends Exception {

	private char car;
	private static final long serialVersionUID = 1L;

	/**
	 * @param car
	 */
	public UnexpectedCharacterException(String message, char car) {
		super(message);
		this.car = car;
	}
}
