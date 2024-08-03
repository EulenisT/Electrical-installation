package controller;

import java.io.IOException;
import java.sql.Connection;
import java.util.Optional;
import java.util.ResourceBundle;

import dao.DAOFactory;
import dao.DAOFactory.TypePersistance;
import databases.connexion.ConnexionFromFile;
import databases.connexion.ConnexionSingleton;
import databases.uri.Databases;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import model.Installation;
import services.Facade;
import view.installation.VueInstallationController;
import view.installation.VueListeInstallationsController;
import view.proprietes.ProprieteManager;
import view.tree.TreeBuilder;
import view.utils.I18N;

@Slf4j
public class MainController extends Application {

//Facade
	private Facade facade;
//Factory
	private DAOFactory factory;
//La treeView
	private TreeBuilder treeB;
//MainStage
	private Stage mainStage;
//conteneur principal	
	private BorderPane cp;
//pour définir les propriétés
	private VBox rightPane;

//Lancement de l'application
	@Override
	public void start(Stage mainStage) {
		// mémorise la fénêtre principale
		this.mainStage = mainStage;

		/*
		 * connexion à la base de données
		 * création de la fabrique
		*/
		factory = connexionToDatabase();

		// création de la facade
		facade = new Facade(factory);

		// Conteneur principal
		cp = new BorderPane();

		// Charge le menu
		MenuBar menuBar = getMenu();
		cp.setTop(menuBar);

		// Liste de boutons
		VBox leftPane = new VBox();

		// bt1 Charge inst
		Button bt1 = new Button("Charger une installation ");
		bt1.setOnAction(ev -> {
			//facade.chargeInstallation(1);
			showVueListeInstallations(facade);
		});


		// bt3 new
		Button bt3 = new Button("Nouvelle installation");
		bt3.setOnAction(this::actionNouvelleInstallation);

		// bt4 modifie
		Button bt4 = new Button("Modifie installation");
		bt4.setOnAction(this::actionModifInstallation);
		
		// bt5 save installation 
		Button bt5 = new Button("Save installation");
		bt5.setOnAction(ev -> {
			try {
				facade.saveInstallation();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});

		leftPane.getChildren().addAll(bt1, bt3, bt4, bt5);
		cp.setLeft(leftPane);

		/*
		 * Pane de droite pour les propriétés des éléments
		 */
		rightPane = new VBox(5);
		rightPane.setMinWidth(200);
		rightPane.setId("right-pane");
		Label lbl = new Label(I18N.getString("propriete"));
		VBox paneProprietes = new VBox();
		rightPane.getChildren().addAll(lbl, paneProprietes);
		lbl.getStyleClass().add("labelTitre");
		cp.setRight(rightPane);

		// Création d'un Propriété Manager qui affichera les propriétés de l'élément
		// sélectionné
		new ProprieteManager(paneProprietes, this);

		// Création d'une treeView qui sera maj en faisant reBuildTree
		treeB = new TreeBuilder(this);
		cp.setCenter(treeB.getTree());

		// Scene et show et css
		Scene scene = new Scene(cp, 900, 400);
		scene.getStylesheets().add("./view/css/installation.css");
		mainStage.setScene(scene);
		mainStage.setTitle(I18N.getString("projet"));
		mainStage.show();
	}

	/**
	 * Connexion à la base de données
	 * sur base du contenu du fichier
	 * "connexion_InstallationsElectriques.properties"
	 */
	private DAOFactory connexionToDatabase() {
		DAOFactory factory = null;
		// Connexion à la BD
		try {
			ConnexionSingleton.setInfoConnexion(new ConnexionFromFile(
					"./ressources/connexion_InstallationsElectriques.properties", Databases.FIREBIRD));
			Connection connect = ConnexionSingleton.getConnexion();
			log.info("Connexion établie");
			// Crée la factory Firebird
			factory = DAOFactory.getDAOFactory(TypePersistance.FIREBIRD, connect);

		} catch (Exception e) {
			showErreur("Problème de connexion");
			// Quitte l'application
			Platform.exit();
		}

		return factory;
	}

	/**
	 * Crée la barre de menu
	 * @return
	 */
	private MenuBar getMenu() {
		// MenuBar
		MenuBar menuBar = new MenuBar();
		Menu menuInstallation = new Menu(I18N.getString("installation"));
		menuBar.getMenus().add(menuInstallation);
		// items
		MenuItem item1Inst = new MenuItem(I18N.getString("charger"));

		item1Inst.setGraphic(new ImageView("file:.//ressources//images//open.png"));
		item1Inst.setOnAction(e -> {
			TextInputDialog choix = new TextInputDialog("");
			choix.setContentText(I18N.getString("inst.id"));
			Optional<String> oTxt = choix.showAndWait();
			oTxt.ifPresent((txt) -> {
				try {
					int i = Integer.parseInt(txt);
					var oInst = facade.chargeInstallation(i);
					oInst.ifPresentOrElse((inst) -> log.info("Installation chargée"), () -> {
						log.error("Installation inexistante");
						showErreur(I18N.getString("inst.existePas"));
					});
				} catch (NumberFormatException e1) {
					showErreur(I18N.getString("err.nbr"));
				}

			});
		});
		MenuItem item2Inst = new MenuItem(I18N.getString("sauver"));
		item2Inst.setGraphic(new ImageView("file:.//ressources//images//save.png"));
		MenuItem item3Inst = new MenuItem(I18N.getString("créer"));
		item3Inst.setGraphic(new ImageView("file:.//ressources//images//new.png"));
		item3Inst.setOnAction(this::actionNouvelleInstallation);
		menuInstallation.getItems().addAll(item1Inst, item2Inst, item3Inst);
		return menuBar;
	}

	// Actions
	void actionNouvelleInstallation(ActionEvent event) {
		showVueInstallation(facade, null);
	}

	void actionModifInstallation(ActionEvent event) {
		if (facade.getCurrentInstallation() != null)
			showVueInstallation(facade, facade.getCurrentInstallation());
		else
			showErreur(I18N.getString("inst.non"));
	}

	/**
	 * Permet de modifier ou créer une nouvelle installation
	 * 
	 * @param facade
	 * @param installation avec un id (modif), sinon vide
	 */
	private void showVueInstallation(Facade facade, Installation installation) {
		// bundle
		ResourceBundle bundle;
		// Crée une stage
		Stage stage = new Stage();
		// indique sa stage parent
		stage.initOwner(mainStage);
		stage.initModality(Modality.WINDOW_MODAL);
		stage.setX(100);
		stage.setY(50);

		// Crée un loader pour charger la vue FXML
		FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/installation/VueInstallation.fxml"));
		try {
			bundle = ResourceBundle.getBundle("view.Installation.bundle.VueInstallation");
			loader.setResources(bundle);
			// Obtenir la traduction du titre dans la locale
			stage.setTitle(bundle.getString("titre"));
		} catch (Exception e) {
			log.error("Imposible de charger le buddle pour la vue Installation" + e.getMessage());
			showErreur("Impossible de charger le buddle ");
			stage.setTitle("Vue Installation");
		}
		// Charge la vue à partir du Loader
		// et initialise son contenu en appelant la méthode setUp du controleur
		AnchorPane root;
		try {
			root = loader.load();
			// récupère le ctrl (après l'initialisation)
			VueInstallationController ctrl = loader.getController();
			// fourni la fabrique au ctrl pour charger les données
			ctrl.setUp(this, installation, stage);
			// charge le Pane dans la Stage
			Scene scene = new Scene(root);
			scene.getStylesheets().add("./view/css/installation.css");
			stage.setScene(scene);
			stage.showAndWait();
		} catch (IOException e) {
			log.error("Imposible de charger la vue Installation");
			showErreur("Impossible de charger la vue Installation: " + e.getMessage());
			stage = null;
		}

	}
	
	/**
	 * Permet de modifier ou créer une nouvelle installation
	 * 
	 * @param facade
	 * @param installation avec un id (modif), sinon vide
	 */
	private void showVueListeInstallations(Facade facade) {
		// bundle
		ResourceBundle bundle;
		// Crée une stage
		Stage stage = new Stage();
		// indique sa stage parent
		stage.initOwner(mainStage);
		stage.initModality(Modality.WINDOW_MODAL);
		stage.setX(100);
		stage.setY(50);

		// Crée un loader pour charger la vue FXML
		FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/installation/VueListeInstallations.fxml"));
		try {
			bundle = ResourceBundle.getBundle("view.Installation.bundle.VueInstallation");
			loader.setResources(bundle);
			// Obtenir la traduction du titre dans la locale
			stage.setTitle(bundle.getString("titre"));
		} catch (Exception e) {
			log.error("Imposible de charger le buddle pour la vue Installation" + e.getMessage());
			showErreur("Impossible de charger le buddle ");
			stage.setTitle("Vue Liste Installations");
		}
		// Charge la vue à partir du Loader
		// et initialise son contenu en appelant la méthode setUp du controleur
		AnchorPane root;
		try {
			root = loader.load();
			// récupère le ctrl (après l'initialisation)
			VueListeInstallationsController ctrl = loader.getController();
			// fourni la fabrique au ctrl pour charger les données
			ctrl.setUp(facade, stage);
			// charge le Pane dans la Stage
			Scene scene = new Scene(root);
			scene.getStylesheets().add("./view/css/installation.css");
			stage.setScene(scene);
			stage.showAndWait();
		} catch (IOException e) {
			log.error("Imposible de charger la vue Liste Installations");
			showErreur("Impossible de charger la vue Liste Installations: " + e.getMessage());
			stage = null;
		}

	}
	
	

	/**
	 * Getter de la facade
	 * @return
	 */
	public Facade getFacade() {
		return facade;
	}
	
	/**
	 * Boite de confirmation
	 * @param message
	 * @return
	 */
	public boolean showConfirmation(String message) {
		Alert a = new Alert(AlertType.CONFIRMATION, message);
		Optional<ButtonType> result = a.showAndWait();
		return result.get() == ButtonType.OK;
	}

	/**
	 * Vue pour afficher les messages d'erreur
	 * 
	 * @param message
	 */
	public void showErreur(String message) {
		Alert a = new Alert(AlertType.ERROR, message);
		a.showAndWait();
	}

	/**
	 * Point d'entrée principal de l'application
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}

}
