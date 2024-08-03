package dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import model.appareil.CodeApp;
import model.appareil.Prise;

@Slf4j
public class SQLPriseDao implements IPriseDao {

	private static final String SQL_GET_FROMID = """
			select p.CODE, p.NOM, p.NBPHASES, p.SVG, p.TERRE, p.PROTECTIONENF, p.IP44 from VPRISE p where p.CODE = ?;
			""";
	private static final String SQL_LISTE_CODE = """
			SELECT CODE, NOM FROM VPRISE ORDER BY CODE
			""";

	private Connection connection;

	/**
	 * Construction du DAO, fourni la fabrique pour avoir la connexion
	 * 
	 * @param fabrique
	 */
	public SQLPriseDao(DAOFactory fabrique) {
		this.connection = fabrique.getConnection();
	}

	@Override
	public Optional<Prise> getFromID(String id) {
		Prise obj = null;
		try (var q = connection.prepareStatement(SQL_GET_FROMID)) {
			q.setString(1, id);
			ResultSet rs = q.executeQuery();
			if (rs.next())
				obj = new Prise(rs.getString("code"), rs.getString("nom"), rs.getInt("nbPhases"), rs.getString("svg"),
						rs.getBoolean("terre"), rs.getBoolean("protectionEnf"), rs.getBoolean("ip44"));

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
