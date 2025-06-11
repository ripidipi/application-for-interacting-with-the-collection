package service;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import java.util.Locale;
import java.util.ResourceBundle;

public class Localization {
    private static final ObjectProperty<Locale> locale =
            new SimpleObjectProperty<>(Locale.getDefault());

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
        return ResourceBundle.getBundle("messages", getLocale());
    }
}
