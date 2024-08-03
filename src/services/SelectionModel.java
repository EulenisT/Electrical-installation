package services;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import model.Bloc;
import model.GroupeLigne;
import model.IInstallationItems;
import model.Installation;
import model.Ligne;
import model.appareil.Appareil;
import services.ListMessages.Classe;
import services.ListMessages.Evenement;

/**
 * @author Didier
 * Permet de maintenir l'élément sélectionné
 */
public class SelectionModel {
	// permet de préciser le type de l'élément sélectionné
	public enum TYPE_ELEM {
		NONE, INSTALLATION, GROUPE, LIGNE, BLOC, APPAREIL
	};

	@Getter
	private Installation currentInstallation = null;
	@Getter
	private GroupeLigne currentGroupe = null;
	@Getter
	private Ligne currentLigne = null;
	@Getter
	private Bloc currentBloc = null;
	@Getter
	private Appareil currentAppareil = null;
	@Getter
	private TYPE_ELEM typeElem;

	// Pour publier l'élément sélectionné
	private PublisherEvent publisher;

	/**
	 * @param publisher 
	 * @param currentInstallation
	 */
	public SelectionModel(PublisherEvent publisher) {
		typeElem = TYPE_ELEM.NONE;
		this.publisher = publisher;
	}

	/**
	 * Aucun élément est sélectionné,
	 *  l'installation sera l'élément sélectionné
	 */
	public void setInstallation(Installation installation) {
		if (installation == null)
			return;
		reset();
		this.currentInstallation = installation;
		typeElem = TYPE_ELEM.INSTALLATION;

		// publie l'évènement
		publie(Classe.INSTALLATION, currentInstallation);
	}

	/**
	* Permet de savoir si une installation est bien chargée
	* @return
	*/
	public boolean hasInstallation() {

		return this.currentInstallation != null;
	}

	/**
	 * pas d'élément sélectionné
	 */
	public void setNoSelection() {
		reset();
		typeElem = TYPE_ELEM.NONE;
	}

	/**
	 * permet de savoir si un élément est sélectionné
	 * @return
	 */
	public boolean hasSelectedItem() {
		return !TYPE_ELEM.NONE.equals(typeElem);
	}

	/**
	 * Permet de récupérer l'objet sélectionné
	 * @return
	 */
	public IInstallationItems getSelectedItem() {
		IInstallationItems elem = switch (typeElem) {
		case TYPE_ELEM.GROUPE -> currentGroupe;
		case TYPE_ELEM.LIGNE -> currentLigne;
		case TYPE_ELEM.BLOC -> currentBloc;
		case TYPE_ELEM.APPAREIL -> currentAppareil;
		case TYPE_ELEM.INSTALLATION -> currentInstallation;
		default -> null;
		};
		return elem;
	}

	/**
	* @param currentGroupe the currentGroupe to set
	*/
	public void setCurrentGroupe(GroupeLigne currentGroupe) {
		assert currentGroupe != null : " le groupe ne peut pas être à null";
		if (typeElem != TYPE_ELEM.GROUPE || currentGroupe != this.currentGroupe) {
			reset();
			this.currentGroupe = currentGroupe;
			typeElem = TYPE_ELEM.GROUPE;

			publie(Classe.GROUPELIGNE, currentGroupe);
		}
	}

	/**
	 * @param currentLigne the currentLigne to set
	 */
	public void setCurrentLigne(Ligne currentLigne) {
		assert currentLigne != null : " la ligne ne peut pas être à null";
		if (typeElem != TYPE_ELEM.LIGNE || currentLigne != this.currentLigne) {
			reset();
			this.currentLigne = currentLigne;
			typeElem = TYPE_ELEM.LIGNE;
			// publie l'évènement
			publie(Classe.LIGNE, currentLigne);
		}
	}

	/**
	 * @param currentBloc the currentBloc to set
	 */
	public void setCurrentBloc(Bloc currentBloc) {
		assert currentBloc != null : " le bloc ne peut pas être à null";
		if (typeElem != TYPE_ELEM.BLOC || currentBloc != this.currentBloc) {
			reset();
			this.currentBloc = currentBloc;
			typeElem = TYPE_ELEM.BLOC;
			// publie l'évènement
			publie(Classe.BLOC, currentBloc);
		}
	}

	/**
	 * @param currentAppareil the currentAppareil to set
	 */
	public void setCurrentAppareil(Appareil currentAppareil) {
		assert currentAppareil != null : " l'appareil ne peut pas être à null";
		if (typeElem != TYPE_ELEM.APPAREIL || currentAppareil != this.currentAppareil) {
			reset();
			this.currentAppareil = currentAppareil;
			typeElem = TYPE_ELEM.APPAREIL;
			// publie l'évènement
			publie(Classe.APPAREIL, currentAppareil);
		}
	}

	private void reset() {
		currentGroupe = null;
		currentLigne = null;
		currentBloc = null;
		currentAppareil = null;
	}

	/**
		 * Publie l'évènement de sélection
		 * @param typeEvent
		 * @param objet
		 */
	private void publie(Classe typeEvent, IInstallationItems objet) {
		Map<Classe, Evenement> messages = new HashMap<>();
		messages.put(typeEvent, new Evenement(TypeOperation.SELECTION, objet));
		publisher.submit(new ListMessages(messages));
	}
}
