package dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import model.appareil.CodeApp;
import model.appareil.DisjoncteurDiff;

@Slf4j
public class SQLDisjoncteurDiffDao implements IDisjoncteurDiffDao {
	private static final String SQL_GET_FROMID = """
			select f.CODE, f.NOM, f.NBPHASES, f.SVG, f.AMPERAGE, f.SENSIBILITE FROM VDISJONCTEURDIFF f where f.CODE = ?;
			""";
	private static final String SQL_LISTE_CODE = """
			SELECT CODE, NOM FROM VDISJONCTEURDIFF ORDER BY CODE;
			""";

	private Connection connection;

	/**
	 * Construction du DAO, fourni la fabrique pour avoir la connexion
	 * 
	 * @param fabrique
	 */
	public SQLDisjoncteurDiffDao(DAOFactory fabrique) {
		this.connection = fabrique.getConnection();
	}

	@Override
	public Optional<DisjoncteurDiff> getFromID(String id) {
		DisjoncteurDiff obj = null;
		try (var q = connection.prepareStatement(SQL_GET_FROMID)) {
			q.setString(1, id);
			try (ResultSet rs = q.executeQuery()) {
				if (rs.next()) {
					obj = new DisjoncteurDiff(rs.getString("CODE"), rs.getString("NOM"), rs.getInt("NBPHASES"),
							rs.getString("SVG"), rs.getInt("AMPERAGE"), rs.getInt("SENSIBILITE"));
				}
			}
		} catch (SQLException e) {
			log.error("Problème GetFromID " + e.getMessage());
		}
		return Optional.ofNullable(obj);
	}

	public List<CodeApp> getListeCode() {
		List<CodeApp> liste = new ArrayList<>();
		try (var q = connection.prepareStatement(SQL_LISTE_CODE)) {

			ResultSet rs = q.executeQuery();
			while (rs.next())
				liste.add(new CodeApp(rs.getString("CODE"), rs.getString("NOM")));

		} catch (SQLException e) {
			log.error("Problème GetListeCode " + e.getMessage());
		}
		return liste;
	}
}
