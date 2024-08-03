package dao;

import java.sql.Connection;
import java.sql.SQLException;

import dao.exception.InstallationException;
import dao.exception.PKException;
import dao.exception.ValidationException;

/**
 * Fabrique concrète pour FB
 * 
 * @author Didier
 *
 */
//@Slf4j
public class FBDAOFactory extends DAOFactory {

	private Connection connection;

	private ICableDao cableDAO = null;
	private IDisjoncteurDao disjoncteurDAO = null;
	private IDisjoncteurDiffDao disjoncteurDiffDAO = null;
	private IInterrupteurDao interrupteurDAO = null;
	private IPriseDao priseDAO = null;
	private ICompteurDao compteurDAO = null;
	private IInstallationDao installationDAO = null;
	private IAppareilDao appareilDAO = null;
	private IBlocDao blocDAO = null;
	private IGroupeLigneDao groupeLigneDAO = null;
	private ILigneDao ligneDAO = null;
	private ILampeDao lampeDAO = null;
	private ITelerupteurDao telerupteurDAO = null;

	/**
	 * @param connection
	 */
	public FBDAOFactory(Connection connection) {
		this.connection = connection;
	}

//crée un cableDao si pas encore fait

	@Override
	public ICableDao getCableDAO() {
		if (cableDAO == null) {
			cableDAO = new SQLCableDao(this);
		}
		return cableDAO;
	}

	
	@Override
	public IDisjoncteurDiffDao getDisjoncteurDiffDAO() {
		if (disjoncteurDiffDAO == null) {
			disjoncteurDiffDAO = new SQLDisjoncteurDiffDao(this);
		}
		return disjoncteurDiffDAO;
	}	
	
	
	@Override
	public IDisjoncteurDao getDisjoncteurDAO() {
		if (disjoncteurDAO == null) {
			disjoncteurDAO = new SQLDisjoncteurDao(this);
		}
		return disjoncteurDAO;
	}	
	


//crée un interrupteurDao si pas encore fait

	@Override
	public IInterrupteurDao getInterrupteurDAO() {
		if (interrupteurDAO == null) {
			IInterrupteurDao sqlInterrupteurDao = new SQLInterrupteurDao(this);
			interrupteurDAO = new CacheInterrupteurDAO(sqlInterrupteurDao);
		}
		return interrupteurDAO;
	}

//crée une priseDao si pas encore fait

	@Override
	public IPriseDao getPriseDAO() {
		if (priseDAO == null) {
			IPriseDao sqlPriseDao = new SQLPriseDao(this);
			priseDAO = new CachePriseDAO(sqlPriseDao);
		}
		return priseDAO;
	}

//crée un compteurDao si pas encore fait

	@Override
	public ICompteurDao getCompteurDAO() {
		if (compteurDAO == null) {
			ICompteurDao sqlCompteurDao = new SQLCompteurDao(this);
			compteurDAO = new CacheCompteurDAO(sqlCompteurDao);
		}
		return compteurDAO;
	}

//crée un installationDao si pas encore fait
	@Override
	public IInstallationDao getInstallationDAO() {
		if (installationDAO == null) {
			installationDAO = new SQLInstallationDao(this);
		}
		return installationDAO;
	}

//crée un appareilDao si pas encore fait
	@Override
	public IAppareilDao getAppareilDAO() {
		if (appareilDAO == null) {
			appareilDAO = new SQLAppareilDao(this);
		}
		return appareilDAO;
	}

//crée un blocDao si pas encore fait
	@Override
	public IBlocDao getBlocDAO() {
		if (blocDAO == null) {
			blocDAO = new SQLBlocDao(this);
		}
		return blocDAO;
	}

	// crée un groupeLigneDao si pas encore fait
	@Override
	public IGroupeLigneDao getGroupeLigneDAO() {
		if (groupeLigneDAO == null) {
			groupeLigneDAO = new SQLGroupeLigneDao(this);
		}
		return groupeLigneDAO;
	}

	// crée un ligneDao si pas encore fait
	@Override
	public ILigneDao getLigneDAO() {
		if (ligneDAO == null) {
			ligneDAO = new SQLLigneDao(this);
		}
		return ligneDAO;
	}

	// crée un lampeDao si pas encore fait
	@Override
	public ILampeDao getLampeDAO() {
		if (lampeDAO == null) {
			ILampeDao sqlLampeDao = new SQLLampeDao(this);
			lampeDAO = new CacheLampeDAO(sqlLampeDao);
		}
		return lampeDAO;
	}
	
	
	// crée un telerupteurDao si pas encore fait
		@Override
		public ITelerupteurDao getTelerupteurDAO() {
			if (telerupteurDAO == null) {
				telerupteurDAO = new SQLTelerupteurDao(this);
			}
			return telerupteurDAO;
		}
	
	

	@Override
	public Connection getConnection() {
		return connection;
	}

	@Override
	protected void dispatchException(Exception e, String detail) throws InstallationException {
		if (e instanceof SQLException exc) {
			switch (exc.getErrorCode()) {
			case 335544665 -> throw new PKException("Problème d'identifiant", detail);
			case 335544347, 335544914 -> throw new ValidationException("Problème de validation: ", detail);
			default -> throw new InstallationException("Erreur inconnue: " + exc.getMessage());
			}
		}
		throw new InstallationException("Erreur inconnue: " + e.getMessage());
	}

}
