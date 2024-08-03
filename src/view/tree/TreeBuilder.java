package view.tree;

import java.util.Optional;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

import controller.MainController;
import dao.exception.LigneBlocSizeException;
import javafx.application.Platform;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import model.Bloc;
import model.GroupeLigne;
import model.Installation;
import model.Ligne;
import model.appareil.Appareil;
import model.automate.UnexpectedCharacterException;
import services.Facade;
import services.ListMessages;
import services.ListMessages.Evenement;
import services.TypeOperation;
import view.utils.I18N;
import view.utils.ViewUtils;

@Slf4j
public class TreeBuilder implements Subscriber<ListMessages> {
	private Installation inst;

	@Getter
	private TreeView<ItemTreeWrapper> tree;

	private Facade facade;

	private MainController ctrl;
	// Pour gérer les évènements de publication
	private Subscription subscription;

	private ContextMenu menuInstallation;

	private ContextMenu menuGroupe;

	private ContextMenu menuLigne;

	private ContextMenu menuBloc;

	/**
	 * Création d'un TreeBuilder
	 * ==> prépare l'accès aux données et les menus contextuels
	 * La construction de l'arbre se fera via la méthode "buildTree"
	 */
	public TreeBuilder(MainController ctrl) {
		this.ctrl = ctrl;
		this.facade = ctrl.getFacade();
		// Création des menus
		menuInstallation = creeMenuInstallation();
		menuGroupe = creeMenuGroupe();
		menuLigne = creeMenuLigne();
		menuBloc = creeMenuBloc();

		// crée un arbre avec une installation vide
		// TreeItem<ItemTreeWrapper> itemInst = creeNoeudRacine(new Installation());
		tree = new TreeView<ItemTreeWrapper>();// itemInst);

		// Redéfinition de la cellule pour avoir le bon texte et l'icone
		tree.setCellFactory(tv1 -> new TreeCell<ItemTreeWrapper>() {

			protected void updateItem(ItemTreeWrapper item, boolean empty) {
				super.updateItem(item, empty);
				var treeItem = getTreeItem();
				setGraphic(treeItem == null ? null : treeItem.getGraphic());
				setText(item == null ? null : item.getNom());
				setOpacity(1);
				if (item == null)
					return;
			};
		});

		// Attribue le menu contextuel en fonction du noeud sélectionné (Groupe,
		// Ligne,...
		tree.getSelectionModel().selectedItemProperty().addListener((o, anc, nouv) -> {
			if (nouv != null) {
				switch (nouv.getValue()) {
				// charge le bon menu en fonction du type de l'Item sélectionné
				case ItemTreeInstallationWrapper brol -> {
					facade.setSelectedItem(brol.getObject());
					tree.setContextMenu(menuInstallation);
				}
				case ItemTreeGroupeWrapper brol -> {
					facade.setSelectedItem(brol.getObject());
					tree.setContextMenu(menuGroupe);
				}
				case ItemTreeLigneWrapper brol -> {
					facade.setSelectedItem(brol.getObject());
					tree.setContextMenu(menuLigne);
				}
				case ItemTreeBlocWrapper brol -> {
					facade.setSelectedItem(brol.getObject());
					tree.setContextMenu(menuBloc);
				}
				case ItemTreeAppareilWrapper brol -> {
					facade.setSelectedItem(brol.getObject());
					tree.setContextMenu(null);
				}
				default -> tree.setContextMenu(null);
				}
			}

		});

		// s'enregistre sur les évènements
		facade.addObserver(this);

	}

	/**
	 * reconstruit l'arborescence si c'est une nouvelle installation
	 * 
	 */
	public void reBuildTree() {
		// récupère l'installation actuelle
		Installation inst = facade.getCurrentInstallation();
		// vérifie s'il faut recréer l'arbre ou pas
		// s'agit-il d'une même installation ou d'une nouvelle
		if (!(this.inst == null && inst != null || this.inst != null && this.inst.getId() != inst.getId()))
			return;
		// C'est une nouvelle installation ==>
		this.inst = facade.getCurrentInstallation();
		// Vide l'ancien arbre (s'il existe)
		if (tree != null && tree.getRoot()!=null)
			tree.getRoot().getChildren().clear();

		/*  Création de l'arbre avec son noeud racine       */
		TreeItem<ItemTreeWrapper> itemInst = creeNoeudRacine(this.inst);

		// arbre avec son noeud racine
		// tree = new TreeView<>(itemInst);

		tree.setRoot(itemInst);
		// création des groupes et ses enfants (lignes, blocs, App)
		addItemGroupes(itemInst);
		// Sélectionne la racine
		tree.getSelectionModel().clearAndSelect(0);
	}

