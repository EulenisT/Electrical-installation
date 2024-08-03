package view.tree;

import model.Bloc;

public class ItemTreeBlocWrapper extends ItemTreeWrapper {
	private Bloc bloc;

	/**
	 * @param bloc
	 */
	public ItemTreeBlocWrapper(Bloc bloc) {
		this.bloc = bloc;
	}

	@Override
	public String getNom() {
		return bloc.toString();
	}

	@Override
	public Bloc getObject() {

		return bloc;
	}

}
