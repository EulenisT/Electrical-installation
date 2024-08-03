package model.appareil;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import model.IInstallationItems;

@Getter
@ToString(exclude = "svg")
@AllArgsConstructor
@EqualsAndHashCode
public class Appareil implements IInstallationItems {
	public enum Classe {
		APPAREIL, DISJONCTEUR, DISJONCTEURDIFF, INTERRUPTEUR, PRISE, LAMPE, COMPTEUR, TELERUPTEUR;

//permet d'identifier la Classe en fonction de la classe de l'objet AppareilPRISE
		public static Classe fromClasse(Appareil app) {
			return switch (app) {
			case Prise brol -> Classe.PRISE;
			case Interrupteur brol -> Classe.INTERRUPTEUR;
			case DisjoncteurDiff brol -> Classe.DISJONCTEURDIFF;
			case Disjoncteur brol -> Classe.DISJONCTEUR;
			case Lampe brol -> Classe.LAMPE;
			case Compteur brol -> Classe.COMPTEUR;
			case Telerupteur brol -> Classe.TELERUPTEUR;
			default -> Classe.APPAREIL;
			};
		}
	};

	private final String code;
	private final String nom;
	private final int nbPhases;
	private final String svg;
}
