package dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import dao.exception.LigneBlocSizeException;
import lombok.extern.slf4j.Slf4j;
import model.Ligne;
import model.appareil.Disjoncteur;
import model.appareil.Cable;

@Slf4j
public class SQLLigneDao implements ILigneDao {

	private static final String SQL_GET_FROM_ID = """
			SELECT NUM_LIG, CODE_LIG, INTERNE_LIG, FKCABLE_LIG, FKFUSIBLE_LIG FROM TLIGNE WHERE NUM_LIG = ?
			""";

	private static final String SQL_GET_LISTE_FROM_GROUPE = """
			SELECT NUM_LIG, CODE_LIG, INTERNE_LIG, FKCABLE_LIG, FKFUSIBLE_LIG FROM TLIGNE
			WHERE FKGROUPE_LIG= ?
			ORDER BY ORDRE_LIG ASC
			""";

	private static final String SQL_INSERT_UPDATE = """
			UPDATE OR INSERT INTO TLIGNE ( NUM_LIG, ORDRE_LIG, CODE_LIG,  INTERNE_LIG, FKGROUPE_LIG, FKCABLE_LIG, FKFUSIBLE_LIG) VALUES (?,?,?,?,?,?,?)
			""";

	private static final String SQL_DELETE = """
			DELETE FROM TLIGNE WHERE NUM_LIG = ?
			""";

	private Connection connection;
	private DAOFactory fabrique;

	/**
	 * Construction du dao, fourni la fabrique pour avoir la connexion
	 * 
	 * @param fabrique
	 */
	public SQLLigneDao(DAOFactory fabrique) {
		this.fabrique = fabrique;
		this.connection = fabrique.getConnection();
	}

	@Override
	public Optional<Ligne> getFromID(Integer lig) {

		Ligne obj = null;

		try (var q = connection.prepareStatement(SQL_GET_FROM_ID)) {
			q.setInt(1, lig);
			ResultSet rs = q.executeQuery();

			if (rs.next()) {

				var cable = fabrique.getCableDAO().getFromID(rs.getString("FKCABLE_LIG")).orElse(null);

				var fusible = fabrique.getDisjoncteurDAO().getFromID(rs.getString("FKFUSIBLE_LIG")).orElse(null);

				obj = new Ligne(lig, rs.getString("CODE_LIG"), rs.getBoolean("INTERNE_LIG"), cable, fusible);

			}
		} catch (SQLException e) {
			log.error("Problème GetFromID " + e.getMessage());
		}

		return Optional.ofNullable(obj);
	}

	@Override
	public List<Ligne> getListeFromGroupe(Integer grp) {
		List<Ligne> liste = new ArrayList<>();

		try (var q = connection.prepareStatement(SQL_GET_LISTE_FROM_GROUPE)) {
			q.setInt(1, grp);

			try (ResultSet rs = q.executeQuery()) {
				while (rs.next()) {

					int numLig = rs.getInt("NUM_LIG");
					String fkFusible = rs.getString("FKFUSIBLE_LIG");
					String fkCable = rs.getString("FKCABLE_LIG");

					Optional<Disjoncteur> fusibleOptional = fabrique.getDisjoncteurDAO().getFromID(fkFusible);
					var fusible = fusibleOptional.orElse(null);

					Optional<Cable> cableOptional = fabrique.getCableDAO().getFromID(fkCable);
					var cable = cableOptional.orElse(null);

					Ligne lig = new Ligne(numLig, rs.getString("CODE_LIG"), rs.getBoolean("INTERNE_LIG"), cable,
							fusible);

					var blocs = fabrique.getBlocDAO().getBlocFromLigne(lig.getNum());
					blocs.forEach(b -> {
						try {
							lig.addBloc(b);
						} catch (LigneBlocSizeException e) {
							log.error("Problème de chargement de la ligne: Nombre de blocs excède la limite", e);
						}
					});
					liste.add(lig);
				}
			}
		} catch (SQLException e) {
			log.error("Problème GetListeFromGroupe " + e.getMessage());
		}

		return liste;
	}

	/**
	 * Insert/Update Ligne
	 */
	@Override
	public void insertUpdateLigne(List<Ligne> lignes) throws Exception {
		try (var q1 = connection.prepareStatement(SQL_INSERT_UPDATE)) {
			int i = 0;

			for (Ligne lig : lignes) {
				q1.setInt(1, lig.getNum());
				q1.setInt(2, i);
				q1.setString(3, lig.getCode());
				q1.setBoolean(4, lig.isInterne());
				q1.setInt(5, lig.getNum() / 100);
				q1.setString(6, lig.getCable() != null ? lig.getCable().code() : null);
				q1.setString(7, lig.getFusible() != null ? lig.getFusible().getCode() : null);
				q1.addBatch();
				i++;

				fabrique.getBlocDAO().insertUpdateBlocs(lig.getBlocs());
			}

			int[] res = q1.executeBatch();
			log.info("Update Lignes nb: " + res.length);

			if (!connection.getAutoCommit())
				connection.commit();

		} catch (SQLException e) {

			if (!connection.getAutoCommit())
				connection.rollback();
			log.error("Problème Insert/update Lignes " + e.getMessage());

		}
	}

	/**
	 * Supprime la ligne
	 */
	@Override
	public boolean delete(Ligne ligne) throws Exception {
		try (var q1 = connection.prepareStatement(SQL_DELETE)) {

			q1.setInt(1, ligne.getNum());

			q1.executeUpdate();
			log.info("Suppression de la ligne:" + ligne.getNum());

			if (!connection.getAutoCommit())
				connection.commit();

		} catch (SQLException e) {

			if (!connection.getAutoCommit())
				connection.rollback();
			log.error("Problème delete Lignes " + e.getMessage());
		}
		return true;
	}

}