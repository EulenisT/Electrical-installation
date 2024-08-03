package dao;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import dao.DAOFactory.TypePersistance;
import databases.connexion.ConnexionFromFile;
import databases.connexion.ConnexionSingleton;
import databases.uri.Databases;
import model.appareil.Prise;

class TestDaoPrise {
	private static DAOFactory factory;
	private static IPriseDao priseDao;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		ConnexionSingleton.setInfoConnexion(new ConnexionFromFile(
				"./ressources/connexion_InstallationsElectriquesTest.properties", Databases.FIREBIRD));

		factory = DAOFactory.getDAOFactory(TypePersistance.FIREBIRD, ConnexionSingleton.getConnexion());
		priseDao = factory.getPriseDAO();
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		ConnexionSingleton.liberationConnexion();
	}

	@Test
	void testGetFromID() {
		Prise pri = new Prise("PC00", "Prise Classique sans terre/enf", 2, "m0 5 3 0c0 1 1 2 2 2m0-4c-1 0-2 1-2 2m0 0",
				false, false, false);
		Optional<Prise> pri1 = priseDao.getFromID("PC00");
		assertTrue(pri1.isPresent());
		assertEquals(pri, pri1.get());
	}

}
