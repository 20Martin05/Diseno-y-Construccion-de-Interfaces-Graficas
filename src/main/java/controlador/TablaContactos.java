package controlador;

import java.util.List;

import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import modelo.Persona;
import vista.ventana;

// Gestiona el modelo, las columnas, los anchos, la ordenación y el filtro de la tabla de contactos.
public class TablaContactos {
    private static final int[] ANCHOS = {50, 160, 100, 180, 110, 80};
    private static final int[] MAXIMOS = {70, -1, -1, -1, -1, 90};

    private final ventana delegado;
    private final JTable tabla;
    private TableRowSorter<DefaultTableModel> sorter;

    public TablaContactos(ventana delegado) {
        this.delegado = delegado;
        this.tabla = delegado.lst_contactos;
    }

    private String t(String clave, String respaldo) {
        var bundle = delegado.getBundle();
        return bundle != null ? bundle.getString(clave) : respaldo;
    }

    // Reconstruye el modelo con la lista de contactos recibida.
    public void cargar(List<Persona> personas) {
        DefaultTableModel modelo = crearModelo();
        String si = t("favorito.si", "Sí");
        String no = t("favorito.no", "No");
        for (Persona c : personas) {
            modelo.addRow(new Object[]{
                    c.getId(), c.getNombre(), c.getTelefono(), c.getEmail(), c.getCategoria(),
                    c.isFavorito() ? si : no
            });
        }
        tabla.setModel(modelo);
        sorter = new TableRowSorter<>(modelo);
        tabla.setRowSorter(sorter);
        aplicarAnchos();
    }

    private DefaultTableModel crearModelo() {
        String[] columnas = {
                t("table.col.id", "ID"),
                t("table.col.nombres", "NOMBRES"),
                t("table.col.telefono", "TELÉFONO"),
                t("table.col.email", "EMAIL"),
                t("table.col.categoria", "CATEGORÍA"),
                t("table.col.favorito", "FAVORITO")
        };
        return new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Integer.class : String.class;
            }
        };
    }

    private void aplicarAnchos() {
        var columnas = tabla.getColumnModel();
        if (columnas.getColumnCount() < ANCHOS.length) return;
        for (int i = 0; i < ANCHOS.length; i++) {
            columnas.getColumn(i).setPreferredWidth(ANCHOS[i]);
            if (MAXIMOS[i] > 0) columnas.getColumn(i).setMaxWidth(MAXIMOS[i]);
        }
    }

    // Filtra por nombre sin distinguir mayúsculas; texto vacío muestra todos.
    public void filtrar(String texto) {
        if (sorter == null) return;
        String limpio = texto.trim();
        sorter.setRowFilter(limpio.isEmpty() ? null
                : RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(limpio)));
    }
}
