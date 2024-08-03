package model;

import java.util.ArrayList;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import model.appareil.Appareil;
/**
 * Un bloc correspond à un point d'une ligne
 */
@EqualsAndHashCode(exclude = {"appareils"})
public class Bloc implements IInstallationItems {

	@Getter
	final Integer id;//identifiant d'un bloc généré par la ligne  idLigne||1 chiffre
	
	//Permet via le getter de rajouter des appareils à un bloc
	@Getter
	private List<Appareil> appareils = new ArrayList<>();

	/**
	 * Utilisé pour chargé un bloc existant
	 * sinon sera appelé par la ligne via la méthode uneLigne.addBloc()
	 * @param id identifiant 0<= id%10 <8
	 * ne peut pas être null
	 */
	public Bloc(Integer id) {
		assert id != null && id % 10 >= 0 && id % 10 < 8;
		this.id = id;
	}

	@Override
	public String toString() {
		StringBuilder chaine = new StringBuilder();
		for (Appareil app : appareils) {
			chaine.append("->" + app.getCode());
		}
		return chaine.toString();
	}

}
