package model.automate;

import lombok.AllArgsConstructor;
import model.automate.AutomateBloc.ETAT;

@AllArgsConstructor
public class EtatInit implements Etat {
	private AutomateBloc automate;

	@Override
	public void actionIn() throws UnexpectedCharacterException {
		// car suivant
		char car = automate.next();
		ETAT nextState = switch (car) {
		case 'P' -> ETAT.P;
		case 'I' -> ETAT.I;
		case 'T' -> ETAT.T;
		default -> throw new UnexpectedCharacterException("Caractère invalide",car);
		};
		automate.nextState(nextState);
	}
}
