package dao;

import java.util.List;
import java.util.Optional;

import model.appareil.CodeApp;
import model.appareil.Compteur;

public interface ICompteurDao extends IDAO<Compteur, String> {

	Optional<Compteur> getFromID(String id);

	List<CodeApp> getListeCode();
}
