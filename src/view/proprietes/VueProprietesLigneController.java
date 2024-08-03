package view.proprietes;

import java.net.URL;
import java.util.ResourceBundle;

import controller.MainController;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import model.Ligne;
import model.appareil.Cable;
import model.appareil.CodeApp;
import model.appareil.Disjoncteur;
import services.Facade;

public class VueProprietesLigneController implements Initializable {
	@FXML
	private ComboBox<CodeApp> cbFusible;
	@FXML
	private ComboBox<CodeApp> cbCable;
	@FXML
	private CheckBox cbInterne;
	@FXML
	private TextField tfCode;

	private Facade facade;

	private Ligne ligne;

	@FXML
	public void addFusible(ActionEvent event) {
		if (cbFusible.getValue() != null)
			facade.setFusible(cbFusible.getValue().code());

	}

	@FXML
	public void addCable(ActionEvent event) {
		if (cbCable.getValue() != null)
			facade.setCable(cbCable.getValue().code());

	}

	@FXML
	public void addInterne(ActionEvent event) {
		boolean selected = cbInterne.isSelected();
		facade.setInterne(selected ? cbInterne.getText() : null);

		// Rapport sur le changement
		facade.objectHasChanged(ligne);
	}

	@FXML
	public void addCode(ActionEvent event) {
		String text = tfCode.getText();
		facade.setCode(text.isEmpty() ? null : text);

		// Rapport sur le changement
		facade.objectHasChanged(ligne);
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
		cbFusible.setItems(FXCollections.observableArrayList(facade.getListCodeDisjoncteur()));

	};

	/**
	 * 
	 * 
	 */
	public void showPropriete() {
		// si c'est la même installation on ne change rien
		// ici je teste juste les adresses des objets

		this.ligne = facade.getCurrentLigne();
		if (ligne != null) {

			Cable cable = ligne.getCable();
			if (cable != null) {
				CodeApp code = cbCable.getItems().filtered(c -> c.code().equals(cable.code())).getFirst();
				cbCable.setValue(code);
			} else
				cbCable.valueProperty().set(null);

			Disjoncteur fusible = ligne.getFusible();
			if (fusible != null) {
				CodeApp code = cbFusible.getItems().filtered(c -> c.code().equals(fusible.getCode())).getFirst();
				cbFusible.setValue(code);
			} else
				cbFusible.valueProperty().set(null);

			// CheckBox
			cbInterne.setSelected(ligne.isInterne());

			// TextField
			tfCode.setText(ligne.getCode() != null ? ligne.getCode() : "");
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

		cbFusible.setButtonCell(new ListCell<CodeApp>() {
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

		cbInterne.setOnAction(this::addInterne);

		tfCode.setOnAction(this::addCode);
		tfCode.textProperty().addListener((observable, oldValue, newValue) -> addCode(new ActionEvent()));

	}
}
