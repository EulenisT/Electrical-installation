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
import model.appareil.Lampe;

class TestDaoLampe {
	private static DAOFactory factory;
	private static ILampeDao lampeDao;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		ConnexionSingleton.setInfoConnexion(new ConnexionFromFile(
				"./ressources/connexion_InstallationsElectriquesTest.properties", Databases.FIREBIRD));

		factory = DAOFactory.getDAOFactory(TypePersistance.FIREBIRD, ConnexionSingleton.getConnexion());
		lampeDao = factory.getLampeDAO();
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		ConnexionSingleton.liberationConnexion();
	}

	@Test
	void test() {
		Lampe lam = new Lampe("L1  ", "Lampe classique 220V", 2,
				"m0 0a.05.05 90 005 0 .05.05 90 00-5 0l.05 0a.05.05 90 014.9 0 .05.05 90 01-4.9 0m2.5-.05 1.65-1.7.05.05-1.65 1.7 1.7 1.65-.05.05-1.7-1.65-1.75 1.7-.05-.05 1.75-1.7-1.75-1.7.05-.05 1.75 1.7m2.45.05",
				220);
		Optional<Lampe> lam1 = lampeDao.getFromID("L1  ");
		assertTrue(lam1.isPresent());
		assertEquals(lam, lam1.get());
	}

}
