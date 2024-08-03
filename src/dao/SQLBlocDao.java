package dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import model.Bloc;
import model.appareil.Appareil;
import model.appareil.Appareil.Classe;

@Slf4j
public class SQLBlocDao implements IBlocDao {

	private static final String SQL_GET_FROM_LIGNE = """
			SELECT NUM_BLO FROM TBLOC
			WHERE FKLIGNE_BLO = ?
			ORDER BY ORDRE_BLO
			""";

	private static final String SQL_GET_APP_FROM_BLOC = """
			SELECT FKAPPAREIL_BAP,CLASSE_BAP FROM TBLOCAPP
			WHERE FKBLOC_BAP = ?
			ORDER BY ORDRE_BAP ASC
			""";

	private static final String SQL_INSERT_UPDATE = """
			UPDATE OR INSERT INTO TBLOC (NUM_BLO,FKLIGNE_BLO, ORDRE_BLO) VALUES (?,?,?)
			""";

	private static final String SQL_INSERT_UPDATE_APP = """
			UPDATE OR INSERT INTO TBLOCAPP (FKAPPAREIL_BAP,FKBLOC_BAP,
			ORDRE_BAP, CLASSE_BAP) VALUES (?,?,?,?)
			""";
	private static final String SQL_DELETE_APP = """
			DELETE FROM TBLOCAPP WHERE FKBLOC_BAP = ?
			""";
	private static final String SQL_DELETE = """
			DELETE FROM TBLOC WHERE NUM_BLO = ?
			""";
	private Connection connection;
	private DAOFactory fabrique;

	/**
	 * Construction du dao, fourni la fabrique pour avoir la connexion
	 * 
	 * @param fabrique
	 */
	public SQLBlocDao(DAOFactory fabrique) {
		this.fabrique = fabrique;
		this.connection = fabrique.getConnection();
	}

	@Override
	public List<Bloc> getBlocFromLigne(Integer ligne) {
		List<Bloc> liste = new ArrayList<Bloc>();
		try (var q = connection.prepareStatement(SQL_GET_FROM_LIGNE)) {
			Bloc bloc;
			Integer id;
			q.setInt(1, ligne);
			ResultSet rs = q.executeQuery();
			while (rs.next()) {
				// Création du bloc
				id = rs.getInt("NUM_BLO");
				bloc = new Bloc(id);
				// charge ses appareils
				List<Appareil> listeAppareils = getAppareilFromBloc(id);
				bloc.getAppareils().addAll(listeAppareils);
				liste.add(bloc);
			}
		} catch (SQLException e) {
			log.error("Problème GetListeFromLigne " + e.getMessage());
		}

		return liste;
	}

	/**
	 * Recherche les appareils du bloc
	 * 
	 * @param bloc
	 * @return
	 */
	private List<Appareil> getAppareilFromBloc(Integer bloc) {
		List<Appareil> appareils = new ArrayList<>();
		try (var q = connection.prepareStatement(SQL_GET_APP_FROM_BLOC)) {
			q.setInt(1, bloc);
			String id;
			ResultSet rs = q.executeQuery();
			while (rs.next()) {
				// Création du bloc
				id = rs.getString("FKAPPAREIL_BAP");
				appareils.add(getAppareilFromId(id, Classe.valueOf(rs.getString("CLASSE_BAP"))));
			}
		} catch (SQLException e) {
			log.error("Problème GetListeFromLigne " + e.getMessage());
		}

		return appareils;
	}

	@Override
	public void insertUpdateBlocs(List<Bloc> blocs) throws Exception {
		try (var q1 = connection.prepareStatement(SQL_INSERT_UPDATE);
				var q2 = connection.prepareStatement(SQL_INSERT_UPDATE_APP)) {
			int i = 0;

			// Crée un batch avec tous les groupes
			for (Bloc bloc : blocs) {
				q1.setInt(1, bloc.getId());
				q1.setInt(2, bloc.getId() / 10);
				q1.setInt(3, i);
				q1.addBatch();
				i++;
				// Update des appareils associés aux blocs
				int j = 0;
				for (Appareil app : bloc.getAppareils()) {
					q2.setString(1, app.getCode());
					q2.setInt(2, bloc.getId());
					q2.setInt(3, j);
					q2.setString(4, Classe.fromClasse(app).name());
					q2.addBatch();
					j++;
				}
			}
			int res = q1.executeBatch().length;
			log.info("Update Blocs nb:" + res);

			res = q2.executeBatch().length;
			log.info("Update AppBloc nb:" + res);

			// fait le commit si pas auto-commit
			if (!connection.getAutoCommit())
				connection.commit();

		} catch (SQLException e) {
			// rollback si pas auto-commit
			if (!connection.getAutoCommit())
				connection.rollback();
			log.error("Problème Insert/update Blocs " + e.getMessage());
		}
	}

	/**
	 * A modifier lorsqu'on rajoute des Appareils
	 * 
	 * @param id
	 * @param classe
	 * @return
	 */
	private Appareil getAppareilFromId(String id, Classe classe) {
		Appareil appareil = null;
		Optional<?> app = switch (classe) {
		case Classe.PRISE -> fabrique.getPriseDAO().getFromID(id);
		case Classe.INTERRUPTEUR -> fabrique.getInterrupteurDAO().getFromID(id);
		case Classe.LAMPE -> fabrique.getLampeDAO().getFromID(id); 
		case Classe.TELERUPTEUR -> fabrique.getTelerupteurDAO().getFromID(id);
		case Classe.APPAREIL -> fabrique.getAppareilDAO().getFromID(id);
		default -> throw new IllegalArgumentException("Unexpected value: " + classe);
		};
		appareil = (Appareil) app.orElse(null);

		return appareil;
	}

	/**
	 * Supprime le bloc et ses BlocApp Associés
	 */
	@Override
	public boolean delete(Bloc bloc) throws Exception {
		try (var q1 = connection.prepareStatement(SQL_DELETE); var q2 = connection.prepareStatement(SQL_DELETE_APP)) {
			// supprime les appareils du bloc
			q2.setInt(1, bloc.getId());
			int nb = q2.executeUpdate();
			log.info("Suppression de " + nb + " appareils du bloc:" + bloc.getId());
			// supprime le bloc
			q1.setInt(1, bloc.getId());
			nb = q1.executeUpdate();
			log.info("Suppression du bloc:" + bloc.getId());
			// fait le commit si pas auto-commit
			if (!connection.getAutoCommit())
				connection.commit();

		} catch (SQLException e) {
			// rollback si pas auto-commit
			if (!connection.getAutoCommit())
				connection.rollback();
			log.error("Problème delete Blocs " + e.getMessage());
		}
		return true;
	}
}
