package dao;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import dao.DAOFactory.TypePersistance;
import dao.exception.PKException;
import databases.connexion.ConnexionFromFile;
import databases.connexion.ConnexionSingleton;
import databases.uri.Databases;
import model.Adresse;
import model.Installation;
import model.Installation.TypeLogement;
import utils.DatabaseUtil;

class TestDaoInstallation {
	private static DAOFactory factory;
	private static IInstallationDao installationDao;

	LocalDate date = LocalDate.of(2024, 11, 22);
	Adresse adresse = new Adresse("21 Av. de la gare", 1457, "Walhain");
	TypeLogement logement = TypeLogement.MAISON;

	LocalDate date2 = LocalDate.of(2024, 12, 22);
	Adresse adresse2 = new Adresse("21 Av. de la gare", 1457, "Walhain");
	TypeLogement logement2 = TypeLogement.APPARTEMENT;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		ConnexionSingleton.setInfoConnexion(new ConnexionFromFile(
				"./ressources/connexion_InstallationsElectriquesTest.properties", Databases.FIREBIRD));

		// Réinitialise la base de données dans son état initial
		DatabaseUtil.executeScriptSQL(ConnexionSingleton.getConnexion(),
				"./ressources/Script_Insert_Installation_1.sql");

		factory = DAOFactory.getDAOFactory(TypePersistance.FIREBIRD, ConnexionSingleton.getConnexion());
		installationDao = factory.getInstallationDAO();
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

		Installation ins = new Installation(date, "SupElec", adresse, logement);
		ins.setId(1);

		Optional<Installation> ins1 = installationDao.getFromID(1);
		assertTrue(ins1.isPresent());
		assertEquals(ins, ins1.get());

	}

	@Test
	void testInsert() throws Exception {
		Installation inst = new Installation(date2, "SupElect", adresse2, logement2);
		Installation inst1 = installationDao.insert(inst);
		assertEquals(inst, inst1);
		Optional<Installation> oc = installationDao.getFromID(2);
		assertTrue(oc.isPresent());
	}

	@Test
	void testUpdate() throws Exception {

		// Récupération d'une installation existante
		Optional<Installation> optionalInstallation = installationDao.getFromID(2);
		assertTrue(optionalInstallation.isPresent());

		Installation installationUpdate = optionalInstallation.get();

		// Modifier
		installationUpdate.setDate(LocalDate.of(2024, 11, 22));
		installationUpdate.setInstallateur("SupElec");

		// Mise à jour
		boolean updateResult = installationDao.update(installationUpdate);
		assertTrue(updateResult);

		// vérifier
		Optional<Installation> updatedInstallationOptional = installationDao.getFromID(2);
		assertTrue(updatedInstallationOptional.isPresent());

		Installation updatedInstallation = updatedInstallationOptional.get();
		assertEquals(LocalDate.of(2024, 11, 22), updatedInstallation.getDate());
		assertEquals("SupElec", updatedInstallation.getInstallateur());
	}

}
