package model.automate;

import lombok.AllArgsConstructor;
import model.automate.AutomateBloc.ETAT;

@AllArgsConstructor
public class EtatPrise implements Etat {
	private AutomateBloc automate;

	@Override
	public void actionIn() throws UnexpectedCharacterException {

		// prochain caractère
		char car = automate.next();
		ETAT nextState = switch (car) {
		case '1', '2', '3', '4', '5' -> ETAT.GP;
		case '^' -> {
			automate.actionCreePrise();
			yield ETAT.END;
		}
		default -> throw new UnexpectedCharacterException("Caractère invalide", car);
		};
		automate.nextState(nextState);
	}
}
