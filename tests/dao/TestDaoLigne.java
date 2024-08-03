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
import model.Ligne;
import model.appareil.Cable;
import model.appareil.Disjoncteur;
import utils.DatabaseUtil;

class TestDaoLigne {

	private static DAOFactory factory;
	private static ILigneDao ligneDao;
	Cable cable = new Cable("XVB3G2", (short) 0, false, 0.0f, null);
	Disjoncteur fusible = new Disjoncteur("FU22", null, 0, null, 0);

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		ConnexionSingleton.setInfoConnexion(new ConnexionFromFile(
				"./ressources/connexion_InstallationsElectriquesTest.properties", Databases.FIREBIRD));

		// Réinitialise la base de données dans son état initial
		DatabaseUtil.executeScriptSQL(ConnexionSingleton.getConnexion(),
				"./ressources/Script_Insert_Installation_1.sql");

		factory = DAOFactory.getDAOFactory(TypePersistance.FIREBIRD, ConnexionSingleton.getConnexion());
		ligneDao = factory.getLigneDAO();
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

		Ligne lig = new Ligne(1000, null, true, cable, fusible);
		Optional<Ligne> lig1 = ligneDao.getFromID(1000);
		assertTrue(lig1.isPresent());
		assertEquals(lig, lig1.get());

	}

	@Test
	void testGetListeFromGroupe() {

		List<Ligne> lignes = ligneDao.getListeFromGroupe(10);

		assertFalse(lignes.isEmpty());

		Ligne resLig = new Ligne(1000, null, true, cable, fusible);
		assertEquals(resLig.getNum(), lignes.get(0).getNum());

	}

	@Test
	void testInsertUpdateLigne() throws Exception {
		List<Ligne> lignes = new ArrayList<>();
		Cable cable = new Cable("XVB3G2", (short) 0, false, 0.0f, null);
		Disjoncteur fusible = new Disjoncteur("FU22", null, 0, null, 0);

		Ligne lig1 = new Ligne(2000, null, true, cable, fusible);
		Ligne lig2 = new Ligne(2001, null, true, cable, fusible);

		lignes.add(lig1);
		lignes.add(lig2);

		ligneDao.insertUpdateLigne(lignes);

		// Vérifier que les lignes ont été insérées/mises à jour correctement
		Optional<Ligne> result1 = ligneDao.getFromID(2000);
		Optional<Ligne> result2 = ligneDao.getFromID(2001);

		assertTrue(result1.isPresent());
		assertTrue(result2.isPresent());

		assertEquals(lig1.getNum(), result1.get().getNum());
		assertEquals(lig1.getCode(), result1.get().getCode());
		assertEquals(lig1.isInterne(), result1.get().isInterne());
		assertEquals(lig1.getCable().code(), result1.get().getCable().code());
		assertEquals(lig1.getFusible().getCode(), result1.get().getFusible().getCode());

		assertEquals(lig2.getNum(), result2.get().getNum());
		assertEquals(lig2.getCode(), result2.get().getCode());
		assertEquals(lig2.isInterne(), result2.get().isInterne());
		assertEquals(lig2.getCable().code(), result2.get().getCable().code());
		assertEquals(lig2.getFusible().getCode(), result2.get().getFusible().getCode());
	}

	@Test
	void testDeleteLigne() throws Exception {

		Ligne ligne = new Ligne(2000, null, true, cable, fusible);

		ligneDao.insertUpdateLigne(List.of(ligne));

		Optional<Ligne> resultBeforeDelete = ligneDao.getFromID(2000);
		assertTrue(resultBeforeDelete.isPresent());

		ligneDao.delete(ligne);

	}

}
