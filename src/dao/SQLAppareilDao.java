package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import model.appareil.Appareil;

@Slf4j
public class SQLAppareilDao implements IAppareilDao {
	private static final String SQL_GET_FROMID = """
			SELECT CODE_APP, NOM_APP, NBPHASES_APP, SVG_APP
			FROM TAPPAREIL
			WHERE CODE_APP = ?
			""";

	private Connection connection;

	/**
	 * Construction du dao, fourni la fabrique pour avoir la connexion
	 * 
	 * @param fabrique
	 */
	public SQLAppareilDao(DAOFactory fabrique) {
		this.connection = fabrique.getConnection();
	}

	@Override
	public Optional<Appareil> getFromID(String id) {
		Appareil appareil = null;
		if (id != null && !id.isBlank()) {
			try (PreparedStatement state = connection.prepareStatement(SQL_GET_FROMID)) {
				state.setString(1, id);
				try (ResultSet rs = state.executeQuery()) {
					if (rs.next()) {
						appareil = new Appareil(id, rs.getString("NOM_APP"), rs.getInt("NBPHASES_APP"),
								rs.getString("SVG_APP"));
					}
				}
			} catch (SQLException e) {
				log.error("Problème Appareil GetFromID " + e.getMessage());
			}
		}
		return Optional.ofNullable(appareil);
	}

}
