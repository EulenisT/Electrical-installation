package services;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Flow.Subscriber;

import dao.DAOFactory;
import dao.exception.InstallationException;
import dao.exception.LigneBlocSizeException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import model.Bloc;
import model.GroupeLigne;
import model.IInstallationItems;
import model.Installation;
import model.Ligne;
import model.RInstallation;
import model.appareil.Appareil;
import model.appareil.CodeApp;
import model.appareil.Compteur;
import model.appareil.Disjoncteur;
import model.appareil.DisjoncteurDiff;
import model.automate.AutomateBloc;
import model.automate.UnexpectedCharacterException;
import services.ListMessages.Classe;
import services.ListMessages.Evenement;

@Slf4j
public class Facade {

	// Accès aux dao
	private DAOFactory factory;

	// Le publisher
	private PublisherEvent publisher;

	// Installation chargée
	@Getter
	private SelectionModel selectionModel;

	// Automate
	private AutomateBloc automate;

	/**
	 * @param factory
	 */
	public Facade(DAOFactory factory) {
		// La fabrique pour accéder aux DAOs
		this.factory = factory;

		// Le Publisher pour publier les évènements
		publisher = new PublisherEvent();

		// maintient de l'élément sélectionné
		selectionModel = new SelectionModel(publisher);

		// automate
		this.automate = new AutomateBloc(factory);

	}

	/**
	 * Charge une installation via son id
	 * 
	 * @param id
	 * @return
	 */

	public Optional<Installation> chargeInstallation(int id) {
		var oInst = factory.getInstallationDAO().getFromID(id);

		if (oInst.isPresent()) {
			Installation inst = oInst.get();
			var groupes = factory.getGroupeLigneDAO().getListeFromInstallation(id);
			inst.getGroupeLignes().addAll(groupes);
			selectionModel.setInstallation(inst);

			// Publie l'évènement
			Map<Classe, Evenement> messages = new HashMap<>();
			messages.put(Classe.INSTALLATION, new Evenement(TypeOperation.CHARGEMENT, inst));
			publisher.submit(new ListMessages(messages));

			return Optional.of(inst);
		}

		return Optional.empty();
	}

	/**
	 * Création d'une nouvelle installation
	 * 
	 * @param installation
	 * @return
	 * @throws InstallationException
	 */

	public Installation createInstallation(Installation installation) throws InstallationException {
		try {

			installation = factory.getInstallationDAO().insert(installation);

			// Publie l'évènement
			Map<Classe, Evenement> messages = new HashMap<>();
			messages.put(Classe.INSTALLATION, new Evenement(TypeOperation.CHARGEMENT, installation));
			publisher.submit(new ListMessages(messages));

			// Mise en place de l'installation dans le modèle de sélection
			selectionModel.setInstallation(installation);

		} catch (Exception e) {
			log.error("Problème de création d'une installation: " + e.getMessage(), e);
			throw new InstallationException("Problème de création d'une installation: " + e.getMessage());
		}

		return installation;
	}

	// Retourne l'installation chargée
	public Installation getCurrentInstallation() {
		return selectionModel.getCurrentInstallation();
	}

	// Retourne le groupeLigne chargé
	public GroupeLigne getCurrentGroupeLigne() {
		return selectionModel.getCurrentGroupe();
	}

	// Retourne la ligne chargée
	public Ligne getCurrentLigne() {
		return selectionModel.getCurrentLigne();
	}

	/**
	 * update de l'installation
	 * 
	 * @param installation
	 * @throws InstallationException
	 */

	public void updateInstallation(Installation installation) throws InstallationException {
		if (installation == null) {
			throw new InstallationException("Aucune installation n’est prévue");
		}

		try {

			Connection connection = factory.getConnection();
			connection.setAutoCommit(false);

			try {

				factory.getInstallationDAO().update(installation);

				connection.commit();

				Map<Classe, Evenement> messages = new HashMap<>();
				messages.put(Classe.INSTALLATION, new Evenement(TypeOperation.UPDATE, installation));
				publisher.submit(new ListMessages(messages));

			} catch (Exception e) {
				connection.rollback();
				log.error("Problème de mise à jour d'une installation: " + e.getMessage(), e);
				throw new InstallationException("Problème de mise à jour d'une installation: " + e.getMessage());
			} finally {
				connection.setAutoCommit(true);
			}
		} catch (SQLException e) {
			log.error("Erreur de gestion de la transaction : " + e.getMessage(), e);
			throw new InstallationException("Erreur de gestion de la transaction :" + e.getMessage());
		}
	}

