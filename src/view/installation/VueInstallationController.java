package view.installation;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import controller.MainController;
import dao.exception.InstallationException;
import javafx.collections.FXCollections;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Adresse;
import model.Installation;
import model.Installation.TypeLogement;
import services.Facade;

public class VueInstallationController implements Initializable {
	// pseudo classe pour les erreurs
	private final PseudoClass errorClass = PseudoClass.getPseudoClass("error");
	@FXML
	private Button btAnnuler;

	@FXML
	private Button btOk;

	@FXML
	private ComboBox<TypeLogement> cbType;

	@FXML
	private DatePicker pkDate;

	@FXML
	private TextField ztAdresse;

	@FXML
	private TextField ztCp;

	@FXML
	private TextField ztInstallateur;

	@FXML
	private TextField ztVille;

	private MainController ctrl;

	private Installation installation;

	private Facade facade;

	private Stage stage;

	private boolean nouveau;
	private ResourceBundle bundle;

	@FXML
	void actionAjout(ActionEvent event) {
		// Vérifie la validité des encodages
		boolean bad = checkData();

		if (!bad) {
			try {
				Adresse adr = new Adresse(ztAdresse.getText(), Integer.parseInt(ztCp.getText()), ztVille.getText());
				if (!nouveau) {
					installation.setAdresse(adr);
					installation.setDate(pkDate.getValue());
					installation.setTypeLogement(cbType.getValue());
					installation.setInstallateur(ztInstallateur.getText());

					facade.updateInstallation(installation);
					stage.hide();
				} else {// Nouvelle Installation
					installation = new Installation(pkDate.getValue(), ztInstallateur.getText(), adr,
							cbType.getValue());
					facade.createInstallation(installation);
					stage.hide();
				}

			} catch (InstallationException e) {
				ctrl.showErreur(e.getMessage());
			}
		}
	}

	@FXML
	void actionAnnuler(ActionEvent event) {
		stage.hide();
	}

	/**
	 * Vérifie la validité des champs et champ la pseudo-classe "errorClass" en
	 * fonction
	 * 
	 * @return true si les données sont correctes
	 */
	private boolean checkData() {
		boolean bad = false;
		boolean erreur;

		erreur = cbType.getValue() == null;
		cbType.pseudoClassStateChanged(errorClass, erreur);
		bad = bad || erreur;

		erreur = ztAdresse.getText().isBlank();
		ztAdresse.pseudoClassStateChanged(errorClass, erreur);
		bad = bad || erreur;

		erreur = ztVille.getText().isBlank();
		ztVille.pseudoClassStateChanged(errorClass, erreur);
		bad = bad || erreur;

		erreur = ztCp.getText().isBlank() || !estUnEntier(ztCp.getText());
		ztCp.pseudoClassStateChanged(errorClass, erreur);
		bad = bad || erreur;
		return bad;
	}

	private boolean estUnEntier(String text) {
		boolean res = false;
		try {
			Integer.parseInt(text);
			res = true;
		} catch (NumberFormatException e) {
		}

		return res;
	}

	public void setUp(MainController ctrl, Installation installation, Stage stage) {
		this.ctrl = ctrl;
		this.installation = installation;
		this.facade = ctrl.getFacade();
		this.stage = stage;
		this.nouveau = installation == null;
		if (installation != null) {
			btOk.setText(bundle.getString("maj"));
			// maj de la vue
			ztInstallateur.setText(installation.getInstallateur() == null ? "" : installation.getInstallateur());
			cbType.setValue(installation.getTypeLogement());
			pkDate.setValue(installation.getDate());
			ztAdresse.setText(installation.getAdresse().getRue());
			ztCp.setText(Integer.toString(installation.getAdresse().getCp()));
			ztVille.setText(installation.getAdresse().getVille());
		} else // nouveau composant
		{
			pkDate.setValue(LocalDate.now());
			cbType.setValue(TypeLogement.MAISON);
		}

	}

	@Override
	public void initialize(URL arg0, ResourceBundle bundle) {
		this.bundle = bundle;
		cbType.setItems(FXCollections.observableArrayList(TypeLogement.values()));
	}

}
