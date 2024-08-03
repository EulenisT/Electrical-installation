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
import model.GroupeLigne;
import model.appareil.CodeApp;
import model.appareil.DisjoncteurDiff;
import services.Facade;

public class VueProprietesGroupeLigneController implements Initializable {

	@FXML
	private ComboBox<CodeApp> cbDisjoncteurDiff;

	private Facade facade;

	private GroupeLigne groupeLigne;

	// Event Listener on ComboBox[#cbDisjoncteur].onAction
	@FXML
	public void addDisjoncteurDiff(ActionEvent event) {
		if (cbDisjoncteurDiff.getValue() != null)
			facade.setDisjoncteurDiff(cbDisjoncteurDiff.getValue().code());

	}

	/**
	 * Permet d'initialiser la vue lors du premier chargement
	 * 
	 * @param ctrl
	 * @param stage
	 */
	public void setUp(MainController ctrl) {
		this.facade = ctrl.getFacade();
		cbDisjoncteurDiff.setItems(FXCollections.observableArrayList(facade.getListCodeDisjoncteurDiff()));

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
		this.groupeLigne = facade.getCurrentGroupeLigne();
		if (groupeLigne != null) {

			DisjoncteurDiff disjoncteurDiff = groupeLigne.getDisjoncteur();
			if (disjoncteurDiff != null) {
				CodeApp code = cbDisjoncteurDiff.getItems().filtered(c -> c.code().equals(disjoncteurDiff.getCode()))
						.getFirst();
				cbDisjoncteurDiff.setValue(code);
			} else
				cbDisjoncteurDiff.valueProperty().set(null);

		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		cbDisjoncteurDiff.setButtonCell(new ListCell<CodeApp>() {
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

	}
}
