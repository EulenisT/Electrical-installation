package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import dao.exception.InstallationException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import model.appareil.Cable;
import model.appareil.Compteur;
import model.appareil.Disjoncteur;

/**
 * @author Didier
 */
@Getter
@Setter
@EqualsAndHashCode(exclude = { "groupeLignes", "disjoncteurCompagnie", "compteur" })
@NoArgsConstructor
@ToString(exclude = { "compteur", "disjoncteurCompagnie", "groupeLignes", "cable" })
public class Installation implements IInstallationItems {
	public enum TypeLogement {
		APPARTEMENT, MAISON
	};

	private Integer id=null;// générer par la BD
	private LocalDate date;
	private String Installateur;
	private Adresse adresse;
	private TypeLogement typeLogement;
	private Compteur compteur;
	private Disjoncteur disjoncteurCompagnie;
	private List<GroupeLigne> groupeLignes = new ArrayList<>();
	private Cable cable;

	/**
	* Constructeur pour créer une nouvelle installation
	* @param date
	* @param installateur
	* @param adresse
	* @param typeLogement
	*/
	public Installation(LocalDate date, String installateur, Adresse adresse, TypeLogement typeLogement) {
		this.date = date;
		Installateur = installateur;
		this.adresse = adresse;
		this.typeLogement = typeLogement;
	}

	/**
	 * Crée un nouveau groupeLigne en générant son ID
	 * codeInst||numéro de ligne(1..9)
	 * ajoute le groupe à l'installation
	 * @return une groupe ligne avec son ID
	 * @throws InstallationException 
	 */
	public GroupeLigne addGroupeLigne() throws InstallationException {
		if (this.id == null)
			throw new InstallationException("L'id de l'installation doit exister avant de générer un groupe");
		/* recherche le premier numéro de groupe non utilisé (0..9)*/
		// mémorise les codes de groupe déjà utiliés
		boolean[] code = new boolean[9];
		for (var grp : groupeLignes)
			code[grp.getNum() % 10] = true;
		// prend le 1er code de libre
		int i = 0;
		while (code[i])
			i++;
		// Création du nouveau groupe
		GroupeLigne groupe = new GroupeLigne(id * 10 + i);
		groupeLignes.add(groupe);
		return groupe;
	};

	/**
	 * Supprime un groupe à condition qu'il ne possède pas de ligne
	 * @param groupe
	 * @return
	 * @throws InstallationException
	 */
	public boolean removeGroupeLigne(GroupeLigne groupe) throws InstallationException {
		if (!groupe.getLignes().isEmpty())
			throw new InstallationException("Le groupe ne peut pas être retiré car il possède des lignes");
		return groupeLignes.remove(groupe);
	}

}
