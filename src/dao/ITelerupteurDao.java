package dao;

import java.util.List;
import java.util.Optional;

import model.appareil.CodeApp;
import model.appareil.Telerupteur;


public interface ITelerupteurDao extends IDAO<Telerupteur, String> {

	Optional<Telerupteur> getFromID(String id);

	List<CodeApp> getListeCode();
}
