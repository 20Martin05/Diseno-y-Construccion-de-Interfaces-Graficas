package vista;

import java.awt.EventQueue;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import controlador.LogicaVentana;
import vista.base.VentanaUI;

public class ventana extends VentanaUI {
	private static final Logger LOGGER = Logger.getLogger(ventana.class.getName());

	private ResourceBundle bundle;

	public static void main(String[] args) {
		Locale.setDefault(Locale.of("es", "ES"));
		EventQueue.invokeLater(() -> {
			try {
				new ventana().setVisible(true);
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, "No se pudo iniciar la aplicación", e);
			}
		});
	}

	public ventana() {
		actualizarIdioma(Locale.getDefault());

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setContentPane(contentPane);
		pack();
		setLocationRelativeTo(null);

		try {
			java.net.URL iconoURL = getClass().getResource("/images/icons/application/gestion.png");
			if (iconoURL != null) {
				setIconImage(new ImageIcon(iconoURL).getImage());
			}
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "No se pudo cargar el icono de la aplicación", e);
		}
		new LogicaVentana(this);
	}

	/**
	 * Carga el ResourceBundle del idioma indicado y actualiza los textos de los componentes Swing.
	 */
	public void actualizarIdioma(Locale locale) {
		try {
			bundle = ResourceBundle.getBundle("messages", locale);

			if (panelContactos.getBorder() instanceof TitledBorder tb) {
				tb.setTitle(bundle.getString("border.contactos"));
			}
			if (panelEstadisticas.getBorder() instanceof TitledBorder tb) {
				tb.setTitle(bundle.getString("border.estadisticas"));
			}

			btn_add.setText(bundle.getString("btn.add"));
			btn_modificar.setText(bundle.getString("btn.modificar"));
			btn_eliminar.setText(bundle.getString("btn.eliminar"));
			btn_exportar.setText(bundle.getString("btn.exportar"));
			btn_actualizar_estadisticas.setText(bundle.getString("btn.actualizar.estadisticas"));
			btn_limpiar_estadisticas.setText(bundle.getString("btn.limpiar.estadisticas"));
			btn_exportar_reporte.setText(bundle.getString("btn.exportar.reporte"));

			chb_favorito.setText(bundle.getString("chb.favorito"));
			txt_buscar.setToolTipText(bundle.getString("lbl.buscar"));

			lbl_nombres_id.setText(bundle.getString("lbl.form.nombres"));
			lbl_email_id.setText(bundle.getString("lbl.form.email"));
			lbl_telefono_id.setText(bundle.getString("lbl.form.telefono"));
			lbl_categoria_id.setText(bundle.getString("lbl.form.categoria"));
			lbl_buscar_id.setText(bundle.getString("lbl.form.buscar"));

			repaint();
			revalidate();
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "No se pudieron refrescar los textos para: " + locale, e);
		}
	}

	public ResourceBundle getBundle() {
		return bundle;
	}
}
