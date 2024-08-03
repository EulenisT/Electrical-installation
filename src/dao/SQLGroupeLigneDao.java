package dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import model.GroupeLigne;
import model.Ligne;
import model.appareil.DisjoncteurDiff;

@Slf4j
public class SQLGroupeLigneDao implements IGroupeLigneDao {

	private static final String SQL_GET_FROM_ID = """
			SELECT  NUM_GRO, ORDRE_GRO, FKINSTALLATION_GRO, FKDISJONCTEUR_GRO FROM TGROUPE
			WHERE NUM_GRO = ?
			""";

	private static final String SQL_GET_LISTE_FROM_INSTALLATION = """
			SELECT NUM_GRO, FKINSTALLATION_GRO, FKDISJONCTEUR_GRO FROM TGROUPE WHERE FKINSTALLATION_GRO = ?
			""";

	private static final String SQL_INSERT_UPDATE = """
			UPDATE OR INSERT INTO TGROUPE (NUM_GRO, ORDRE_GRO, FKINSTALLATION_GRO, FKDISJONCTEUR_GRO) VALUES (?,?,?,?)
			""";

	private static final String SQL_DELETE = """
			DELETE FROM TGROUPE WHERE NUM_GRO = ?
			""";

	private Connection connection;
	private DAOFactory fabrique;

	/**
	 * Construction du dao, fourni la fabrique pour avoir la connexion
	 * 
	 * @param fabrique
	 */
	public SQLGroupeLigneDao(DAOFactory fabrique) {
		this.fabrique = fabrique;
		this.connection = fabrique.getConnection();
	}

	@Override
	public Optional<GroupeLigne> getFromID(Integer num) {

		GroupeLigne obj = null;

		try (var q = connection.prepareStatement(SQL_GET_FROM_ID)) {

			q.setInt(1, num);
			ResultSet rs = q.executeQuery();
			if (rs.next())
				obj = new GroupeLigne(rs.getInt("NUM_GRO"));

		} catch (SQLException e) {
			log.error("Problème getFromID " + e.getMessage());
		}

		return Optional.ofNullable(obj);
	}

	@Override
	public List<GroupeLigne> getListeFromInstallation(Integer inst) {
		List<GroupeLigne> liste = new ArrayList<>();

		try (var q = connection.prepareStatement(SQL_GET_LISTE_FROM_INSTALLATION)) {
			q.setInt(1, inst);

			try (ResultSet rs = q.executeQuery()) {
				while (rs.next()) {
					int numGro = rs.getInt("NUM_GRO");
					String fkDisjoncteur = rs.getString("FKDISJONCTEUR_GRO");

					Optional<DisjoncteurDiff> disjoncteurOptional = fabrique.getDisjoncteurDiffDAO()
							.getFromID(fkDisjoncteur);
					var dij = disjoncteurOptional.orElse(null);

					List<Ligne> lignes = fabrique.getLigneDAO().getListeFromGroupe(numGro);

					GroupeLigne grp = new GroupeLigne(numGro, dij, lignes);
					liste.add(grp);
				}
			}
		} catch (SQLException e) {
			log.error("Problème getListeFromInstllation: " + e.getMessage());
		}

		return liste;
	}

	/**
	 * Insert/Update un groupe
	 */

	@Override
	public void insertUpdateGroupeLigne(List<GroupeLigne> grp) throws Exception {
		try (var q1 = connection.prepareStatement(SQL_INSERT_UPDATE)) {
			int i = 0;

			for (GroupeLigne groupe : grp) {
				q1.setInt(1, groupe.getNum());
				q1.setInt(2, i);
				q1.setInt(3, groupe.getNum() / 10);
				q1.setString(4, groupe.getDisjoncteur() != null ? groupe.getDisjoncteur().getCode() : null);
				q1.addBatch();
				i++;

				fabrique.getLigneDAO().insertUpdateLigne(groupe.getLignes());
			}

			int[] res = q1.executeBatch();
			log.info("Update GroupeLigne nb: " + res.length);

			if (!connection.getAutoCommit()) {
				connection.commit();

			}
		} catch (SQLException e) {

			if (!connection.getAutoCommit()) {
				connection.rollback();

			}
			log.error("Problème Insert/update GroupeLignes " + e.getMessage());

		}
	}

	/**
	 * Supprime un groupe
	 */

	@Override
	public boolean delete(GroupeLigne grp) throws Exception {
		try (var q1 = connection.prepareStatement(SQL_DELETE)) {

			q1.setInt(1, grp.getNum());
			q1.executeUpdate();
			log.info("Suppression du groupe:" + grp.getNum());

			if (!connection.getAutoCommit())
				connection.commit();

		} catch (SQLException e) {

			if (!connection.getAutoCommit())
				connection.rollback();
			log.error("Problème delete GroupeLigne " + e.getMessage());
		}
		return true;
	}
}
