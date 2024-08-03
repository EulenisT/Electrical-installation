package dao;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import model.appareil.CodeApp;
import model.appareil.Interrupteur;

public class CacheInterrupteurDAO implements IInterrupteurDao {

	private final IInterrupteurDao sqlInterrupteurDao;
	private final Map<String, Interrupteur> cache;

	public CacheInterrupteurDAO(IInterrupteurDao sqlInterrupteurDao) {
		this.sqlInterrupteurDao = sqlInterrupteurDao;
		this.cache = new ConcurrentHashMap<>();
	}

	@Override
	public Optional<Interrupteur> getFromID(String id) {
		// Vérifier si l'interrupteur est déjà dans le cache
		Interrupteur interrupteur = cache.get(id);
		if (interrupteur != null) {
			return Optional.of(interrupteur);
		}

		// Si pas dans le cache
		Optional<Interrupteur> interrupteurOpt = sqlInterrupteurDao.getFromID(id);
		// Mettre à jour le cache
		interrupteurOpt.ifPresent(d -> cache.put(id, d));

		return interrupteurOpt;
	}

	@Override
	public List<CodeApp> getListeCode() {
		return sqlInterrupteurDao.getListeCode();
	}
}
