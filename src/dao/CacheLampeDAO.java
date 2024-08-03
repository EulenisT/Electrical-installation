package dao;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import model.appareil.CodeApp;
import model.appareil.Lampe;

public class CacheLampeDAO implements ILampeDao{

	 private final ILampeDao sqlLampeDao;
	 private final Map<String, Lampe> cache;

	    public CacheLampeDAO(ILampeDao sqlLampeDao) {
	        this.sqlLampeDao = sqlLampeDao;
	        this.cache = new ConcurrentHashMap<>(); 
	    }

	    @Override
	    public Optional<Lampe> getFromID(String id) {
	        // Vérifier si la lampe est déjà dans le cache
	        Lampe lampe = cache.get(id);
	        if (lampe != null) {
	            return Optional.of(lampe);
	        }

	        // Si pas dans le cache
	        Optional<Lampe> lampeOpt = sqlLampeDao.getFromID(id);
	        // Mettre à jour le cache
	        lampeOpt.ifPresent(d -> cache.put(id, d)); 

	        return lampeOpt;
	    }

	    @Override
	    public List<CodeApp> getListeCode() {
	        return sqlLampeDao.getListeCode(); 
	    }
	}
