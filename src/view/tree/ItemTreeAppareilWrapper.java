package view.tree;

import model.appareil.Appareil;

public class ItemTreeAppareilWrapper extends ItemTreeWrapper {
	private Appareil appareil;

	/**
	 * @param app
	 */
	public ItemTreeAppareilWrapper(Appareil app) {
		this.appareil = app;
	}

	@Override
	public String getNom() {
		return appareil.getCode();
	}

	@Override
	public Appareil getObject() {
		return appareil;
	}

}
