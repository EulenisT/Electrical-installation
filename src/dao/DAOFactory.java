package dao;

import java.sql.Connection;

import dao.exception.InstallationException;

/**
 * Fabrique abstraite
 * 
 * @author Didier
 *
 */
public abstract class DAOFactory {
//Type de persistances
	public enum TypePersistance {
		FIREBIRD, H2
	}

	// DAOs que doit fournir chaque fabrique concrète
	public abstract ICableDao getCableDAO();

	public abstract IDisjoncteurDao getDisjoncteurDAO();

	public abstract IDisjoncteurDiffDao getDisjoncteurDiffDAO();

	public abstract IInterrupteurDao getInterrupteurDAO();

	public abstract IPriseDao getPriseDAO();

	public abstract ICompteurDao getCompteurDAO();

	public abstract IInstallationDao getInstallationDAO();

	public abstract IAppareilDao getAppareilDAO();

	public abstract IBlocDao getBlocDAO();

	public abstract IGroupeLigneDao getGroupeLigneDAO();

	public abstract ILigneDao getLigneDAO();
	
	public abstract ILampeDao getLampeDAO();
	
	public abstract ITelerupteurDao getTelerupteurDAO();

	// Méthode statique qui génère des fabriques concrètes
	public static DAOFactory getDAOFactory(TypePersistance typeP, Connection connect) {
		switch (typeP) {
		case FIREBIRD:
			return new FBDAOFactory(connect);
//		case H2:
//			return new H2DAOFactory(connect);

		default:
			return null;
		}
	}

// Retourne la connection SQL
	public abstract Connection getConnection();

// Permet de transformer une exception SQL vers une exception Installation
	protected abstract void dispatchException(Exception e, String detail) throws InstallationException;

}