	/**
	 * @param inst 
	 * @return
	 */
	private TreeItem<ItemTreeWrapper> creeNoeudRacine(Installation inst) {
		// Noeud racine de l'installation
		ItemTreeInstallationWrapper wap = new ItemTreeInstallationWrapper(inst);
		// crée son icone
		final ImageView icon = new ImageView(ViewUtils.getIcon(wap));
		icon.setFitHeight(16);
		icon.setFitWidth(16);
		TreeItem<ItemTreeWrapper> itemInst = new TreeItem<>(wap, icon);
		return itemInst;
	}

	/**
	 * Crée les noeuds groupes et indirectement ses enfants
	 * @param itemInst le noeud racine
	 */
	private void addItemGroupes(TreeItem<ItemTreeWrapper> itemInst) {
		// récupère l'installation à partir du wrapper
		Installation inst = (Installation) itemInst.getValue().getObject();
		for (GroupeLigne grp : inst.getGroupeLignes()) {
			TreeItem<ItemTreeWrapper> itemGrp = addGroupeWrapper(grp);
			itemInst.getChildren().add(itemGrp);
			itemInst.setExpanded(true);
			addItemLignes(itemGrp);
		}
	}

	/**
	 * crée un wrapper pour un groupe en précisant son image
	 * @param grp le Groupe Ligne
	 * @return le wrapper pour un groupe aved son image
	 */
	private TreeItem<ItemTreeWrapper> addGroupeWrapper(GroupeLigne grp) {
		ItemTreeGroupeWrapper wap = new ItemTreeGroupeWrapper(grp);
		// Icon
		final ImageView icon = new ImageView(ViewUtils.getIcon(wap));
		icon.setFitHeight(16);
		icon.setFitWidth(16);

		TreeItem<ItemTreeWrapper> itemGrp = new TreeItem<>(wap, icon);
		return itemGrp;
	}

	/**
	 * Crée les noeuds lignes et indirectement ses enfants
	 * @param itemGrp le noeud parent (groupe)
	 */
	private void addItemLignes(TreeItem<ItemTreeWrapper> itemGrp) {
		// récupère le groupeLigne à partir du wrapper
		GroupeLigne grp = (GroupeLigne) itemGrp.getValue().getObject();
		// rajoute chaque ligne
		for (Ligne l : grp.getLignes()) {
			// Crée un wrapper pour cette ligne
			TreeItem<ItemTreeWrapper> itemLigne = addLigneWrapper(l);
			// Rajoute ce wrapper au noeud Groupe de l'arbre
			itemGrp.getChildren().add(itemLigne);
			itemGrp.setExpanded(true);// développe le noeud

			// rajoute les blocs de cette lignes
			addItemBlocs(itemLigne);
		}
	}

	/**
	 * crée un wrapper pour une ligne en précisant son image
	 * @param l la ligne
	 * @return le wrapper et son image
	 */
	private TreeItem<ItemTreeWrapper> addLigneWrapper(Ligne l) {
		ItemTreeLigneWrapper wap = new ItemTreeLigneWrapper(l);
		// Icon
		final ImageView icon = new ImageView(ViewUtils.getIcon(wap));
		icon.setFitHeight(16);
		icon.setFitWidth(16);

		TreeItem<ItemTreeWrapper> itemLigne = new TreeItem<>(wap, icon);
		return itemLigne;
	}

	/**
	 * Crée les noeuds blocs et indirectement ses enfants
	 * @param itemLigne
	 */
	private void addItemBlocs(TreeItem<ItemTreeWrapper> itemLigne) {
		// récupère la ligne à partir du wrapper
		Ligne l = (Ligne) itemLigne.getValue().getObject();
		// rajoute chaque Bloc
		for (Bloc b : l.getBlocs()) {
			TreeItem<ItemTreeWrapper> itemBloc = addBlocWrapper(b);
			itemLigne.getChildren().add(itemBloc);
			addItemAppareil(itemBloc);
		}

	}

	/**
	 * Crée un wrapper pour un bloc
	 * @param b un bloc
	 * @return le wrapper du bloc avec son image
	 */
	private TreeItem<ItemTreeWrapper> addBlocWrapper(Bloc b) {
		ItemTreeBlocWrapper wap = new ItemTreeBlocWrapper(b);
		// Icon
		final ImageView icon = new ImageView(ViewUtils.getIcon(wap));
		icon.setFitHeight(16);
		icon.setFitWidth(16);

		TreeItem<ItemTreeWrapper> itemBloc = new TreeItem<>(wap, icon);
		return itemBloc;
	}

