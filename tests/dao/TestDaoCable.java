package dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import dao.DAOFactory.TypePersistance;
import dao.exception.PKException;
import databases.connexion.ConnexionFromFile;
import databases.connexion.ConnexionSingleton;
import databases.uri.Databases;
import model.appareil.Cable;
import utils.DatabaseUtil;

class TestDaoCable {
	private static DAOFactory factory;
	private static ICableDao cableDao;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		ConnexionSingleton.setInfoConnexion(new ConnexionFromFile(
				"./ressources/connexion_InstallationsElectriquesTest.properties", Databases.FIREBIRD));
		// Réinitialise la base de données dans son état initial
		DatabaseUtil.executeScriptSQL(ConnexionSingleton.getConnexion(), "./ressources/scriptInitDBTest.sql");

		factory = DAOFactory.getDAOFactory(TypePersistance.FIREBIRD, ConnexionSingleton.getConnexion());
		cableDao = factory.getCableDAO();
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		// Réinitialise la base de données dans son état initial
		DatabaseUtil.executeScriptSQL(ConnexionSingleton.getConnexion(), "./ressources/scriptInitDBTest.sql");
		ConnexionSingleton.liberationConnexion();
	}

	@Test
	void testGetFromID() {
		Cable xvb = new Cable("XVB3G2", (short) 3, true, 2.5f, "XVB");
		Optional<Cable> oc1 = cableDao.getFromID("XVB3G2");
		assertTrue(oc1.isPresent());
		assertEquals(xvb, oc1.get());
	}

	@Test
	void testInsert() throws Exception {
		Cable xvb = new Cable("XVB10G2", (short) 10, true, 2.5f, "XVB"); // Parce qu'il existe déjà
		Cable c1 = cableDao.insert(xvb);
		assertEquals(xvb, c1);
		Optional<Cable> oc = cableDao.getFromID("XVB10G2");
		assertTrue(oc.isPresent());
	}

	@Test
	void testInsertErreur() {
		Cable xvb = new Cable("XVB3G2", (short) 3, true, 2.5f, "XVB");
		assertThrows(PKException.class, () -> cableDao.insert(xvb));
	}

	@Test
	@Disabled
	void testCount() {

	}

}