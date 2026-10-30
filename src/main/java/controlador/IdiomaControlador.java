package controlador;

import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import vista.ventana;

// Gestiona el combo de idiomas y notifica el cambio de Locale.
public class IdiomaControlador {
    private static final List<String> IDIOMAS = List.of("Español", "English", "Português");

    private final ventana delegado;
    private final Consumer<Locale> alCambiar;

    // alCambiar se ejecuta con el nuevo Locale cada vez que el usuario elige un idioma.
    public IdiomaControlador(ventana delegado, Consumer<Locale> alCambiar) {
        this.delegado = delegado;
        this.alCambiar = alCambiar;
    }

    public void configurar() {
        delegado.cmb_idiomas.removeAllItems();
        for (String idioma : IDIOMAS) {
            delegado.cmb_idiomas.addItem(idioma);
        }
        delegado.cmb_idiomas.addActionListener(e -> {
            String sel = (String) delegado.cmb_idiomas.getSelectedItem();
            if (sel != null) alCambiar.accept(localeDe(sel));
        });
    }

    public static Locale localeDe(String idioma) {
        return switch (idioma) {
            case "English" -> Locale.of("en");
            case "Português" -> Locale.of("pt");
            default -> Locale.of("es");
        };
    }
}