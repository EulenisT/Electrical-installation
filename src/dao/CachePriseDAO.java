package dao;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import model.appareil.CodeApp;
import model.appareil.Prise;

public class CachePriseDAO implements IPriseDao {

	private final IPriseDao sqlPriseDao;
	private final Map<String, Prise> cache;

	public CachePriseDAO(IPriseDao sqlPriseDao) {
		this.sqlPriseDao = sqlPriseDao;
		this.cache = new ConcurrentHashMap<>();
	}

	@Override
	public Optional<Prise> getFromID(String id) {
		// Vérifier si la prise est déjà dans le cache
		Prise prise = cache.get(id);
		if (prise != null) {
			return Optional.of(prise);
		}

		// Si pas dans le cache
		Optional<Prise> priseOpt = sqlPriseDao.getFromID(id);
		// Mettre à jour le cache
		priseOpt.ifPresent(d -> cache.put(id, d));

		return priseOpt;
	}

	@Override
	public List<CodeApp> getListeCode() {
		return sqlPriseDao.getListeCode();
	}
}
