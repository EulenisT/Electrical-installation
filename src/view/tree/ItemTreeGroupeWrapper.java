package view.tree;

import model.GroupeLigne;

public class ItemTreeGroupeWrapper extends ItemTreeWrapper {
	private GroupeLigne groupe;

	/**
	 * @param groupe
	 */
	public ItemTreeGroupeWrapper(GroupeLigne groupe) {
		this.groupe = groupe;
	}

	@Override
	public String getNom() {
		return groupe.getNum().toString();
	}

	@Override
	public GroupeLigne getObject() {
		return groupe;
	}
}
