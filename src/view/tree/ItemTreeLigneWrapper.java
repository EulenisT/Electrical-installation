package view.tree;

import model.Ligne;

public class ItemTreeLigneWrapper extends ItemTreeWrapper {
	private Ligne ligne;

	/**
	 * @param ligne
	 */
	public ItemTreeLigneWrapper(Ligne ligne) {
		this.ligne = ligne;
	}

	@Override
	public String getNom() {
		
		return ligne.getCode()==null? ligne.getNum().toString():ligne.getCode();
	}

	@Override
	public Ligne getObject() {
		
		return ligne;
	}

}
