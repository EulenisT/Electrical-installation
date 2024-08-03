package dao;

import java.util.List;
import java.util.Optional;

import model.appareil.CodeApp;
import model.appareil.Disjoncteur;

public interface IDisjoncteurDao extends IDAO<Disjoncteur, String> {

	Optional<Disjoncteur> getFromID(String id);

	List<CodeApp> getListeCode();
}
