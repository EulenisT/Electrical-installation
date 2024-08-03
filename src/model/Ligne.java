package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dao.exception.LigneBlocSizeException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import model.appareil.Cable;
import model.appareil.Disjoncteur;

/**
 * @author Didier
 */
@EqualsAndHashCode(exclude = "blocs")
@NoArgsConstructor // pour créer une nouvelle ligne
public class Ligne implements IInstallationItems {
	@Getter
	@Setter
	private Integer num;// ID groupe||NUMLigne(2chiffres)
	@Getter
	@Setter
	private String code;// code de la ligne sur le plan

	// cable intégré au mur
	@Getter
	@Setter
	private boolean interne = true; // default
	@Getter
	@Setter
	private Cable cable;
	@Getter
	@Setter
	private Disjoncteur fusible;

	private List<Bloc> blocs = new ArrayList<Bloc>();

	// utilisé en interne pour attribuer l'identifiant d'un bloc
	private boolean[] usedBlocId = new boolean[8];

	/**
	 * Pour créer une ligne existante
	 * @param num //id de la ligne
	 * @param code //code de ligne sur le plan
	 * @param interne
	 * @param cable
	 * @param fusible
	 */
	public Ligne(int num, String code, boolean interne, Cable cable, Disjoncteur fusible) {
		this.num = num;
		this.interne = interne;
		this.cable = cable;
		this.fusible = fusible;
		this.code = code;
	}

	/**
	 * Rajoute un bloc avec son id à la ligne 
	 * Utlisé lorsque l'on charge un bloc existant
	 * @param bloc
	 * @return nombre de blocs sur la ligne
	 * @throws LigneBlocSizeException
	 */
	public int addBloc(Bloc bloc) throws LigneBlocSizeException {
		assert !usedBlocId[bloc.getId() % 10] : "Ce identifiant de bloc existe déjà";
		if (blocs.size() >= 8)
			throw new LigneBlocSizeException("Une ligne ne peut pas avoir plu de 8 blocs", this);
		blocs.add(bloc);
		usedBlocId[bloc.getId() % 10] = true;
		return blocs.size();
	}

	/**
	* Rajoute un nouveau bloc et génère son id 
	* Utilisé pour créer un nouveau bloc
	* @param bloc
	* @return
	* @throws LigneBlocSizeException
	*/
	public Bloc addBloc() throws LigneBlocSizeException {
		if (blocs.size() >= 8)
			throw new LigneBlocSizeException("Une ligne ne peut pas avoir plu de 8 blocs", this);
		// cherche un id libre
		int i = blocs.size();
		while (usedBlocId[i])
			i = (i + 1) % 8;
		// génère l'id de bloc (ligne||i)
		int blocId = num * 10 + i;
		// crée le bloc
		Bloc bloc = new Bloc(blocId);
		// ajoute à la liste des blocs
		blocs.add(bloc);
		// indique que ce numéro de bloc est utilisé
		usedBlocId[i] = true;
		return bloc;
	}

	/**
	 * Retourne la liste de blocs (non modifiable) 
	 * @return
	 */
	public List<Bloc> getBlocs() {
		return Collections.unmodifiableList(blocs);
	}

	/**
	 * Supprime un bloc et libère son id
	 * @param bloc
	 * @return nbr de blocs restant sur la ligne
	 */
	public int removeBloc(Bloc bloc) {
		// retire le bloc de la liste
		blocs.remove(bloc);
		// indique que ce numéro de bloc n'est plus utilisé
		usedBlocId[bloc.id % 10] = false;
		return blocs.size();
	}

	@Override
	public String toString() {
		StringBuilder chaine = new StringBuilder();
		chaine.append(num + " (F").append(fusible == null ? "--" : fusible.getAmperage() + "A").append(", C:")
				.append(cable == null ? "--" : cable.code()).append(",Interne: " + interne + ")");
		return chaine.toString();
	}

}
