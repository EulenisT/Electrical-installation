package dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import model.appareil.CodeApp;
import model.appareil.Lampe;

@Slf4j
public class SQLLampeDao implements ILampeDao {
	private static final String SQL_GET_FROMID = """
			SELECT l.CODE, l.NOM, l.NBPHASES, l.SVG, l.TENSION FROM VLAMPE l WHERE CODE = ?
			""";
	private static final String SQL_LISTE_CODE = """
			SELECT l.CODE, l.NOM FROM VLAMPE l ORDER BY l.CODE
			""";

	private Connection connection;

	/**
	 * Construction du DAO, fourni la fabrique pour avoir la connexion
	 * 
	 * @param fabrique
	 */
	public SQLLampeDao(DAOFactory fabrique) {
		this.connection = fabrique.getConnection();
	}

	@Override
	public Optional<Lampe> getFromID(String id) {
		Lampe obj = null;
		try (var q = connection.prepareStatement(SQL_GET_FROMID)) {
			q.setString(1, id);
			ResultSet rs = q.executeQuery();
			if (rs.next())
				obj = new Lampe(rs.getString("CODE"), rs.getString("NOM"), rs.getInt("nbPhases"), rs.getString("SVG"),
						rs.getInt("TENSION"));

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