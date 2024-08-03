package services;

import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.SubmissionPublisher;

public class PublisherEvent {
	// permet de publier des messages
	private SubmissionPublisher<ListMessages> publisher = new SubmissionPublisher<>();

	/**
	 * Permet de s'enregistrer comme abonné
	 * 
	 * @param obs
	 */
	public void addObserver(Subscriber<ListMessages> obs) {
		publisher.subscribe(obs);
	}

	/**
	 * Permet d'avertir les abonnés en envoyant une liste d'évènements produits
	 */
	public void submit(ListMessages liste) {
		publisher.submit(liste);
	}
}
