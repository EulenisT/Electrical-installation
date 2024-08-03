package dao;

import java.util.List;
import java.util.Optional;

import model.appareil.CodeApp;
import model.appareil.Lampe;

public interface ILampeDao extends IDAO<Lampe, String> {

	Optional<Lampe> getFromID(String id);

	List<CodeApp> getListeCode();
}