	/**
	 * créer les noeuds Appareil d'un bloc
	 * @param itemBloc
	 */
	private void addItemAppareil(TreeItem<ItemTreeWrapper> itemBloc) {
		// récupère le bloc à partir de son wrapper
		Bloc b = (Bloc) itemBloc.getValue().getObject();
		// rajoute chaque Appareil
		for (Appareil a : b.getAppareils()) {
			TreeItem<ItemTreeWrapper> itemApp = addAppareilWrapper(a);
			itemBloc.getChildren().add(itemApp);
		}
	}

	/**
	 * Créer un wrapper pour un appareil avec son image SVG
	 * @param a
	 * @return
	 */
	private TreeItem<ItemTreeWrapper> addAppareilWrapper(Appareil a) {
		ItemTreeAppareilWrapper wap = new ItemTreeAppareilWrapper(a);
		// Icon
		final Pane icon = ViewUtils.getIconFromSvg(wap);
		TreeItem<ItemTreeWrapper> itemApp = new TreeItem<>(wap, icon);
		return itemApp;
	}

	/*
	 * Création des menus contextuels 
	 */

	/**
	 * Menu contexte pour l'élément Installation
	 * @return le menu contextuel
	 */
	private ContextMenu creeMenuInstallation() {
		ContextMenu menu = new ContextMenu();

		MenuItem menuItem1 = new MenuItem(I18N.getString("ctx.addGroupe"));
		menuItem1.setOnAction(e -> {
			TreeItem<ItemTreeWrapper> item = tree.getSelectionModel().getSelectedItem();
			if (item != null && item.getValue() instanceof ItemTreeInstallationWrapper) {
				// Ajout Groupe
				Optional<GroupeLigne> oGroupe = facade.addGroupe();
				// maj de l'arbre en rajoutant un wrapper de groupeLigne
				oGroupe.ifPresent(g -> {
					TreeItem<ItemTreeWrapper> itemGroupe = addGroupeWrapper(g);
					item.getChildren().add(itemGroupe);
				});
			}
		});
		menu.getItems().add(menuItem1);
		return menu;
	};

	/**
	 * Menu contexte pour l'élément GroupeLigne
	 * @return le menu contextuel
	 */
	private ContextMenu creeMenuGroupe() {
		ContextMenu menu = new ContextMenu();

		MenuItem menuItem1 = new MenuItem(I18N.getString("ctx.addLigne"));
		menuItem1.setOnAction(e -> {
			TreeItem<ItemTreeWrapper> item = tree.getSelectionModel().getSelectedItem();
			if (item != null && item.getValue() instanceof ItemTreeGroupeWrapper itemWrapper) {
				// Ajout Ligne
				Optional<Ligne> oLigne = facade.addLigne((GroupeLigne) itemWrapper.getObject());
				// maj de l'arbre
				oLigne.ifPresent(l -> {
					TreeItem<ItemTreeWrapper> itemLigne = addLigneWrapper(l);
					item.getChildren().add(itemLigne);
				});
			}
		});
		MenuItem menuItem2 = new MenuItem(I18N.getString("ctx.delGroupe"));
		menuItem2.setOnAction(e -> {
			TreeItem<ItemTreeWrapper> item = tree.getSelectionModel().getSelectedItem();
			if (item != null && item.getValue() instanceof ItemTreeGroupeWrapper itemWrapper) {
				// Supprime Groupe
				boolean ok = ctrl.showConfirmation(I18N.getString("ctx.delGroupe") + " ?");
				if (ok) {
					GroupeLigne groupe;
					try {
						groupe = (GroupeLigne) itemWrapper.getObject();

						facade.deleteGroupeLigne(groupe);
						// maj de l'arbre
						// remonte au parent (Installation) pour supprimer ce noeud
						item.getParent().getChildren().remove(item);
						item = null;
					} catch (Exception e1) {
						ctrl.showErreur(e1.getMessage());
					}
				}
			}
		});

		menu.getItems().addAll(menuItem1, menuItem2);
		return menu;
	};

