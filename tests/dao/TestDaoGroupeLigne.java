package dao;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import dao.DAOFactory.TypePersistance;
import databases.connexion.ConnexionFromFile;
import databases.connexion.ConnexionSingleton;
import databases.uri.Databases;
import model.GroupeLigne;
import utils.DatabaseUtil;

class TestDaoGroupeLigne {
	private static DAOFactory factory;
	private static IGroupeLigneDao groupeLigneDao;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		ConnexionSingleton.setInfoConnexion(new ConnexionFromFile(
				"./ressources/connexion_InstallationsElectriquesTest.properties", Databases.FIREBIRD));

		// Réinitialise la base de données dans son état initial
		DatabaseUtil.executeScriptSQL(ConnexionSingleton.getConnexion(),
				"./ressources/Script_Insert_Installation_1.sql");

		factory = DAOFactory.getDAOFactory(TypePersistance.FIREBIRD, ConnexionSingleton.getConnexion());
		groupeLigneDao = factory.getGroupeLigneDAO();
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {

		// Réinitialise la base de données dans son état initial
		DatabaseUtil.executeScriptSQL(ConnexionSingleton.getConnexion(),
				"./ressources/Script_Insert_Installation_1.sql");

		ConnexionSingleton.liberationConnexion();
	}

	@Test
	void testGetFromID() {

		GroupeLigne gl = new GroupeLigne(10);
		Optional<GroupeLigne> gl1 = groupeLigneDao.getFromID(10);
		assertTrue(gl1.isPresent());
		assertEquals(gl, gl1.get());

	}

	@Test
	void testInsertUpdateGroupeLigne() throws Exception {
		List<GroupeLigne> groupes = new ArrayList<>();

		GroupeLigne groupe1 = new GroupeLigne(70);
		GroupeLigne groupe2 = new GroupeLigne(80);

		groupes.add(groupe1);
		groupes.add(groupe2);

		groupeLigneDao.insertUpdateGroupeLigne(groupes);

	}

	@Test
	void testDeleteLigne() throws Exception {

		GroupeLigne grp = new GroupeLigne(10);

		groupeLigneDao.insertUpdateGroupeLigne(List.of(grp));

		Optional<GroupeLigne> resultBeforeDelete = groupeLigneDao.getFromID(10);
		assertTrue(resultBeforeDelete.isPresent());

		groupeLigneDao.delete(grp);

	}

}
