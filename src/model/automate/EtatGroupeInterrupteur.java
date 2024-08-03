package model.automate;

import lombok.AllArgsConstructor;
import model.automate.AutomateBloc.ETAT;

@AllArgsConstructor
public class EtatGroupeInterrupteur implements Etat {

	private AutomateBloc automate;

	@Override
	public void actionIn() throws UnexpectedCharacterException {
		// caractère d'entrée (courant)
		int n = automate.getCar() - '0';// transforme la caractère 1..9 en un entier

		// prochain caractère
		char car = automate.next();
		ETAT nextState = switch (car) {
		case 'P' -> {
			// crée les interrupteurs
			createInterrupteurs(n);
			yield ETAT.P;
		}
		case 'L' -> {
			createInterrupteurs(n);
			yield ETAT.L;
		}

		default -> throw new UnexpectedCharacterException("Caractère invalide", car);
		};
		automate.nextState(nextState);
	}

	private void createInterrupteurs(int n) {
		assert n > 0;
		int nbInverseurs = n > 2 ? n - 2 : 0;
		switch (n) {
		case 1 -> automate.actionCreeInterrupteur("IC1");
		case 2 -> {
			automate.actionCreeInterrupteur("IV1");
			automate.actionCreeInterrupteur("IV1");
		}
		default -> {
			automate.actionCreeInterrupteur("IV1");
			for (; nbInverseurs > 0; nbInverseurs--)
				automate.actionCreeInterrupteur("II1");
			automate.actionCreeInterrupteur("IV1");
		}
		}
	}
}
