package dao;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import dao.DAOFactory.TypePersistance;
import databases.connexion.ConnexionFromFile;
import databases.connexion.ConnexionSingleton;
import databases.uri.Databases;
import model.Bloc;
import model.appareil.Appareil;
import utils.DatabaseUtil;

class TestDaoBloc {

	private static DAOFactory factory;
	private static IBlocDao blocDao;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		ConnexionSingleton.setInfoConnexion(new ConnexionFromFile(
				"./ressources/connexion_InstallationsElectriquesTest.properties", Databases.FIREBIRD));

		DatabaseUtil.executeScriptSQL(ConnexionSingleton.getConnexion(),
				"./ressources/Script_Insert_Installation_1.sql");

		factory = DAOFactory.getDAOFactory(TypePersistance.FIREBIRD, ConnexionSingleton.getConnexion());
		blocDao = factory.getBlocDAO();
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {

		DatabaseUtil.executeScriptSQL(ConnexionSingleton.getConnexion(),
				"./ressources/Script_Insert_Installation_1.sql");

		ConnexionSingleton.liberationConnexion();
	}

	@Test
	void testGetBlocFromLigne() {

		List<Bloc> blocs = blocDao.getBlocFromLigne(1001);

		// Vérifier que la liste n'est pas vide
		assertFalse(blocs.isEmpty());

		// Vérifiez que le premier bloc porte l'identifiant 10010
		Bloc resBloc = new Bloc(10010);
		assertEquals(resBloc.getId(), blocs.get(0).getId());

	}

	@Test
	void testInsertUpdateBlocs() throws Exception {

		List<Bloc> blocs = new ArrayList<>();

		Bloc bloc1 = new Bloc(10000);
		Appareil app1 = new Appareil("PC11", "Prise Classique terre/Enf", 2,
				"m0 0 3 0c0 1 1 2 2 2m0-4c-1 0-2 1-2 2m0-2 0 4m2 1 0-1m0-4 0-1m-2 3");

		bloc1.getAppareils().add(app1);

		Bloc bloc2 = new Bloc(10001);
		Appareil app2 = new Appareil("PC11", "Prise Classique terre/Enf", 2,
				"m0 0 3 0c0 1 1 2 2 2m0-4c-1 0-2 1-2 2m0-2 0 4m2 1 0-1m0-4 0-1m-2 3");

		bloc2.getAppareils().add(app2);

		blocs.add(bloc1);
		blocs.add(bloc2);

		blocDao.insertUpdateBlocs(blocs);

	}

	@Test
	void testDeleteBloc() throws Exception {

		Bloc blocToDelete = new Bloc(10001);
		Appareil appToDelete = new Appareil("PC11", "Prise Classique terre/Enf", 2,
				"m0 0 3 0c0 1 1 2 2 2m0-4c-1 0-2 1-2 2m0-2 0 4m2 1 0-1m0-4 0-1m-2 3");
		blocToDelete.getAppareils().add(appToDelete);

		blocDao.insertUpdateBlocs(Collections.singletonList(blocToDelete));

		boolean deleted = blocDao.delete(blocToDelete);

		assertTrue(deleted);

	}

}
