package dao;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import model.appareil.CodeApp;
import model.appareil.DisjoncteurDiff;

public class CacheDisjoncteurDiffDAO implements IDisjoncteurDiffDao {

	private final IDisjoncteurDiffDao sqlDisjoncteurDiffDao;
	private final Map<String, DisjoncteurDiff> cache;

	public CacheDisjoncteurDiffDAO(IDisjoncteurDiffDao sqlDisjoncteurDiffDao) {
		this.sqlDisjoncteurDiffDao = sqlDisjoncteurDiffDao;
		this.cache = new ConcurrentHashMap<>();
	}

	@Override
	public Optional<DisjoncteurDiff> getFromID(String id) {

		// Vérifier si le disjoncteurDiff est déjà dans le cache
		DisjoncteurDiff disjoncteurDiff = cache.get(id);
		
		if (disjoncteurDiff != null) {
			return Optional.of(disjoncteurDiff);
		}

		// Si pas dans le cache
		Optional<DisjoncteurDiff> disjoncteurOpt = sqlDisjoncteurDiffDao.getFromID(id);
		// Mettre à jour le cache
		disjoncteurOpt.ifPresent(d -> cache.put(id, d));

		return disjoncteurOpt;
	}

	@Override
	public List<CodeApp> getListeCode() {
		return sqlDisjoncteurDiffDao.getListeCode();
	}
}