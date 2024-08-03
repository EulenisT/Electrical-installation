package dao;

import java.util.List;
import java.util.Optional;

import model.Ligne;

public interface ILigneDao {

	// Pour charger les lignes d'un groupe
	List<Ligne> getListeFromGroupe(Integer grp);

	// Pour insérer ou mettre à jour une ligne
	public void insertUpdateLigne(List<Ligne> lig) throws Exception;

	// Pour obtenir une ligne par son ID
	Optional<Ligne> getFromID(Integer num);

	// Pour supprimer une ligne
	boolean delete(Ligne ligne) throws Exception;
}
