package model.automate;

import lombok.AllArgsConstructor;
import model.automate.AutomateBloc.ETAT;

@AllArgsConstructor
public class EtatLampe implements Etat {
	private AutomateBloc automate;

	@Override
	public void actionIn() throws UnexpectedCharacterException {

		// prochain caractère
		char car = automate.next();
		ETAT nextState = switch (car) {
		case '1', '2', '3', '4', '5', '6', '7', '8', '9' -> ETAT.GL;
		case '^' -> {
			automate.actionCreeLampe();
			yield ETAT.END;
		}
		default -> throw new UnexpectedCharacterException("Caractère invalide", car);
		};
		automate.nextState(nextState);
	}

}