	/**
	 * Menu contexte pour l'élément Ligne
	 * @return le menu contextuel
	 */
	private ContextMenu creeMenuLigne() {
		ContextMenu menu = new ContextMenu();

		MenuItem menuItem1 = new MenuItem(I18N.getString("ctx.addBloc"));
		menuItem1.setOnAction(e -> {
			TreeItem<ItemTreeWrapper> item = tree.getSelectionModel().getSelectedItem();
			if (item != null && item.getValue() instanceof ItemTreeLigneWrapper itemWrapper) {
				// récupère la ligne
				Ligne ligne = (Ligne) itemWrapper.getObject();
				// Ajout un bloc et ses appareils
				Bloc bloc;

				// demande la création des appareils via une expression
				var inExpr = new TextInputDialog(I18N.getString("ctx.addAppExp"));
				var oExpr = inExpr.showAndWait();
				if (oExpr.isPresent()) {
					try {
						// Création du bloc avec ses appareils et ajout à la ligne
						bloc = facade.addBlocAppareils(ligne, oExpr.get());

						/* maj de la TreeView  */
						// maj du bloc sur la ligne de la TreeView
						TreeItem<ItemTreeWrapper> itemBloc = addBlocWrapper(bloc);
						item.getChildren().add(itemBloc);
						// maj des appareils sur l'arbre
						var liste = bloc.getAppareils();
						for (Appareil app : liste) {
							TreeItem<ItemTreeWrapper> itemApp = addAppareilWrapper(app);
							itemBloc.getChildren().add(itemApp);
						}
					} catch (UnexpectedCharacterException e1) {
						ctrl.showErreur(e1.getMessage() + " car: " + e1.getCar());
					} catch (LigneBlocSizeException e1) {
						ctrl.showErreur(I18N.getString("err.blocLimit"));
					}
				}

			}
		});

		MenuItem menuItem2 = new MenuItem(I18N.getString("ctx.delLigne"));
		menuItem2.setOnAction(e -> {
			TreeItem<ItemTreeWrapper> item = tree.getSelectionModel().getSelectedItem();
			if (item != null && item.getValue() instanceof ItemTreeLigneWrapper itemWrapper) {
				// Supprime ligne
				boolean ok = ctrl.showConfirmation(I18N.getString("ctx.delLigne") + " ?");
				if (ok) {
					Ligne ligne;
					try {
						ligne = (Ligne) itemWrapper.getObject();

						facade.deleteLigne(ligne);
						// maj de l'arbre
						// remonte au parent (ligne) pour supprimer ce noeud
						item.getParent().getChildren().remove(item);
						item = null;
					} catch (Exception e1) {
						ctrl.showErreur(e1.getMessage());
					}
				}
			}
		});
		menu.getItems().addAll(menuItem1, menuItem2);
		return menu;
	};

	/**
	 * Menu contexte pour l'élément Bloc
	 * @return le menu contextuel
	 */
	private ContextMenu creeMenuBloc() {
		ContextMenu menu = new ContextMenu();

		MenuItem menuItem2 = new MenuItem(I18N.getString("ctx.delBloc"));
		menuItem2.setOnAction(e -> {
			TreeItem<ItemTreeWrapper> item = tree.getSelectionModel().getSelectedItem();
			if (item != null && item.getValue() instanceof ItemTreeBlocWrapper itemWrapper) {
				// Supprime Bloc
				boolean ok = ctrl.showConfirmation(I18N.getString("ctx.delBloc"));
				if (ok) {
					Bloc bloc;
					try {
						bloc = (Bloc) itemWrapper.getObject();

						facade.deleteBloc(bloc);
						// maj de l'arbre
						// remonte au parent (ligne) pour supprimer ce noeud
						item.getParent().getChildren().remove(item);
						item = null;
					} catch (Exception e1) {
						ctrl.showErreur(e1.getMessage());
					}
				}
			}
		});
		menu.getItems().addAll(/*menuItem1,*/ menuItem2);
		return menu;
	}

	/****************** GESTION DE LA PUBLICATION ********************/

	// Gestion des publications
	// appellé lors de l'enregistrement on reçoit alors une subscription
	@Override
	public void onSubscribe(Subscription subscription) {
		this.subscription = subscription;
		subscription.request(10);
		log.info("Je suis un écouteur d'installation ");
	}

	// Reception des messages
	@Override
	public void onNext(ListMessages messages) {
		// log.info("EVENT DS TREE:(Create:) "
		// +messages.EventForTypeOperation(TypeOperation.CREATE) + " sur installation
		// (TreeBuilder)");
		// vérifie s'il existe un événement sur "installation"
		Optional<Evenement> oEv = messages.getEventFromClasse(services.ListMessages.Classe.INSTALLATION);
		// récupère l'objet (si nécessaire)
		if (oEv.isPresent())

		{
			// récupère le type d'opération
			TypeOperation operation = oEv.get().op();

			log.info(operation + " sur installation (TreeBuilder)");
			switch (operation) {
			case CREATE, CHARGEMENT:
				Platform.runLater(() -> this.reBuildTree());
				break;

			default:
				break;
			}
		}
		subscription.request(1);
	}

	@Override
	public void onError(Throwable throwable) {
		log.error("erreur d'abonnement aux évènements TreeBuilder");

	}

	@Override
	public void onComplete() {
		log.info(" écouteur d'installation: On Complete");
	}

	// Permet d'avoir la "Subscription pour se désabonner"
	public Subscription getSubscription() {
		return subscription;
	}
}
