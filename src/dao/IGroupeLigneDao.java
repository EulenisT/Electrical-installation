package dao;

import java.util.List;

import model.GroupeLigne;

public interface IGroupeLigneDao extends IDAO<GroupeLigne, Integer> {

	// Pour charger la liste des groupes d'une installation
	List<GroupeLigne> getListeFromInstallation(Integer inst);

	// Pour sauvegarder ou mettre à jour le groupe d'une installation
	void insertUpdateGroupeLigne(List<GroupeLigne> grp) throws Exception;

}
