package dao;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import model.appareil.CodeApp;
import model.appareil.Compteur;


public class CacheCompteurDAO implements ICompteurDao {

	private final ICompteurDao sqlCompteurDao;
	private final Map<String, Compteur> cache;

	public CacheCompteurDAO(ICompteurDao sqlCompteurDao) {
		this.sqlCompteurDao = sqlCompteurDao;
		this.cache = new ConcurrentHashMap<>();
	}

	@Override
	public Optional<Compteur> getFromID(String id) {

		// Vérifier si le compteur est déjà dans le cache
		Compteur compteur = cache.get(id);
		if (compteur != null) {
			return Optional.of(compteur);
		}

		// Si pas dans le cache
		Optional<Compteur> compteurOpt = sqlCompteurDao.getFromID(id);
		// Mettre à jour le cache
		compteurOpt.ifPresent(d -> cache.put(id, d));

		return compteurOpt;
	}

	@Override
	public List<CodeApp> getListeCode() {
		return sqlCompteurDao.getListeCode();
	}
}
