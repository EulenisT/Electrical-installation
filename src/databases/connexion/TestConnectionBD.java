package databases.connexion;

import java.sql.Connection;

import dao.exception.PKException;
import dao.exception.ValidationException;
import databases.uri.Databases;

public class TestConnectionBD {

	public static void main(String[] args) {

		try {

			ConnexionSingleton.setInfoConnexion(new ConnexionFromFile(
					"./ressources/connexion_InstallationsElectriques.properties", Databases.FIREBIRD));

			Connection connection = ConnexionSingleton.getConnexion();

			// TODO
			System.out.println("Niveau Transaction: " + connection.getTransactionIsolation());
			ConnexionSingleton.liberationConnexion();
		} catch (PersistanceException e) {
			System.out.println("Problème");
		} catch (Exception e) {
			if (e instanceof ValidationException exc) {
				System.out.println(exc.getMessage() + " Champ: " + exc.getChamp());
			} else if (e instanceof PKException exc) {
				System.out.println(exc.getMessage() + " Champ: " + exc.getId());
			}
		}
	}
}
