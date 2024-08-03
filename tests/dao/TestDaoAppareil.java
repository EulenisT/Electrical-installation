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
import model.appareil.Appareil;

class TestDaoAppareil {
	private static DAOFactory factory;
	private static IAppareilDao appareilDao;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		ConnexionSingleton.setInfoConnexion(new ConnexionFromFile(
				"./ressources/connexion_InstallationsElectriquesTest.properties", Databases.FIREBIRD));

		factory = DAOFactory.getDAOFactory(TypePersistance.FIREBIRD, ConnexionSingleton.getConnexion());
		appareilDao = factory.getAppareilDAO();
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		ConnexionSingleton.liberationConnexion();
	}

	@Test
	void testGetFromID() {
		Appareil app = new Appareil("DG12", "Disjoncteur Diff 20A/300ma 2Phases", 2,
				"m0 0 0-1-1-2-.045.029.096.192.043-.019m-.094 1.798.153-.317.121.314-.274.003m1-2 0-1");
		Optional<Appareil> app1 = appareilDao.getFromID("DG12");
		assertTrue(app1.isPresent());
		assertEquals(app, app1.get());

	}

}