	/**
	 * Ajoute un groupeLigne à l'installation
	 * 
	 * @return optional de GroupeLigne
	 */
	public Optional<GroupeLigne> addGroupe() {
		GroupeLigne grp = null;

		try {
			grp = selectionModel.getCurrentInstallation().addGroupeLigne();

			Map<Classe, Evenement> messages = new HashMap<>();
			messages.put(Classe.GROUPELIGNE, new Evenement(TypeOperation.CREATE, grp));
			publisher.submit(new ListMessages(messages));

		} catch (InstallationException e) {
			log.error("Impossible d'ajouter un groupe car l'installation n'existe pas");
		}

		return Optional.ofNullable(grp);
	}

	/**
	 * Ajoute une ligne au groupe
	 * 
	 * @param groupe
	 */
	public Optional<Ligne> addLigne(GroupeLigne groupe) {
		Ligne ligne = null;
		if (selectionModel.hasInstallation() && groupe != null) {
			ligne = new Ligne();
			groupe.addLigne(ligne);

			Map<Classe, Evenement> messages = new HashMap<>();
			messages.put(Classe.LIGNE, new Evenement(TypeOperation.CREATE, ligne));
			publisher.submit(new ListMessages(messages));
		}
		return Optional.ofNullable(ligne);
	}

	/**
	 * Ajoute un bloc à une ligne
	 * 
	 * @param ligne
	 * @return
	 * 
	 */
	public Bloc addBloc(Ligne ligne) throws LigneBlocSizeException {
		assert ligne != null : "La ligne doit exister";
		Bloc bloc = null;
		if (selectionModel.hasInstallation()) {
			bloc = ligne.addBloc();

			Map<Classe, Evenement> messages = new HashMap<>();
			messages.put(Classe.BLOC, new Evenement(TypeOperation.CREATE, bloc));
			publisher.submit(new ListMessages(messages));
		}
		return bloc;
	}

	/**
	 * Ajoute un bloc et des appareils à partir d'une expression DSL
	 * 
	 * @param ligne la ligne auquel un bloc doit être rajoutée
	 * @param expr  l'expression DSL pour définir des appareils
	 * @returnn le bloc avec ses appareils
	 * @throws UnexpectedCharacterException expression invalide
	 * @throws LigneBlocSizeException       on dépasse le nombre de 8 blocs
	 */
	public Bloc addBlocAppareils(Ligne ligne, String expr) throws UnexpectedCharacterException, LigneBlocSizeException {
		assert ligne != null : "La ligne ne peut pas être à null";
		// Crée une liste d'appareils
		var liste = automate.generateListFromExpr(expr);
		// Ajoute un Bloc à la ligne
		Bloc bloc = ligne.addBloc();
		// ajoute les appareils au bloc
		bloc.getAppareils().addAll(liste);
		// publie l'évènement
		Map<Classe, Evenement> messages = new HashMap<>();
		messages.put(Classe.BLOC, new Evenement(TypeOperation.CREATE, bloc));
		publisher.submit(new ListMessages(messages));
		return bloc;
	}

	/**
	 * 
	 * @param obj
	 */
	public void setSelectedItem(IInstallationItems obj) {
		switch (obj) {
		case Installation inst -> selectionModel.setInstallation(inst);
		case GroupeLigne grp -> selectionModel.setCurrentGroupe(grp);
		case Ligne lig -> selectionModel.setCurrentLigne(lig);
		case Bloc bloc -> selectionModel.setCurrentBloc(bloc);
		case Appareil app -> selectionModel.setCurrentAppareil(app);
		default -> selectionModel.setNoSelection();
		}
	}

	public List<CodeApp> getListCodeCable() {
		return factory.getCableDAO().getListeCode();
	};

	public List<CodeApp> getListCodeCompteur() {

		return factory.getCompteurDAO().getListeCode();
	}

	public List<CodeApp> getListCodeDisjoncteur() {

		return factory.getDisjoncteurDAO().getListeCode();
	}

	public List<CodeApp> getListCodeDisjoncteurDiff() {

		return factory.getDisjoncteurDiffDAO().getListeCode();
	}
	
	
	public List<CodeApp> getListCodeLampe() {  //LAMPE

		return factory.getLampeDAO().getListeCode();
	}
	
	public List<CodeApp> getListCodeTelerupteur() {   //TELERUPTEUR

		return factory.getTelerupteurDAO().getListeCode();
	}
	
	



