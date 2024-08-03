package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Lors d'une publication, on enverra un objet ListMessage qui contiendra tous
 * les évènements qui se sont produits en spécifiant la classe concernée, l'objet modifié et le type
 * d'opération
 * 
 * @author Didier
 *
 */
public class ListMessages {

	/**
	 * 
	 * @author Didier Type des objets envoyés
	 */
	public enum Classe {
		INSTALLATION, GROUPELIGNE, LIGNE, BLOC, APPAREIL
	}

	/**
	 * 
	 * @author Didier 
	 * un évènement sur un objet avec l'objet et
	 *  l'opération sur cet objet
	 */

	public static record Evenement(TypeOperation op, Object element) {
	}

	/**
	 * La liste des opérations effectuées
	 */
	private Map<Classe, Evenement> messages;

	/**
	 * Crée une map de messages (un maximum par classe)
	 */
	public ListMessages(Map<Classe, Evenement> messages) {
		this.messages = messages;
	}

	/**
	 * retourne une liste non modifiable des évènements
	 * 
	 * @return liste non modifiable des évènements
	 */
	public Collection<Evenement> getListe() {

		return Collections.unmodifiableCollection(messages.values());
	}

	/**
	 * Permet de savoir si la liste contient un évènement sur une classe
	 * 
	 * @param classe
	 * @return vrai s'il existe un évènement sur cette classe
	 */
	public boolean contientEventFromClasse(Classe classe) {
		return messages.containsKey(classe);
	}

	/**
	 * Renvoie l'évènement lié à un type de classe
	 * 
	 * @param classe
	 * @return un optional d'évènement
	 */
	public Optional<Evenement> getEventFromClasse(Classe classe) {
		return Optional.ofNullable(messages.get(classe));
	}

	/**
	 * Indique le nombre de messages concernant une opération spécifique
	 * @param op type d'opération
	 * @return nombre d'évènements
	 */
	public int hasTypeOperation(TypeOperation op) {
		int cpt = 0;
		for (var msg : messages.values()) {
			if (msg.op == op)
				cpt++;
		}
		return cpt;
	}

	/**
	 * Retourne une liste avec les Evènements concernant une opération spécifique
	 * @param op type d'opération
	 * @return Liste d'évènements associés
	 */
	public List<Evenement> EventForTypeOperation(TypeOperation op) {
		List<Evenement> liste = new ArrayList<>();
		for (var msg : messages.values()) {
			if (msg.op == op)
				liste.add(msg);
		}
		return liste;
	}

}
