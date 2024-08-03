package dao;

import java.util.List;
import java.util.Optional;

import model.appareil.CodeApp;
import model.appareil.Interrupteur;

public interface IInterrupteurDao extends IDAO<Interrupteur, String> {
	Optional<Interrupteur> getFromID(String id);

	List<CodeApp> getListeCode();
}
