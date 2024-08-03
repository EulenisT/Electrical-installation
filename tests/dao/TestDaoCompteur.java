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
import model.appareil.Compteur;

class TestDaoCompteur {
	private static DAOFactory factory;
	private static ICompteurDao compteurDao;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		ConnexionSingleton.setInfoConnexion(new ConnexionFromFile(
				"./ressources/connexion_InstallationsElectriquesTest.properties", Databases.FIREBIRD));

		factory = DAOFactory.getDAOFactory(TypePersistance.FIREBIRD, ConnexionSingleton.getConnexion());
		compteurDao = factory.getCompteurDAO();
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		ConnexionSingleton.liberationConnexion();
	}

	@Test
	void testGetFromID() {
		Compteur comp = new Compteur("CG2 ", "COMPTEUR COMPAGNIE 2Phases", 2, "m 0 0 v 2 h 3 v -2 h -3 v -1 h 3 v 1");
		Optional<Compteur> comp1 = compteurDao.getFromID("CG2");
		assertTrue(comp1.isPresent());
		assertEquals(comp, comp1.get());

	}

}
