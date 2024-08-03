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
import model.appareil.Disjoncteur;

class TestDaoDisjoncteur {
	private static DAOFactory factory;
	private static IDisjoncteurDao disjoncteurDao;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		ConnexionSingleton.setInfoConnexion(new ConnexionFromFile(
				"./ressources/connexion_InstallationsElectriquesTest.properties", Databases.FIREBIRD));

		factory = DAOFactory.getDAOFactory(TypePersistance.FIREBIRD, ConnexionSingleton.getConnexion());
		disjoncteurDao = factory.getDisjoncteurDAO();
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		ConnexionSingleton.liberationConnexion();
	}

	@Test
	void testGetFromID() {
		Disjoncteur dis = new Disjoncteur("FU02", "Fusible automatique 6A/2Phases", 2,
				"m 0 0 l 0 -1 l -1 -2 l -0.045 0.029 l 0.096 0.192 l 0.043 -0.019 m 0.907 -0.202 l 0 -1", 6);
		Optional<Disjoncteur> dis1 = disjoncteurDao.getFromID("FU02");
		assertTrue(dis1.isPresent());
		assertEquals(dis, dis1.get());

	}

}
