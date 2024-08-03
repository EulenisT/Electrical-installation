package model.automate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dao.DAOFactory;
import dao.IInterrupteurDao;
import dao.ILampeDao;
import dao.IPriseDao;
import dao.ITelerupteurDao;
import model.appareil.Appareil;
import model.appareil.Interrupteur;
import model.appareil.Lampe;
import model.appareil.Prise;
import model.appareil.Telerupteur;

public class AutomateBloc {
//expression régulière de validation de l'expression
	private static final String REG_EXPR = "(P|P[1..5])|(I|I[1-6])((P|P[1-5])|(L|L[1-9]))"; // "(P|P[1..5])|(I|I[1-6])(P|P[1-5])";
	// Version avec les lampes: "(P|P[1..5])|(I|I[1-6])((P|P[1-5])|(L|L[1-9]))";

//permet de savoir rapidement si une expression est valide
	public static final boolean exprIsValid(String expr) {
		return expr.matches(REG_EXPR);
	}

//Clés associés aux possibilités d'états
	enum ETAT {
		INIT, P, GP, I, GI, L, GL, T, END
	};

	// Map ETAT --> objet Etat
	private Map<ETAT, Etat> etats = new HashMap<>();

	// Etat actuel
	private ETAT etatActuel = ETAT.INIT;

	// DAO
	private IPriseDao daoPrise;
	private IInterrupteurDao daoInterrupteur;
	private ILampeDao daoLampe;
	private ITelerupteurDao daoTelerupteur; 

	// Liste des appareils construite lors de l'évaluation de l'expression
	private List<Appareil> appareils;

	// Expression à déchiffrer
	private String expr;

	// Indice dans l'expression
	private int i;

	/**
	 * @param fabrique
	 * @param expr
	 */
	public AutomateBloc(DAOFactory fabrique) {
		this.daoPrise = fabrique.getPriseDAO();
		this.daoInterrupteur = fabrique.getInterrupteurDAO();
		this.daoLampe = fabrique.getLampeDAO();
		this.daoTelerupteur = fabrique.getTelerupteurDAO();

		this.etatActuel = ETAT.INIT;
		// Crée tous les états
		etats.put(ETAT.INIT, new EtatInit(this));
		etats.put(ETAT.P, new EtatPrise(this));
		etats.put(ETAT.GP, new EtatGroupePrise(this));
		etats.put(ETAT.I, new EtatInterrupteur(this));
		etats.put(ETAT.GI, new EtatGroupeInterrupteur(this));
		etats.put(ETAT.L, new EtatLampe(this));
		etats.put(ETAT.GL, new EtatGroupeLampe(this));
		etats.put(ETAT.T, new EtatTelerupteur(this));
	}

	/**
	 * Evalue l'expression
	 * 
	 * @param expr expression
	 * @return liste d'appareils spécifié via l'expression
	 * @throws UnexpectedCharacterException
	 */
	public List<Appareil> generateListFromExpr(String expr) throws UnexpectedCharacterException {
		this.etatActuel = ETAT.INIT;
		this.expr = expr + '^';// ajoute le caractère de terminaison '^'
		// Création d'une liste vide d'appareil
		this.appareils = new ArrayList<>();
		// on se met avant le premier caractère car le 1ère n'est pas encore traité
		this.i = -1;
		Etat etat;
		while (etatActuel != ETAT.END && hasNext()) {
			etat = etats.get(etatActuel);
			etat.actionIn();
		}
		return appareils;
	}

	/*************** Visibilité package Package ***********************/
	/* Avancement sur l'expression */
	/**
	 * indique s'il reste des caractères à exploiter dans l'expression
	 * 
	 * @return
	 */
	boolean hasNext() {
		return i < expr.length() - 1;
	}

	/**
	 * retourne la caractère courant
	 * 
	 * @return
	 */
	char getCar() {
		return expr.charAt(i);
	}

	/**
	 * avance et retourne le caractère
	 * 
	 * @return
	 */
	char next() {
		if (i < expr.length())
			i++;
		return expr.charAt(i);
	}

	/* ACTIONS IN utilisée par les états */
	void actionCreePrise() {
		Prise p = daoPrise.getFromID("PC11").orElseThrow();

		appareils.add(p);
	}

	void actionCreeInterrupteur(String type) {
		Interrupteur i = daoInterrupteur.getFromID(type).orElseThrow();
		appareils.add(i);
	}

	void actionCreeLampe() {
		Lampe l = daoLampe.getFromID("L1").orElseThrow();
		appareils.add(l);
	}
	
	void actionCreeTelerupteur() {
		Telerupteur t = daoTelerupteur.getFromID("TL24").orElseThrow();
		appareils.add(t);
	}

	/**
	 * 
	 * @param nextState
	 */
	void nextState(ETAT nextState) {
		this.etatActuel = nextState;
	}
}
