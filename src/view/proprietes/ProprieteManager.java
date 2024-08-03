package view.proprietes;

import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

import controller.MainController;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;
import model.IInstallationItems;
import services.Facade;
import services.ListMessages;
import services.SelectionModel;
import services.TypeOperation;
import services.ListMessages.Evenement;

@Slf4j
public class ProprieteManager implements Subscriber<ListMessages> {
	// endroit où doit charger la fenêtre de propriété
	private Pane pane;

	// Pane par défaut si rien à afficher
	private Pane paneVide;

	// accès au contrôleur principal
	private MainController ctrl;
	// facade
	private Facade facade;

	// Permet d'écouter les publications
	private Subscription subscription;

	// Les panes pour chaque type de propriétés
	private Pane paneInstallation;
	private VueProprietesInstallationController ctrlInstallation;

	// Les panes pour chaque type de propriétés
	private Pane paneGroupeLigne;
	private VueProprietesGroupeLigneController ctrlGroupeLigne;

	// Les panes pour chaque type de propriétés
	private Pane paneLigne;
	private VueProprietesLigneController ctrlLigne;

	/**
	 * @param pane
	 * @param ctrl
	 * @param facade
	 */
	public ProprieteManager(Pane pane, MainController ctrl) {
		this.pane = pane;
		this.ctrl = ctrl;
		this.facade = ctrl.getFacade();
		paneVide = new VBox();
		creePaneInstallation();
		creePaneGroupeLigne();
		creePaneLigne();

		// S'enregistre comme abonné
		facade.addObserver(this);
	}

	/**
	 * Permet de mettre la vue des propriétés de l'élément sélectionné dans le pane
	 * prévu à cet effet
	 * 
	 */
	public void showVuePropriete() {
		// pane à mettre
		Pane pane;

		// Vérifie si une installation existe
		if (facade.getSelectionModel().hasInstallation()) {
			// Quel élément est sélectionné?
			SelectionModel selection = facade.getSelectionModel();
			pane = switch (selection.getTypeElem()) {
			case INSTALLATION -> {
				// màj en fonction du composant
				ctrlInstallation.showPropriete();
				// retourne le pane adéquat
				yield paneInstallation;
			}
			case GROUPE -> {
				// maj en fonction du groupe
				ctrlGroupeLigne.showPropriete();
				yield paneGroupeLigne;

			}// TODO
			case LIGNE -> {
				// maj en fonction du groupe
				ctrlLigne.showPropriete();
				yield paneLigne;

			}// TODO
			case BLOC -> {
				yield paneVide;
			}// TODO
			case APPAREIL -> {
				yield paneVide;
			}// TODO
			default -> paneVide;
			};
			// retire le pane actuel et le remplace par celui sélectionné
			this.pane.getChildren().clear();
			this.pane.getChildren().add(pane);
		}
	}

	/**
	 * Crée la vue pour afficher les propriétés d'une installation
	 * 
	 */
	private void creePaneInstallation() {

		ctrlInstallation = null;
		paneInstallation = null;

		// Crée un loader pour charger la vue FXML
		FXMLLoader loader = new FXMLLoader(getClass().getResource("VueProprietesInstallation.fxml"));

		try {
			var bundle = ResourceBundle.getBundle("view.proprietes.bundle.VueProprietes");
			loader.setResources(bundle);
		} catch (Exception e) {
			log.error("Imposible de charger le buddle pour la vue Propriete" + e.getMessage());
		}
		// Charge la vue à partir du Loader
		// et initialise son contenu en appelant la méthode setUp du controleur

		try {
			paneInstallation = loader.load();
			// récupère le ctrl (après l'initialisation)
			ctrlInstallation = loader.getController();
			// fourni l'accès aux données au ctrl
			ctrlInstallation.setUp(ctrl);
		} catch (IOException e) {
			log.error("Imposible de charger la vue Propriete Installation");
			ctrl.showErreur("Impossible de charger la vue Propriété Installation: " + e.getMessage());
			paneInstallation = null;
			ctrlInstallation = null;
		}
	}

