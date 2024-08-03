package dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import model.appareil.CodeApp;
import model.appareil.Telerupteur;

@Slf4j
public class SQLTelerupteurDao implements ITelerupteurDao {
	private static final String SQL_GET_FROMID = """
			SELECT l.CODE, l.NOM, l.NBPHASES, l.SVG, l.TENSION FROM VTELERUPTEUR l WHERE CODE = ?
			""";
	private static final String SQL_LISTE_CODE = """
			SELECT l.CODE, l.NOM FROM VTELERUPTEUR l ORDER BY l.CODE
			""";

	private Connection connection;

	
	public SQLTelerupteurDao(DAOFactory fabrique) {
		this.connection = fabrique.getConnection();
	}

	@Override
	public Optional<Telerupteur> getFromID(String id) {
		Telerupteur tel = null;
		try (var q = connection.prepareStatement(SQL_GET_FROMID)) {
			q.setString(1, id);
			ResultSet rs = q.executeQuery();
			if (rs.next())
				tel = new Telerupteur(rs.getString("CODE"), rs.getString("NOM"), rs.getInt("nbPhases"), rs.getString("SVG"),
						rs.getInt("TENSION"));

		} catch (SQLException e) {
			log.error("Problème GetFromID " + e.getMessage());
		}
		return Optional.ofNullable(tel);
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