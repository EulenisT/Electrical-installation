package view.tree;

import model.Installation;

public class ItemTreeInstallationWrapper extends ItemTreeWrapper {
	private Installation installation;

	/**
	 * @param installation
	 */
	public ItemTreeInstallationWrapper(Installation installation) {
		this.installation = installation;
	}

	@Override
	public String getNom() {
		return installation.getId() != null ? installation.getId().toString() : "---";
	}

	@Override
	public Installation getObject() {
		return installation;
	}

}
