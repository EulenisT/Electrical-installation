package view.utils;

import java.util.ResourceBundle;

public class I18N {
	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("view/bundle/installation");

    public static String getString(String key) {
        return RESOURCE_BUNDLE.getString(key);
    }
}
