package dao;

import java.util.List;
import java.util.Optional;

import model.appareil.CodeApp;
import model.appareil.DisjoncteurDiff;

public interface IDisjoncteurDiffDao extends IDAO<DisjoncteurDiff, String> {

	Optional<DisjoncteurDiff> getFromID(String id);

	List<CodeApp> getListeCode();
}
