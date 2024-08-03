package dao;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import model.appareil.CodeApp;
import model.appareil.Disjoncteur;

public class CacheDisjoncteurDAO implements IDisjoncteurDao {

	private final IDisjoncteurDao sqlDisjoncteurDao;
	private final Map<String, Disjoncteur> cache;

	public CacheDisjoncteurDAO(IDisjoncteurDao sqlDisjoncteurDao) {
		this.sqlDisjoncteurDao = sqlDisjoncteurDao;
		this.cache = new ConcurrentHashMap<>();
	}

	@Override
	public Optional<Disjoncteur> getFromID(String id) {

		// Vérifier si le disjoncteur est déjà dans le cache
		Disjoncteur disjoncteur = cache.get(id);
		if (disjoncteur != null) {
			return Optional.of(disjoncteur);
		}

		// Si pas dans le cache
		Optional<Disjoncteur> disjoncteurOpt = sqlDisjoncteurDao.getFromID(id);
		// Mettre à jour le cache
		disjoncteurOpt.ifPresent(d -> cache.put(id, d));

		return disjoncteurOpt;
	}

	@Override
	public List<CodeApp> getListeCode() {
		return sqlDisjoncteurDao.getListeCode();
	}
}