	// Installation

	public void setDisjoncteur(String code) {

		Optional<Disjoncteur> optionalDisjoncteur = factory.getDisjoncteurDAO().getFromID(code);

		if (optionalDisjoncteur.isPresent()) {
			// Obtenir le disjoncteur
			Disjoncteur disjoncteur = optionalDisjoncteur.get();

			// Obtenir l'installation actuelle
			Installation currentInstallation = selectionModel.getCurrentInstallation();

			// Définit le disjoncteur
			currentInstallation.setDisjoncteurCompagnie(disjoncteur);

			Map<Classe, Evenement> messages = new HashMap<>();
			messages.put(Classe.INSTALLATION, new Evenement(TypeOperation.UPDATE, currentInstallation));
			publisher.submit(new ListMessages(messages));
		} else {
			log.warn("Disjoncteur " + code + " introuvable");
		}
	}

	public void setCompteur(String code) {

		Optional<Compteur> optionalCompteur = factory.getCompteurDAO().getFromID(code);

		if (optionalCompteur.isPresent()) {

			Compteur compteur = optionalCompteur.get();

			Installation currentInstallation = selectionModel.getCurrentInstallation();

			currentInstallation.setCompteur(compteur);

			Map<Classe, Evenement> messages = new HashMap<>();
			messages.put(Classe.INSTALLATION, new Evenement(TypeOperation.UPDATE, currentInstallation));
			publisher.submit(new ListMessages(messages));

		}

	}

	public void setCable(String code) {
		this.factory.getCableDAO().getFromID(code).ifPresent((c) -> {
			Classe classe = switch (selectionModel.getSelectedItem()) {
			case Installation inst -> {
				inst.setCable(c);
				yield Classe.INSTALLATION;
			}
			case Ligne ligne -> {
				ligne.setCable(c);
				yield Classe.LIGNE;
			}
			default -> throw new IllegalArgumentException("Unexpected Elem ");
			};
			// publie l'évènement
			Map<Classe, Evenement> messages = new HashMap<>();
			messages.put(classe, new Evenement(TypeOperation.UPDATE, selectionModel.getSelectedItem()));
			publisher.submit(new ListMessages(messages));
		});

	}

	// GroupeLigne

	public void setDisjoncteurDiff(String code) {

		Optional<DisjoncteurDiff> optionalDisjoncteurDiff = factory.getDisjoncteurDiffDAO().getFromID(code);

		if (optionalDisjoncteurDiff.isPresent()) {

			DisjoncteurDiff disjoncteurDiff = optionalDisjoncteurDiff.get();

			GroupeLigne currentGroupeLigne = selectionModel.getCurrentGroupe();

			currentGroupeLigne.setDisjoncteur(disjoncteurDiff);

			Map<Classe, Evenement> messages = new HashMap<>();
			messages.put(Classe.GROUPELIGNE, new Evenement(TypeOperation.UPDATE, currentGroupeLigne));
			publisher.submit(new ListMessages(messages));
		} else {
			log.warn("DisjoncteurDiff" + code + " introuvable ");
		}
	}

	// Ligne

	public void setFusible(String code) {

		Optional<Disjoncteur> optionalFusible = factory.getDisjoncteurDAO().getFromID(code);

		if (optionalFusible.isPresent()) {

			Disjoncteur fusible = optionalFusible.get();

			Ligne currentLigne = selectionModel.getCurrentLigne();

			currentLigne.setFusible(fusible);

			Map<Classe, Evenement> messages = new HashMap<>();
			messages.put(Classe.LIGNE, new Evenement(TypeOperation.UPDATE, currentLigne));
			publisher.submit(new ListMessages(messages));
		} else {
			log.warn("Fusible " + code + " introuvable");
		}
	}

	public void setInterne(String interne) {

		Ligne currentLigne = selectionModel.getCurrentLigne();
		if (currentLigne != null) {
			currentLigne.setInterne(interne != null && interne.equals("Interne"));

			Map<Classe, Evenement> messages = new HashMap<>();
			messages.put(Classe.LIGNE, new Evenement(TypeOperation.UPDATE, currentLigne));
			publisher.submit(new ListMessages(messages));
		}
	}

	public void setCode(String code) {
		Ligne currentLigne = selectionModel.getCurrentLigne();
		if (currentLigne != null) {

			currentLigne.setCode(code);

			Map<Classe, Evenement> messages = new HashMap<>();
			messages.put(Classe.LIGNE, new Evenement(TypeOperation.UPDATE, currentLigne));
			publisher.submit(new ListMessages(messages));
		}

	}

