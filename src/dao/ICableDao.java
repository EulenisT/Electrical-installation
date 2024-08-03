package dao;

import java.util.List;

import model.appareil.Cable;
import model.appareil.CodeApp;

public interface ICableDao extends IDAO<Cable, String> {

	/**
	 * Fourni la liste des câbles d'un certain type
	 * 
	 * @param type
	 * @return
	 */
	List<Cable> getListeFromType(String type);

	List<CodeApp> getListeCode();

	/**
	 * Fourni la liste des câbles
	 */

	List<Cable> getListeCodeCable();
}
