package dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import model.appareil.CodeApp;
import model.appareil.Compteur;

@Slf4j
public class SQLCompteurDao implements ICompteurDao {
	private static final String SQL_GET_FROMID = """
			select c.CODE, c.NOM, c.NBPHASES, c.SVG from VCOMPTEUR c where c.CODE = ?
			""";
	private static final String SQL_LISTE_CODE = """
			SELECT CODE, NOM FROM VCOMPTEUR ORDER BY CODE
			""";

	private Connection connection;

	/**
	 * Construction du DAO, fourni la fabrique pour avoir la connexion
	 * 
	 * @param fabrique
	 */
	public SQLCompteurDao(DAOFactory fabrique) {
		this.connection = fabrique.getConnection();
	}

	@Override
	public Optional<Compteur> getFromID(String id) {
		Compteur obj = null;
		try (var q = connection.prepareStatement(SQL_GET_FROMID)) {
			q.setString(1, id);
			ResultSet rs = q.executeQuery();
			if (rs.next())
				obj = new Compteur(rs.getString("CODE"), rs.getString("NOM"), rs.getInt("nbPhases"),
						rs.getString("SVG"));

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
