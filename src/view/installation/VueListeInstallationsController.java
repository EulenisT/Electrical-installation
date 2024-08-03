package view.installation;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;

import javafx.scene.control.TableView;
import javafx.stage.Stage;
import model.RInstallation;
import services.Facade;
import javafx.scene.control.TableColumn;

public class VueListeInstallationsController implements Initializable {
	@FXML
	private TableView<RInstallation> tblInstallations;
	@FXML
	private TableColumn<RInstallation, Integer> colId;
	@FXML
	private TableColumn<RInstallation, LocalDate> colDate;
	@FXML
	private TableColumn<RInstallation, String> colAdresse;
	@FXML
	private TableColumn<RInstallation, Integer> colCp;
	@FXML
	private TableColumn<RInstallation, String> colCommune;
	@FXML
	private Button btCharger;

	private Stage stage;
	private Facade facade;

	// Event Listener on Button.onAction
	@FXML
	public void annuler(ActionEvent event) {
		stage.close();
	}

	// Event Listener on Button[#btCharger].onAction
	@FXML
	public void charger(ActionEvent event) {
		RInstallation inst = tblInstallations.getSelectionModel().getSelectedItem();
		if (inst != null) {
			facade.chargeInstallation(inst.id());
			stage.close();
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		colId.setCellValueFactory(p -> new SimpleIntegerProperty(p.getValue().id()).asObject());
		colDate.setCellValueFactory(p -> new SimpleObjectProperty<LocalDate>(p.getValue().date()));
		colAdresse.setCellValueFactory(p -> new ReadOnlyStringWrapper(p.getValue().adresse()));
		colCp.setCellValueFactory(p -> new SimpleIntegerProperty(p.getValue().cp()).asObject());
		colCommune.setCellValueFactory(p -> new ReadOnlyStringWrapper(p.getValue().commune()));
		// Single selection
		tblInstallations.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

	}

	public void setUp(Facade facade, Stage stage) {
		this.stage = stage;
		this.facade = facade;
		tblInstallations.setItems(FXCollections.observableArrayList(facade.getListeInstallations()));
		facade.getListeInstallations();

	}
}
