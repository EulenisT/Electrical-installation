package view.utils;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import view.tree.ItemTreeAppareilWrapper;
import view.tree.ItemTreeBlocWrapper;
import view.tree.ItemTreeGroupeWrapper;
import view.tree.ItemTreeInstallationWrapper;
import view.tree.ItemTreeLigneWrapper;
import view.tree.ItemTreeWrapper;


public class ViewUtils {
	// Chemin des icones
	private static String ICON_BASE_PATH = "./view/images/tree/";
	// Cache pour les images
	private static final Map<String, Image> loadedImages = new HashMap<>();

	/**
	 * Charge une image sur base du Wrapper
	 * Utile le cache pour réexploiter les images
	 * @param node
	 * @return
	 */
	public static Image getIcon(ItemTreeWrapper node) {

		return switch (node) {
		case ItemTreeInstallationWrapper inst -> getIcon("Tableau.png");
		case ItemTreeGroupeWrapper grp -> getIcon("Groupe.png");
		case ItemTreeLigneWrapper lig -> getIcon("Ligne.png");
		case ItemTreeBlocWrapper bloc -> getIcon("Bloc.png");
		case ItemTreeAppareilWrapper app -> getIcon("Bloc2.png");
		default -> throw new IllegalArgumentException("Unexpected value: " + node);
		};
	};

	/**
	 * Charge un Pane avec le dessin SGV pour former une icone
	 * @param wap un objet Wrapper
	 * @return
	 */
	public static Pane getIconFromSvg(ItemTreeAppareilWrapper wap) {
		String svg = wap.getObject().getSvg();
		SVGPath svgPath = new SVGPath();
		svgPath.setContent(svg);
		// Creating a Group object
		svgPath.setStrokeWidth(0.5);
		svgPath.setFill(Color.TRANSPARENT);
		svgPath.setStroke(Color.BLACK);
		// svgPath.setScaleX(2.5);svgPath.setScaleY(2.5);
		// svgPath.setTranslateX(2);
		svgPath.setTranslateY(0);
		HBox img = new HBox(svgPath);
		img.setScaleX(2.5);
		img.setScaleY(2.5);
		return img;
	}

	/**
	 * Charge l'image sur base de son nom 
	 * utilise le cache
	 * @param nom
	 * @return
	 */
	private static Image getIcon(String nom) {
		Image image = loadedImages.get(nom);
		if (image == null) {
			String url = ICON_BASE_PATH + nom;
			image = new Image(url);
			loadedImages.put(nom, image);
		}
		return image;
	}
}
