package dao;

import java.util.List;

import model.Bloc;

public interface IBlocDao extends IDAO<Bloc, Integer> {
	/**
	 * Charge la liste des Blocs triées d'une ligne
	 * 
	 * @param ligne le numéro de la ligne
	 */
	List<Bloc> getBlocFromLigne(Integer ligne);

	/**
	 * Permet d'insérer ou mettre à jour les Blocs et appareils associés à une ligne
	 * 
	 * @param blocs
	 * @throws Exception
	 */
	void insertUpdateBlocs(List<Bloc> blocs) throws Exception;
}
