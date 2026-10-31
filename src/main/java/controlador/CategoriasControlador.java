package controlador;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

// Convierte la categoría guardada (en cualquier idioma) a su clave interna y a su texto traducido.
public final class CategoriasControlador {
    private static final List<ResourceBundle> BUNDLES = List.of(
            ResourceBundle.getBundle("messages", Locale.of("es")),
            ResourceBundle.getBundle("messages", Locale.of("en")),
            ResourceBundle.getBundle("messages", Locale.of("pt")));

    private CategoriasControlador() {}

    // "Família", "Familia" o "familia" -> "familia". Si no coincide con nada, devuelve el valor original.
    public static String clave(String valor) {
        if (valor == null || valor.isBlank()) return "";
        for (ResourceBundle b : BUNDLES) {
            for (String c : Iconos.CLAVES_CATEGORIA) {
                if (valor.equals(b.getString("cat." + c))) return c;
            }
        }
        return valor;
    }

    // Texto a mostrar en el idioma del bundle activo.
    public static String etiqueta(ResourceBundle activo, String valor) {
        String clave = clave(valor);
        String k = "cat." + clave;
        if (activo != null && activo.containsKey(k)) return activo.getString(k);
        return valor == null ? "" : valor;
    }
}
