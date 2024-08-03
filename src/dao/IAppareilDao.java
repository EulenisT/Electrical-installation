package dao;

import java.util.Optional;

import model.appareil.Appareil;

public interface IAppareilDao extends IDAO<Appareil, String> {

	Optional<Appareil> getFromID(String id);
}
