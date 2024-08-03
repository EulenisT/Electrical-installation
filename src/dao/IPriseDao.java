package dao;

import java.util.List;
import java.util.Optional;

import model.appareil.CodeApp;
import model.appareil.Prise;

public interface IPriseDao extends IDAO<Prise, String> {

	Optional<Prise> getFromID(String id);

	List<CodeApp> getListeCode();
}