	/**
	 * Crée la vue pour afficher les propriétés de groupeLigne
	 * 
	 */
	private void creePaneGroupeLigne() {

		ctrlGroupeLigne = null;
		paneGroupeLigne = null;

		// Crée un loader pour charger la vue FXML
		FXMLLoader loader = new FXMLLoader(getClass().getResource("VueProprietesGroupeLigne.fxml"));

		try {
			var bundle = ResourceBundle.getBundle("view.proprietes.bundle.VueProprietes");
			loader.setResources(bundle);
		} catch (Exception e) {
			log.error("Imposible de charger le buddle pour la vue Propriete" + e.getMessage());
		}
		// Charge la vue à partir du Loader
		// et initialise son contenu en appelant la méthode setUp du controleur

		try {
			paneGroupeLigne = loader.load();
			// récupère le ctrl (après l'initialisation)
			ctrlGroupeLigne = loader.getController();
			// fourni l'accès aux données au ctrl
			ctrlGroupeLigne.setUp(ctrl);
		} catch (IOException e) {
			log.error("Imposible de charger la vue Propriete GroupeLigne");
			ctrl.showErreur("Impossible de charger la vue Propriété GroupeLigne: " + e.getMessage());
			paneGroupeLigne = null;
			ctrlGroupeLigne = null;
		}
	}

	/**
	 * Crée la vue pour afficher les propriétés de Ligne
	 * 
	 */
	private void creePaneLigne() {

		ctrlLigne = null;
		paneLigne = null;

		// Crée un loader pour charger la vue FXML
		FXMLLoader loader = new FXMLLoader(getClass().getResource("VueProprietesLigne.fxml"));

		try {
			var bundle = ResourceBundle.getBundle("view.proprietes.bundle.VueProprietes");
			loader.setResources(bundle);
		} catch (Exception e) {
			log.error("Imposible de charger le buddle pour la vue Propriete" + e.getMessage());
		}
		// Charge la vue à partir du Loader
		// et initialise son contenu en appelant la méthode setUp du controleur

		try {
			paneLigne = loader.load();
			// récupère le ctrl (après l'initialisation)
			ctrlLigne = loader.getController();
			// fourni l'accès aux données au ctrl
			ctrlLigne.setUp(ctrl);
		} catch (IOException e) {
			log.error("Imposible de charger la vue Propriete Ligne");
			ctrl.showErreur("Impossible de charger la vue Propriété Ligne: " + e.getMessage());
			paneLigne = null;
			ctrlLigne = null;
		}
	}

	/****************** GESTION DE LA PUBLICATION ********************/

	// Gestion des publications
	// appellé lors de l'enregistrement on reçoit alors une subscription
	@Override
	public void onSubscribe(Subscription subscription) {
		this.subscription = subscription;
		subscription.request(10);
		log.info("propriété Manager: Je suis un écouteur");
	}

	/*
	 * Reception des messages
	 * 
	 * gestion du rafraichissement des propriétés
	 * 
	 */
	@Override
	public void onNext(ListMessages messages) {
		// Je m'intéresse aux évènements de sélection sur n'importe quelle classe
		List<Evenement> liste = messages.EventForTypeOperation(TypeOperation.SELECTION);
		// vérifie s'il existe un évènement de selection
		if (!liste.isEmpty() && liste.getFirst().element() instanceof IInstallationItems objet) {
			// ici obj représente l'objet sélectionné
			log.info("Event: Selection sur " + objet.toString());
			Platform.runLater(() -> this.showVuePropriete());
		}

		subscription.request(1);
	}

	@Override
	public void onError(Throwable throwable) {
		log.error("erreur d'abonnement aux évènements ds Propriété Manager");

	}

	@Override
	public void onComplete() {
		log.info(" écouteur de selection: On Complete");
	}

	// Permet d'avoir la "Subscription pour se désabonner"
	public Subscription getSubscription() {
		return subscription;
	}

	/****************************
	 * fin subscriber
	 ************************************/
}
