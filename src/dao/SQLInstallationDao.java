package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import dao.exception.PKException;
import lombok.extern.slf4j.Slf4j;
import model.Adresse;
import model.Installation;
import model.RInstallation;

@Slf4j
public class SQLInstallationDao implements IInstallationDao {

	private static final String SQL_GET_FROMID = """
			SELECT NUM_INS, DATE_INS, ADRESSE_INS, CP_INS, VILLE_INS, INSTALLATEUR_INS, TYPE_INS, FKCABLE_INS, FKDISJONCTEUR_INS, FKCOMPTEUR_INS FROM TINSTALLATION WHERE NUM_INS = ?
			""";

	private static final String SQL_INSERT = """
			INSERT INTO TINSTALLATION (DATE_INS, INSTALLATEUR_INS, ADRESSE_INS, CP_INS, VILLE_INS, TYPE_INS, FKCABLE_INS, FKDISJONCTEUR_INS, FKCOMPTEUR_INS)
			VALUES (?,?,?,?,?,?,?,?,?) RETURNING NUM_INS;
			""";

	private static final String SQL_UPDATE = """
			UPDATE TINSTALLATION
			SET DATE_INS = ?, INSTALLATEUR_INS = ?, ADRESSE_INS = ?, CP_INS = ?, VILLE_INS = ?, TYPE_INS = ?, FKCABLE_INS = ?, FKDISJONCTEUR_INS = ?, FKCOMPTEUR_INS = ?
			WHERE NUM_INS = ?;
			""";

	private static final String SQL_LISTE_RECORD = """
			select i.NUM_INS, i.DATE_INS, i.ADRESSE_INS, i.CP_INS, i.VILLE_INS from TINSTALLATION i order BY i.DATE_INS desc
					""";

	private Connection connection;
	private DAOFactory fabrique;

	/**
	 * Construction du dao, fourni la fabrique pour avoir la connexion
	 * 
	 * @param fabrique
	 */
	public SQLInstallationDao(DAOFactory fabrique) {
		this.fabrique = fabrique;
		this.connection = fabrique.getConnection();
	}

	@Override
	public Optional<Installation> getFromID(Integer id) {

		Installation obj = null;
		try (var q = connection.prepareStatement(SQL_GET_FROMID)) {
			q.setInt(1, id);
			ResultSet rs = q.executeQuery();

			if (rs.next()) {
				LocalDate date = rs.getDate("DATE_INS") != null ? rs.getDate("DATE_INS").toLocalDate() : null;
				Adresse adresse = new Adresse(rs.getString("ADRESSE_INS"), rs.getInt("CP_INS"),
						rs.getString("VILLE_INS"));
				String installateur = rs.getString("INSTALLATEUR_INS");
				Installation.TypeLogement typeLogement = Installation.TypeLogement.valueOf(rs.getString("TYPE_INS"));

				obj = new Installation(date, installateur, adresse, typeLogement);
				obj.setId(id);

			}

		} catch (SQLException e) {
			log.error("Problème GetFromID " + e.getMessage());

		}
		return Optional.ofNullable(obj);
	}

	@Override
	public Installation insert(Installation installation) throws Exception {

		try (var q1 = connection.prepareStatement(SQL_INSERT)) {

			if (installation.getDate() == null) {
				q1.setNull(1, Types.DATE);
			} else {
				q1.setDate(1, Date.valueOf(installation.getDate()));
			}

			q1.setString(2, installation.getInstallateur());
			q1.setString(3, installation.getAdresse().getRue());
			q1.setInt(4, installation.getAdresse().getCp());
			q1.setString(5, installation.getAdresse().getVille());
			q1.setString(6, installation.getTypeLogement().name());

			if (installation.getCable() == null) {
				q1.setNull(7, Types.VARCHAR);
			} else {
				q1.setString(7, installation.getCable().code());
			}

			if (installation.getDisjoncteurCompagnie() == null) {
				q1.setNull(8, Types.CHAR);
			} else {
				q1.setString(8, installation.getDisjoncteurCompagnie().getCode());
			}

			if (installation.getCompteur() == null) {
				q1.setNull(9, Types.CHAR);
			} else {
				q1.setString(9, installation.getCompteur().getCode());
			}

			ResultSet rs = q1.executeQuery();
			if (rs.next()) {
				installation.setId(rs.getInt(1));
			}

			fabrique.getGroupeLigneDAO().insertUpdateGroupeLigne(installation.getGroupeLignes());

			if (!connection.getAutoCommit())
				connection.commit();

			log.info("Insertion d'une installation :" + installation.getId());
		} catch (SQLException e) {

			if (!connection.getAutoCommit()) {
				connection.rollback();
				log.error("Problème d'insertion d'un câble: " + e.getMessage());
				fabrique.dispatchException(e, "Exception installation");
			}
		}
		return installation;
	}

	@Override
	public boolean update(Installation installation) throws Exception {

		try (var q = connection.prepareStatement(SQL_UPDATE)) {

			if (installation.getDate() == null) {
				q.setNull(1, Types.DATE);
			} else {
				q.setDate(1, Date.valueOf(installation.getDate()));
			}

			q.setString(2, installation.getInstallateur());
			q.setString(3, installation.getAdresse().getRue());
			q.setInt(4, installation.getAdresse().getCp());
			q.setString(5, installation.getAdresse().getVille());
			q.setString(6, installation.getTypeLogement().name());

			if (installation.getCable() == null) {
				q.setNull(7, Types.VARCHAR);
			} else {
				q.setString(7, installation.getCable().code());
			}

			if (installation.getDisjoncteurCompagnie() == null) {
				q.setNull(8, Types.CHAR);
			} else {
				q.setString(8, installation.getDisjoncteurCompagnie().getCode());
			}

			if (installation.getCompteur() == null) {
				q.setNull(9, Types.CHAR);
			} else {
				q.setString(9, installation.getCompteur().getCode());
			}

			q.setInt(10, installation.getId());

			q.executeUpdate();

			fabrique.getGroupeLigneDAO().insertUpdateGroupeLigne(installation.getGroupeLignes());

			if (!connection.getAutoCommit()) {
				connection.commit();
			}

			log.info("Update d'une installation :" + installation.getId());
		} catch (SQLException e) {

			fabrique.dispatchException(e, "Problème Update d'une installation");

			if (!connection.getAutoCommit()) {
				try {
					connection.rollback();
				} catch (SQLException rollbackException) {
					log.error("Erreur lors du rollback: " + rollbackException.getMessage());
				}
			}

		}
		return installation.getId() != null;
	}

	@Override
	public List<RInstallation> getListeRecord() {
		List<RInstallation> liste = new ArrayList<>();
		try (var q = connection.prepareStatement(SQL_LISTE_RECORD)) {
			ResultSet rs = q.executeQuery();
			while (rs.next()) {
				Integer id = rs.getInt("NUM_INS");
				LocalDate date = rs.getDate("DATE_INS") != null ? rs.getDate("DATE_INS").toLocalDate() : null;
				String adresse = rs.getString("ADRESSE_INS");
				Integer cp = rs.getInt("CP_INS");
				String commune = rs.getString("VILLE_INS");
				liste.add(new RInstallation(id, date, adresse, cp, commune));
			}
		} catch (SQLException e) {
			log.error("Problème GetListeRecord: " + e.getMessage());
		}
		return liste;
	}

}
