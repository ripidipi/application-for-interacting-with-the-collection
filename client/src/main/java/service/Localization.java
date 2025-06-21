package service;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class Localization {
    private static final ObjectProperty<Locale> locale =
            new SimpleObjectProperty<>(new Locale("en"));

    public static ObjectProperty<Locale> localeProperty() {
        return locale;
    }

    public static Locale getLocale() {
        return locale.get();
    }

    public static void setLocale(Locale l) {
        locale.set(l);
    }

    public static ResourceBundle bundle() {
        return ResourceBundle.getBundle("messages", getLocale(),
                new UTF8Control());
    }

    public static class UTF8Control extends ResourceBundle.Control {
        @Override
        public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload)
                throws IllegalAccessException, InstantiationException, IOException {
            ResourceBundle bundle = super.newBundle(baseName, locale, format, loader, reload);

            if (bundle != null) {
                return bundle;
            }
            String resourceName = toResourceName(baseName, "properties");
            try (InputStream stream = loader.getResourceAsStream(resourceName)) {
                if (stream != null) {
                    return new PropertyResourceBundle(new InputStreamReader(stream, StandardCharsets.UTF_8));
                }
            }
            return null;
        }
    }
}
