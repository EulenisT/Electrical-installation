package model.automate;


import lombok.AllArgsConstructor;
import model.automate.AutomateBloc.ETAT;

@AllArgsConstructor
public class EtatTelerupteur implements Etat {
	private AutomateBloc automate;

	@Override
	public void actionIn() throws UnexpectedCharacterException {

		// prochain caractère
		char car = automate.next();
		ETAT nextState = switch (car) {
		case 'P' -> {
			automate.actionCreeTelerupteur();
			yield ETAT.P;
		}
		case 'L' -> {
			automate.actionCreeTelerupteur();
			yield ETAT.L;
		}
		
		default -> throw new UnexpectedCharacterException("Caractère invalide",car);
		};
		automate.nextState(nextState);
	}

}
