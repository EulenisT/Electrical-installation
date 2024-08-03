package view.proprietes;

import java.net.URL;
import java.util.ResourceBundle;

import controller.MainController;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import model.Installation;
import model.appareil.Cable;
import model.appareil.CodeApp;
import model.appareil.Compteur;
import model.appareil.Disjoncteur;
import services.Facade;

public class VueProprietesInstallationController implements Initializable {
	@FXML
	private ComboBox<CodeApp> cbCompteur;
	@FXML
	private ComboBox<CodeApp> cbDisjoncteur;
	@FXML
	private ComboBox<CodeApp> cbCable;

	private Facade facade;

	private Installation installation;

	// Event Listener on ComboBox[#cbCompteur].onAction
	@FXML
	public void addCompteur(ActionEvent event) {
		if (cbCompteur.getValue() != null)
			facade.setCompteur(cbCompteur.getValue().code());

	}

	// Event Listener on ComboBox[#cbDisjoncteur].onAction
	@FXML
	public void addDisjoncteur(ActionEvent event) {
		if (cbDisjoncteur.getValue() != null)
			facade.setDisjoncteur(cbDisjoncteur.getValue().code());

	}

	// Event Listener on ComboBox[#cbCable].onAction
	@FXML
	public void addCable(ActionEvent event) {
		if (cbCable.getValue() != null)
			facade.setCable(cbCable.getValue().code());
	}

	/**
	 * Permet d'initialiser la vue lors du premier chargement
	 * 
	 * @param ctrl
	 * @param stage
	 */
	public void setUp(MainController ctrl) {
		this.facade = ctrl.getFacade();
		cbCable.setItems(FXCollections.observableArrayList(facade.getListCodeCable()));
		cbDisjoncteur.setItems(FXCollections.observableArrayList(facade.getListCodeDisjoncteur()));
		cbCompteur.setItems(FXCollections.observableArrayList(facade.getListCodeCompteur()));
	};

	/**
	 * 
	 * 
	 */
	public void showPropriete() {
		// si c'est la même installation on ne change rien
		// ici je teste juste les adresses des objets
//		if (this.installation == facade.getCurrentInstallation())
//			return;
		this.installation = facade.getCurrentInstallation();
		if (installation != null) {
			Cable cable = installation.getCable();
			if (cable != null) {
				CodeApp code = cbCable.getItems().filtered(c -> c.code().equals(cable.code())).getFirst();
				cbCable.setValue(code);
			} else
				cbCable.valueProperty().set(null);

			Disjoncteur disjoncteur = installation.getDisjoncteurCompagnie();
			if (disjoncteur != null) {
				CodeApp code = cbDisjoncteur.getItems().filtered(c -> c.code().equals(disjoncteur.getCode()))
						.getFirst();
				cbDisjoncteur.setValue(code);
			} else
				cbDisjoncteur.valueProperty().set(null);

			// Compteur
			Compteur compteur = installation.getCompteur();
			if (compteur != null) {
				CodeApp code = cbCompteur.getItems().filtered(c -> c.code().equals(compteur.getCode())).getFirst();
				cbCompteur.setValue(code);
			} else
				cbCompteur.valueProperty().set(null);
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		cbCable.setButtonCell(new ListCell<CodeApp>() {
			@Override
			protected void updateItem(CodeApp t, boolean empty) {
				super.updateItem(t, empty);
				if (empty) {
					// setText("");
				} else {
					setText(t.code());
				}
			}
		});
//		cbCable.setConverter(new StringConverter<CodeApp>() {
//			@Override
//			public String toString(CodeApp app) {
//				if (app != null)
//					return app.code();
//				return "";
//			}
//
//			@Override
//			public CodeApp fromString(String str) {
//				// sera jamais appelé car non modifiable
//				return cbCable.getItems().stream().filter(c -> c.code().equals(str)).findFirst()
//						.orElse(cbCable.getItems().getFirst());
//			}
//		});

		cbDisjoncteur.setButtonCell(new ListCell<CodeApp>() {
			@Override
			protected void updateItem(CodeApp t, boolean empty) {
				super.updateItem(t, empty);
				if (empty) {
					// setText("");
				} else {
					setText(t.code());
				}
			}
		});

		cbCompteur.setButtonCell(new ListCell<CodeApp>() {
			@Override
			protected void updateItem(CodeApp t, boolean empty) {
				super.updateItem(t, empty);
				if (empty) {
					// setText("");
				} else {
					setText(t.code());
				}
			}
		});
//		cbCable.setConverter(
//			    new StringConverter<CodeApp>() {
//			        
//			        @Override
//			        public String toString(CodeApp app) {
//			            if (app != null) 
//			                return app.code();
//			             else 
//			                return "";
//			        }
//
//			        @Override
//			        public CodeApp fromString(String str) {
//			        //sera pas appelé car non editable
//			            return cbCable.getItems().filtered(c->c.code().equals(str)).getFirst();
//			           
//			        }
//			    });

	}
}
