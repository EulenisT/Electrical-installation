package dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import model.appareil.CodeApp;
import model.appareil.Interrupteur;

@Slf4j
public class SQLInterrupteurDao implements IInterrupteurDao {
	private static final String SQL_GET_FROMID = """
			select i.CODE, i.NOM, i.NBPHASES, i.SVG, i.NBCONTACTS, i.NBDIRECTIONS from VINTERRUPTEUR i where i.CODE = ?
			""";
	private static final String SQL_LISTE_CODE = """
			SELECT CODE, NOM FROM VINTERRUPTEUR ORDER BY CODE
			""";

	private Connection connection;

	/**
	 * Construction du DAO, fourni la fabrique pour avoir la connexion
	 * 
	 * @param fabrique
	 */
	public SQLInterrupteurDao(DAOFactory fabrique) {
		this.connection = fabrique.getConnection();
	}

	@Override
	public Optional<Interrupteur> getFromID(String id) {
		Interrupteur obj = null;
		try (var q = connection.prepareStatement(SQL_GET_FROMID)) {
			q.setString(1, id);
			ResultSet rs = q.executeQuery();
			if (rs.next())
				obj = new Interrupteur(rs.getString("CODE"), rs.getString("NOM"), rs.getInt("nbPhases"),
						rs.getString("SVG"), rs.getInt("NBCONTACTS"), rs.getInt("NBDIRECTIONS"));

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
