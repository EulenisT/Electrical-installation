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
import model.appareil.Interrupteur;

class TestDaoInterrupteur {
	private static DAOFactory factory;
	private static IInterrupteurDao interrupteurDao;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		ConnexionSingleton.setInfoConnexion(new ConnexionFromFile(
				"./ressources/connexion_InstallationsElectriquesTest.properties", Databases.FIREBIRD));

		factory = DAOFactory.getDAOFactory(TypePersistance.FIREBIRD, ConnexionSingleton.getConnexion());
		interrupteurDao = factory.getInterrupteurDAO();
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		ConnexionSingleton.liberationConnexion();
	}

	@Test
	void testGetFromID() {
		Interrupteur intp = new Interrupteur("IC1 ", "Interrupteur Classique 1C", 1,
				"m0 0a1 1 0 001 1 1 1 0 001-1.006 1 1 0 00-1-.994 1 1 0 00-1 1m1.662-.74 2.338-2.26.482.519m-2.475 2.475",
				2, 1);
		Optional<Interrupteur> intp1 = interrupteurDao.getFromID("IC1 ");
		assertTrue(intp1.isPresent());
		assertEquals(intp, intp1.get());
	}

}
