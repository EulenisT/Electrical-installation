package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import dao.exception.InstallationException;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import model.appareil.DisjoncteurDiff;

/**
 * @author Didier
 * Permet de définir pour un disjoncteur différentiel 
 * un ensemble de lignes (chaque ligne sera protégée par un fusible)
 */
@Slf4j
@AllArgsConstructor // pour charger un groupe existant
@EqualsAndHashCode(exclude = { "lignes", "disjoncteur" })
public class GroupeLigne implements IInstallationItems {
	@Getter
	private Integer num;//identifiant (inst||groupe)
	@Getter
	@Setter
	private DisjoncteurDiff disjoncteur;
	//liste des lignes
	private List<Ligne> lignes = new ArrayList<Ligne>();

	/**
	 * Permet de créer un nouveau groupe
	 * sera appelé via l'installation
	 * addNewGroupeLigne()
	 * @param id
	 */
	public GroupeLigne(Integer num) {
		this.num = num;
	}

	/**
	 * Permet de rajouter un ligne à un groupe
	 * génére son id si elle n'en possède pas
	 * @param ligne
	 */
	public void addLigne(Ligne ligne) {
		assert ligne.getNum() == null || ligne.getNum() % 100 == this.num
				: "Le numéro de ligne doit commencer par le numéro de son groupe";
		if (ligne.getNum() == null) {
			// généré l'id de la ligne
			/*recherche le 1er code libre*/
			// charge dans ens ts les numéros de ligne utilisés (juste les 2 premiers
			// chiffres)
			Set<Integer> ens = new HashSet<>(lignes.size());
			for (var l : lignes)
				ens.add(l.getNum() % 100);
			// cherche un numéro qui ne se trouve pas dans l'ensemble
			Integer debut = lignes.size();// en principe c'est souvent le bon numéro (sauf si suppression de lignes)
			while (ens.contains(debut))
				debut = (debut + 1) % 100;
			ligne.setNum(this.num * 100 + debut);
			log.info("une ligne avec l'ID:" + ligne.getNum() + " a été créée");
		}
		lignes.add(ligne);
		log.info("La ligne " + ligne.getNum() + " a été rajoutée au groupe: " + this.num);
	}

	/**
	 * Supprime une ligne si elle existe et qu'elle ne contient pas de bloc
	 * @param numLigne
	 * @throws InstallationException
	 */
	public void removeLigne(Integer numLigne) throws InstallationException {
		// recherche la ligne
		Optional<Ligne> oLigne = lignes.stream().filter(l -> l.getNum() == numLigne).findFirst();
		Ligne l = oLigne.orElseThrow(() -> new InstallationException("Ce numéro de ligne n'existe pas"));
		if (l.getBlocs().size() != 0)
			throw new InstallationException("Cette Ligne ne peut pas être supprimée car elle possède des blocs");
		// supprime la ligne
		lignes.removeIf((elem) -> elem.getNum() == numLigne);
	}

	@Override
	public String toString() {
		String disj = disjoncteur == null ? "---"
				: disjoncteur.getAmperage() + "A/" + disjoncteur.getSensibilite() + "ma";
		return "Groupe( disjoncteur: " + disj + " NbLignes:" + lignes.size() + ")";
	}

	/**
	 * Retourne une version non modifiable de la liste
	 * @return liste de lignes
	 */
	public List<Ligne> getLignes() {
		return Collections.unmodifiableList(lignes);
	}
}
