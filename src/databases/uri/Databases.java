package databases.uri;

/**
 * 
 * @author Didier
 *
 */
public enum Databases implements IDbURL {
	H2(new H2_URL()), FIREBIRD(new Firebird_URL());

	private IDbURL dbUrl;

	private Databases(IDbURL dbURL) {
		this.dbUrl = dbURL;
	}

	//Déviation vers dbUrl
	
	@Override
	public String getUrl() {
		return dbUrl.getUrl();
	}

	@Override
	public String buildMemURL(String file) {

		return dbUrl.buildMemURL(file);
	}

	@Override
	public String buildEmbeddedURL(String file) {
		return dbUrl.buildEmbeddedURL(file);
	}

	@Override
	public String buildServeurURL(String file, String ip) {

		return dbUrl.buildServeurURL(file, ip);
	}

	@Override
	public String buildServeurURL(String file, String ip, int port) {

		return dbUrl.buildServeurURL(file, ip, port);
	}

	@Override
	public int getDefaultPort() {
		
		return dbUrl.getDefaultPort();
	}

	@Override
	public boolean hasMemoryMode() {
		
		return dbUrl.hasMemoryMode();
	}

	@Override
	public boolean hasEmbeddedMode() {
		return dbUrl.hasEmbeddedMode();
	}

	@Override
	public boolean hasServeurMode() {
		return dbUrl.hasServeurMode();
	}
}
