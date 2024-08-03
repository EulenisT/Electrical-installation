package dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import model.appareil.Cable;
import model.appareil.CodeApp;

@Slf4j
public class SQLCableDao implements ICableDao {
	private static final String SQL_GET_FROMID = """
			SELECT NBCONDUCTEURS_CAB, TERRE_CAB,SECTION_CAB,TYPE_CAB
			FROM TCABLE WHERE CODE_CAB = ?
			""";
	private static final String SQL_FIND_FROM_TYPE = """
			SELECT CODE_CAB, NBCONDUCTEURS_CAB, TERRE_CAB, SECTION_CAB
			FROM TCABLE WHERE TYPE_CAB=?
			""";

	private static final String SQL_INSERT = """
			 INSERT INTO TCABLE(CODE_CAB, NBCONDUCTEURS_CAB, TERRE_CAB, SECTION_CAB, TYPE_CAB)
			 VALUES(?,?,?,?,?)
			""";

	private static final String SQL_LIST_CODE = """
			SELECT r.CODE_CAB, r.TYPE_CAB||(r.NBCONDUCTEURS_CAB-1)||+IIF(r.TERRE_CAB,'+G ',' ')||r.SECTION_CAB
			as NOM FROM TCABLE r
			order by r.TYPE_CAB desc,r.NBCONDUCTEURS_CAB, r.SECTION_CAB
			""";
	
	private static final String SQL_LIST_CODE_CABLE = """
			SELECT CODE_CAB FROM TCABLE order by TYPE_CAB desc, SECTION_CAB
			""";
	

	private Connection connection;
	private DAOFactory fabrique;

	/**
	 * Construction du dao, 
	 * fourni la fabrique pour avoir la connexion
	 * @param fabrique
	 */
	public SQLCableDao(DAOFactory fabrique) {
		this.fabrique = fabrique;
		this.connection = fabrique.getConnection();
	}

	/**
	 * 
	 */
	@Override
	public Optional<Cable> getFromID(String id) {
		Cable obj = null;
		try (var q = connection.prepareStatement(SQL_GET_FROMID)) {
			q.setString(1, id);
			ResultSet rs = q.executeQuery();
			if (rs.next())
				obj = new Cable(id, rs.getShort("NBCONDUCTEURS_CAB"), rs.getBoolean("TERRE_CAB"),
						rs.getFloat("SECTION_CAB"), rs.getString("TYPE_CAB").trim());

		} catch (SQLException e) {
			log.error("Problème GetFromID " + e.getMessage());
		}
		return Optional.ofNullable(obj);
	}

	@Override
	public List<Cable> getListeFromType(String type) {
		List<Cable> liste = new ArrayList<>();
		try (var q = connection.prepareStatement(SQL_FIND_FROM_TYPE)) {
			q.setString(1, type);
			ResultSet rs = q.executeQuery();
			while (rs.next())
				liste.add(new Cable(rs.getString("CODE_CAB"), rs.getShort("NBCONDUCTEURS_CAB"),
						rs.getBoolean("TERRE_CAB"), rs.getFloat("SECTION_CAB"), type));

		} catch (SQLException e) {
			log.error("Problème GetListeFromType " + e.getMessage());
		}
		return liste;
	}
	
	@Override
	public List<CodeApp> getListeCode() {
		List<CodeApp> liste = new ArrayList<>();
		try (var q = connection.prepareStatement(SQL_LIST_CODE)) {
			ResultSet rs = q.executeQuery();
			while (rs.next())
				liste.add(new CodeApp(rs.getString("CODE_CAB"), rs.getString("NOM")));

		} catch (SQLException e) {
			log.error("Problème GetListeCode " + e.getMessage());
		}
		return liste;
	}

	@Override
	public Cable insert(Cable obj) throws Exception {

		try (var q = connection.prepareStatement(SQL_INSERT)) {
			q.setString(1, obj.code().trim().toUpperCase());
			q.setShort(2, obj.NbConducteurs());
			q.setBoolean(3, obj.terre());
			q.setFloat(4, obj.section());
			q.setString(5, obj.type().trim());

			q.executeUpdate();
			// fait le commit si pas auto-commit
			if (!connection.getAutoCommit())
				connection.commit();
			log.info("Insertion d'un câble :" + obj.code());
		} catch (SQLException e) {
			// rollback si pas auto-commit
			if (!connection.getAutoCommit())
				connection.rollback();
			log.error("Problème d'insertion d'un câble: " + e.getMessage());
			// transformer l'exception SQL en une exception Electrique
			// recherche dans le message XXX_TYP
			String champ = SQLCableDao.rechercheChamp(e.getMessage(), "_CAB");
			fabrique.dispatchException(e, champ);
		}

		return obj;
	}

	/**
	 * Recherche le nom du champ dans le message d'erreur
	 * @param message
	 * @param finChamp la terminaison d'un champ "_CAB" par exemple
	 * @return le nom du champ ou ""
	 */
	private static String rechercheChamp(String message, String finChamp) {
		int i = message.indexOf(finChamp);
		int d = i;
		while (d > 0 && message.charAt(d) != '"')
			d--;
		return d >= 0 ? message.substring(d + 1, i + 4) : "AUTRE";
	}
	
		@Override
		public List<Cable> getListeCodeCable() {
			List<Cable> liste = new ArrayList<>();
			try (var q = connection.prepareStatement(SQL_LIST_CODE_CABLE)) {
				ResultSet rs = q.executeQuery();
				while (rs.next())
					liste.add(new Cable(rs.getString("CODE_CAB"), rs.getShort("NBCONDUCTEURS_CAB"),
							rs.getBoolean("TERRE_CAB"), rs.getFloat("SECTION_CAB"), rs.getString("TYPE_CAB")));
	
			} catch (SQLException e) {
				log.error("Problème GetListeCodeCable " + e.getMessage());
			}
			return liste;
		}

}
