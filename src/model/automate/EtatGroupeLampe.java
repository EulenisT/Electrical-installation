package model.automate;

import lombok.AllArgsConstructor;
import model.automate.AutomateBloc.ETAT;

@AllArgsConstructor
public class EtatGroupeLampe implements Etat {

	private AutomateBloc automate;

	@Override
	public void actionIn() throws UnexpectedCharacterException {
		// caractère d'entrée (courant)
		int n = automate.getCar() - '0';// transforme la caractère 1..9 en un entier

		// prochain caractère
		char car = automate.next();
		ETAT nextState = switch (car) {
		case '^' -> {
			// crée les n lampes
			for (; n > 0; n--)
				automate.actionCreeLampe();
			yield ETAT.END;
		}

		default -> throw new UnexpectedCharacterException("Caractère invalide", car);
		};
		automate.nextState(nextState);
	}
}
