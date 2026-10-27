package vista;

import java.awt.EventQueue;
import javax.swing.*;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import controlador.LogicaVentana;
import vista.base.VentanaUI;

public class ventana extends VentanaUI {
	private static final Logger LOGGER = Logger.getLogger(ventana.class.getName());

	// Variable global para gestionar el idioma en toda la ventana
	private ResourceBundle bundle;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		// Define el idioma por defecto al arrancar la aplicación
		Locale.setDefault(new Locale("es", "ES"));
		EventQueue.invokeLater(() -> {
			try {
				ventana frame = new ventana();
				frame.setVisible(true);
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, "No se pudo iniciar la aplicación", e);
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ventana() {
		// 1. Cargar el idioma actual al inicializar la ventana
		actualizarIdioma(Locale.getDefault());

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setContentPane(contentPane);
		pack();
		setLocationRelativeTo(null);

		try {
			// Carga la imagen desde la ruta de recursos del proyecto
			java.net.URL iconoURL = getClass().getResource("/images/icons/application/gestion.png");
			if (iconoURL != null) {
				ImageIcon iconoOriginal = new ImageIcon(iconoURL);
				this.setIconImage(iconoOriginal.getImage());
			}
		} catch (Exception e) {
			// Manejo silencioso o registro en caso de que la ruta falle
			e.printStackTrace();
		}
		// Instancia el controlador para inicializar lógica y componentes
		new LogicaVentana(this);
	}

	/**
	 * Carga el ResourceBundle correspondiente y actualiza los textos de los componentes Swing
	 */
	public void actualizarIdioma(Locale locale) {
		try {
			// 1. Cargar el bundle correcto según el idioma seleccionado
			this.bundle = ResourceBundle.getBundle("messages", locale);

			// 2. TÍTULOS Y BORDES PRINCIPALES
			if (panelContactos != null && panelContactos.getBorder() instanceof javax.swing.border.TitledBorder) {
				((javax.swing.border.TitledBorder) panelContactos.getBorder()).setTitle(bundle.getString("border.contactos"));
			}
			if (panelEstadisticas != null && panelEstadisticas.getBorder() instanceof javax.swing.border.TitledBorder) {
				((javax.swing.border.TitledBorder) panelEstadisticas.getBorder()).setTitle(bundle.getString("border.estadisticas"));
			}

			// 3. BOTONES DEL FORMULARIO DE CONTACTOS
			if (btn_add != null) btn_add.setText(bundle.getString("btn.add"));
			if (btn_modificar != null) btn_modificar.setText(bundle.getString("btn.modificar"));
			if (btn_eliminar != null) btn_eliminar.setText(bundle.getString("btn.eliminar"));
			if (btn_exportar != null) btn_exportar.setText(bundle.getString("btn.exportar"));

			// 4. CHECKBOX Y BUSCADOR
			if (chb_favorito != null) chb_favorito.setText(bundle.getString("chb.favorito"));
			if (txt_buscar != null) {
				txt_buscar.setToolTipText(bundle.getString("lbl.buscar"));
			}

			// 5. BOTONES DE LA SECCIÓN DE ESTADÍSTICAS
			if (btn_actualizar_estadisticas != null) btn_actualizar_estadisticas.setText(bundle.getString("btn.actualizar.estadisticas"));
			if (btn_limpiar_estadisticas != null) btn_limpiar_estadisticas.setText(bundle.getString("btn.limpiar.estadisticas"));
			if (btn_exportar_reporte != null) btn_exportar_reporte.setText(bundle.getString("btn.exportar.reporte"));

			// Traducción de las etiquetas fijas del formulario de contactos
			if (lbl_nombres_id != null) lbl_nombres_id.setText(bundle.getString("lbl.form.nombres"));
			if (lbl_email_id != null) lbl_email_id.setText(bundle.getString("lbl.form.email"));
			if (lbl_telefono_id != null) lbl_telefono_id.setText(bundle.getString("lbl.form.telefono"));
			if (lbl_categoria_id != null) lbl_categoria_id.setText(bundle.getString("lbl.form.categoria"));
			if (lbl_buscar_id != null) lbl_buscar_id.setText(bundle.getString("lbl.form.buscar"));
			// 6. REFRESCAR CONTENEDOR GRÁFICO
			this.repaint();
			this.revalidate();

		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "No se pudieron refrescar los textos en tiempo de ejecución para: " + locale, e);
		}
	}

	public void actualizarTextosVentana(ResourceBundle bundle) {
		if (bundle == null) return;

		// Etiquetas de los campos del formulario con los nombres correctos
		lbl_nombres_id.setText(bundle.getString("lbl.nombres"));
		lbl_telefono_id.setText(bundle.getString("lbl.telefono"));
		lbl_email_id.setText(bundle.getString("lbl.email"));
		lbl_buscar_id.setText(bundle.getString("lbl.buscar"));
		lbl_categoria_id.setText(bundle.getString("lbl.categoria"));
		chb_favorito.setText(bundle.getString("chb.favorito"));

		// Botones del sistema
		btn_add.setText(bundle.getString("btn.add"));
		btn_modificar.setText(bundle.getString("btn.modificar"));
		btn_eliminar.setText(bundle.getString("btn.eliminar"));
		btn_exportar.setText(bundle.getString("btn.exportar"));
		btn_actualizar_estadisticas.setText(bundle.getString("btn.actualizar.estadisticas"));
		btn_limpiar_estadisticas.setText(bundle.getString("btn.limpiar.estadisticas"));
		btn_exportar_reporte.setText(bundle.getString("btn.exportar.reporte"));
	}


	/**
	 * Permite a otros componentes o controladores acceder al bundle actual
	 */
	public ResourceBundle getBundle() {
		return this.bundle;
	}
}
