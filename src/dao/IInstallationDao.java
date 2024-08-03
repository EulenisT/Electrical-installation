package dao;

import java.util.List;

/**
 *@author Eulenis Tarazona
 */

import java.util.Optional;

import model.Installation;
import model.RInstallation;

public interface IInstallationDao extends IDAO<Installation, Integer> {

	/**
	 * Permet d'insérer ou mettre à jour les Intallations
	 * 
	 * @param
	 * @throws Exception
	 */
	Optional<Installation> getFromID(Integer id);

	public Installation insert(Installation installation) throws Exception;

	public boolean update(Installation installation) throws Exception;
	
	List<RInstallation> getListeRecord();
}