	public void objectHasChanged(IInstallationItems item) {
		Map<Classe, Evenement> messages = new HashMap<>();
		Classe classe = switch (item) {
		case Installation inst -> Classe.INSTALLATION;
		case GroupeLigne grp -> Classe.GROUPELIGNE;
		case Ligne lig -> Classe.LIGNE;
		case Bloc bloc -> Classe.BLOC;
		case Appareil app -> Classe.APPAREIL;
		default -> throw new IllegalArgumentException("Valeur inattendue: " + item);
		};
		// publie l'évènement
		messages.put(classe, new Evenement(TypeOperation.UPDATE, item));
		publisher.submit(new ListMessages(messages));
	}

	// Sauvegarder l'installation

	public void saveInstallation() throws Exception {
		var inst = selectionModel.getCurrentInstallation();
		if (inst.getId() != null) {
			factory.getInstallationDAO().update(inst);
		}
	}

	/**
	 * Supprime le groupe si possible
	 * 
	 * @param ligne
	 * @throws Exception
	 */
	public void deleteGroupeLigne(GroupeLigne groupe) throws Exception {
		assert groupe != null : "Le groupe ne peut pas être null";
		var installation = selectionModel.getCurrentInstallation();
		installation.removeGroupeLigne(groupe);

		// supprime le groupe de la base de données
		// TODO Activer la ligne suivante lorsque DAO fait
		factory.getGroupeLigneDAO().delete(groupe);
		// publie l'évènement
		Map<Classe, Evenement> messages = new HashMap<>();
		messages.put(Classe.GROUPELIGNE, new Evenement(TypeOperation.DELETE, groupe));
		publisher.submit(new ListMessages(messages));

	}

	/**
	 * Supprime la ligne si possible
	 * 
	 * @param ligne
	 * @throws Exception
	 */
	public void deleteLigne(Ligne ligne) throws Exception {
		assert ligne != null : "La ligne ne peut pas être null";
		// Récupère l'identifiant de son groupeLigne
		Integer idGroupe = ligne.getNum() / 100;
		// recherche le groupe:
		GroupeLigne grp = selectionModel.getCurrentInstallation().getGroupeLignes().stream()
				.filter(g -> g.getNum().equals(idGroupe)).findFirst().orElseThrow();
		grp.removeLigne(ligne.getNum());
		// supprime la ligne de la base de données
		// TODO A décocher lorsque le DAO Existe
		factory.getLigneDAO().delete(ligne);
		// publie l'évènement
		Map<Classe, Evenement> messages = new HashMap<>();
		messages.put(Classe.LIGNE, new Evenement(TypeOperation.DELETE, ligne));
		publisher.submit(new ListMessages(messages));

	}

	/**
	 * Supprime le bloc
	 * 
	 * @param bloc
	 * @throws Exception
	 */
	public void deleteBloc(Bloc bloc) throws Exception {
		assert bloc != null : "Le bloc ne peut pas être null";
		// Récupère l'identifiant de sa ligne et de son groupe
		Integer idLigne = bloc.getId() / 10;
		Integer idGroupe = idLigne / 100;
		// recherche le groupe de la ligne:
		GroupeLigne grp = selectionModel.getCurrentInstallation().getGroupeLignes().stream()
				.filter(g -> g.getNum().equals(idGroupe)).findFirst().orElseThrow();
		// recherche la ligne du groupe
		Ligne ligne = grp.getLignes().stream().filter(l -> l.getNum().equals(idLigne)).findFirst().orElseThrow();
		// supprime le bloc de la ligne
		ligne.removeBloc(bloc);
		// supprime de la base de données
		// TODO A activer lorsque le DAO est fait
		factory.getBlocDAO().delete(bloc);

		// publie l'évènement
		Map<Classe, Evenement> messages = new HashMap<>();
		messages.put(Classe.BLOC, new Evenement(TypeOperation.DELETE, bloc));
		publisher.submit(new ListMessages(messages));
	}

	/**
	 * Permet de s'abonner aux évènements
	 * 
	 * @param treeBuilder
	 */
	public void addObserver(Subscriber<ListMessages> abonne) {
		publisher.addObserver(abonne);
	}

	/**
	 * Permettra de choisir une installation
	 * 
	 * @return
	 */
	public List<RInstallation> getListeInstallations() {

		return factory.getInstallationDAO().getListeRecord();
	}

}
