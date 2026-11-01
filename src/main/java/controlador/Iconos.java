package controlador;

import java.awt.Component;
import java.awt.Image;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;

import vista.ventana;

// Carga iconos de botones y categorías, y configura el renderer del combo de categorías.
public class Iconos {
    static final String[] CLAVES_CATEGORIA = {"familia", "amigos", "trabajo", "universidad", "clientes", "emergencias", "otros"};

    private final ventana delegado;
    private final Map<String, Icon> iconosCategoria = new HashMap<>();

    public Iconos(ventana delegado) {
        this.delegado = delegado;
        for (String c : CLAVES_CATEGORIA) {
            iconosCategoria.put(c, cargar("/images/icons/categories/" + c + ".png"));
        }
    }

    private String t(String clave, String respaldo) {
        var bundle = delegado.getBundle();
        return bundle != null ? bundle.getString(clave) : respaldo;
    }

    public void configurarBotones() {
        Map<JButton, String> mapa = Map.of(
                delegado.btn_add, "agregar",
                delegado.btn_modificar, "modificar",
                delegado.btn_eliminar, "eliminar",
                delegado.btn_exportar, "exportar",
                delegado.btn_actualizar_estadisticas, "actualizar",
                delegado.btn_limpiar_estadisticas, "limpiar",
                delegado.btn_exportar_reporte, "exportar"
        );
        mapa.forEach((btn, nombre) -> {
            Icon icono = cargar("/images/icons/buttons/" + nombre + ".png");
            if (icono != null) btn.setIcon(icono);
        });
    }

    // Renderer del combo: muestra el icono según el texto traducido de cada categoría.
    public void configurarRendererCategorias() {
        delegado.cmb_categoria.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value != null) {
                    label.setIcon(iconoDeTexto(value.toString()));
                }
                return label;
            }
        });
    }

    private Icon iconoDeTexto(String texto) {
        for (String c : CLAVES_CATEGORIA) {
            if (texto.equals(t("cat." + c, c))) return iconosCategoria.get(c);
        }
        return null;
    }

    private Icon cargar(String ruta) {
        URL url = getClass().getResource(ruta);
        if (url == null) return null;
        Image escalada = new ImageIcon(url).getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH);
        return new ImageIcon(escalada);
    }
}
