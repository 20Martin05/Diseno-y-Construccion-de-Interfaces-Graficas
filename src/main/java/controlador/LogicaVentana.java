package controlador;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import modelo.Persona;
import modelo.PersonaDAO;
import vista.ventana;

// Coordinador de la ventana: conecta la vista con los controladores especializados.
public class LogicaVentana implements ActionListener, ListSelectionListener, ItemListener {
	private static final Logger LOGGER = Logger.getLogger(LogicaVentana.class.getName());

	private final ventana delegado;
	private final TablaContactos tabla;
	private final Iconos iconos;
	private final ContactosControlador contactos;
	private final IdiomaControlador idiomas;
	private ReporteEstadisticas reporte;
	private String categoria = "";
	private boolean favorito = false;

	public LogicaVentana(ventana delegado) {
		this.delegado = delegado;
		this.delegado.setExtendedState(JFrame.MAXIMIZED_BOTH);

		List<Persona> lista = cargarLista();
		this.tabla = new TablaContactos(delegado);
		this.iconos = new Iconos(delegado);
		this.contactos = new ContactosControlador(delegado, lista);
		this.idiomas = new IdiomaControlador(delegado, this::cambiarIdioma);

		configurarTabla();
		configurarMenuContextual();
		configurarAtajosTeclado();
		iconos.configurarBotones();
		iconos.configurarRendererCategorias();
		inicializarCategorias();
		idiomas.configurar();
		reporte = new ReporteEstadisticas(delegado.getBundle());

		tabla.cargar(lista);
		barraProgreso();
		registrarEventos();
		actualizarEstadoAcciones();
		mostrarReporte();
	}

	private List<Persona> cargarLista() {
		try {
			return new PersonaDAO(new Persona()).leerArchivo();
		} catch (IOException e) {
			LOGGER.log(Level.WARNING, "No se pudieron cargar los contactos", e);
			JOptionPane.showMessageDialog(delegado,
					t("msg.cargar.error", "Existen problemas al cargar todos los contactos"),
					t("dialog.error.titulo", "Error"), JOptionPane.ERROR_MESSAGE);
			return new java.util.ArrayList<>();
		}
	}

	private void registrarEventos() {
		for (JButton b : List.of(delegado.btn_add, delegado.btn_eliminar, delegado.btn_modificar,
				delegado.btn_exportar, delegado.btn_actualizar_estadisticas,
				delegado.btn_limpiar_estadisticas, delegado.btn_exportar_reporte)) {
			b.addActionListener(this);
		}
		delegado.cmb_categoria.addItemListener(this);
		delegado.chb_favorito.addItemListener(this);
		delegado.lst_contactos.getSelectionModel().addListSelectionListener(this);
		DocumentListener filtro = new DocumentListener() {
			public void insertUpdate(DocumentEvent e) { filtrar(); }
			public void removeUpdate(DocumentEvent e) { filtrar(); }
			public void changedUpdate(DocumentEvent e) { filtrar(); }
			private void filtrar() { tabla.filtrar(delegado.txt_buscar.getText()); }
		};
		delegado.txt_buscar.getDocument().addDocumentListener(filtro);
	}

	private String t(String clave, String respaldo) {
		var bundle = delegado.getBundle();
		return bundle != null ? bundle.getString(clave) : respaldo;
	}

	private void configurarTabla() {
		JTable t = delegado.lst_contactos;
		t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		t.setRowHeight(24);
		t.setFont(new Font("Tahoma", Font.PLAIN, 14));
		if (t.getTableHeader() != null) {
			t.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 14));
		}
	}

	private void configurarMenuContextual() {
		if (delegado.menuContactos == null) {
			delegado.menuContactos = new JPopupMenu();
			delegado.modificar = new JMenuItem();
			delegado.eliminar = new JMenuItem();
			delegado.menuContactos.add(delegado.modificar);
			delegado.menuContactos.add(delegado.eliminar);
			delegado.modificar.addActionListener(e -> delegado.btn_modificar.doClick());
			delegado.eliminar.addActionListener(e -> delegado.btn_eliminar.doClick());
			MouseAdapter raton = new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) { mostrarMenuContextual(e); }
				@Override
				public void mouseReleased(MouseEvent e) { mostrarMenuContextual(e); }
			};
			delegado.lst_contactos.addMouseListener(raton);
		}
		delegado.modificar.setText(t("menu.modificar", "Modificar"));
		delegado.eliminar.setText(t("menu.eliminar", "Eliminar"));
	}

	private void mostrarMenuContextual(MouseEvent e) {
		if (!e.isPopupTrigger()) return;
		int fila = delegado.lst_contactos.rowAtPoint(e.getPoint());
		if (fila >= 0) {
			delegado.lst_contactos.setRowSelectionInterval(fila, fila);
			actualizarEstadoAcciones();
			delegado.menuContactos.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	private void configurarAtajosTeclado() {
		JComponent root = delegado.getRootPane();
		InputMap inputMap = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		ActionMap actionMap = root.getActionMap();

		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "eliminarContacto");
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK), "guardarContacto");
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_DOWN_MASK), "agregarContacto");
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK), "exportarContacto");
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK), "buscarContacto");
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "clearSelectionAction");

		actionMap.put("eliminarContacto", accion(() -> delegado.btn_eliminar.doClick()));
		actionMap.put("guardarContacto", accion(() -> delegado.btn_modificar.doClick()));
		actionMap.put("agregarContacto", accion(() -> delegado.btn_add.doClick()));
		actionMap.put("exportarContacto", accion(() -> delegado.btn_exportar.doClick()));
		actionMap.put("buscarContacto", accion(() -> {
			delegado.txt_buscar.requestFocusInWindow();
			delegado.txt_buscar.selectAll();
		}));
		actionMap.put("clearSelectionAction", accion(() -> delegado.lst_contactos.clearSelection()));
	}

	private AbstractAction accion(Runnable r) {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) { r.run(); }
		};
	}

	private void inicializarCategorias() {
		int indice = Math.max(delegado.cmb_categoria.getSelectedIndex(), 0);
		delegado.cmb_categoria.removeAllItems();
		delegado.cmb_categoria.addItem(t("cat.default", "Elija una Categoria"));
		for (String c : Iconos.CLAVES_CATEGORIA) {
			delegado.cmb_categoria.addItem(t("cat." + c, c));
		}
		delegado.cmb_categoria.setSelectedIndex(Math.min(indice, delegado.cmb_categoria.getItemCount() - 1));
	}

	private void actualizarEstadoAcciones() {
		boolean hay = delegado.lst_contactos.getSelectedRow() >= 0;
		delegado.btn_modificar.setEnabled(hay);
		delegado.btn_eliminar.setEnabled(hay);
		if (delegado.modificar != null) delegado.modificar.setEnabled(hay);
		if (delegado.eliminar != null) delegado.eliminar.setEnabled(hay);
	}

	private void refrescar() {
		tabla.cargar(contactos.getContactos());
		actualizarEstadoAcciones();
		mostrarReporte();
		guardarReporte();
	}

	private void limpiarCampos() {
		delegado.txt_nombres.setText("");
		delegado.txt_telefono.setText("");
		delegado.txt_email.setText("");
		categoria = "";
		favorito = false;
		delegado.chb_favorito.setSelected(false);
		delegado.cmb_categoria.setSelectedIndex(0);
		delegado.lst_contactos.clearSelection();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object origen = e.getSource();
		String nombres = delegado.txt_nombres.getText();
		String telefono = delegado.txt_telefono.getText();
		String email = delegado.txt_email.getText();

		if (origen == delegado.btn_add) {
			if (contactos.agregar(nombres, telefono, email, categoria, favorito)) {
				refrescar();
				limpiarCampos();
			}
		} else if (origen == delegado.btn_modificar || origen == delegado.btn_eliminar) {
			int fila = delegado.lst_contactos.getSelectedRow();
			if (fila == -1) {
				String claveMsg = origen == delegado.btn_modificar ? "msg.seleccione.modificar" : "msg.seleccione.eliminar";
				String defMsg = origen == delegado.btn_modificar ? "Por favor seleccione un contacto de la tabla para modificar." : "Por favor seleccione un contacto de la tabla para eliminar.";
				JOptionPane.showMessageDialog(delegado, t(claveMsg, defMsg));
				return;
			}
			int indice = delegado.lst_contactos.convertRowIndexToModel(fila);
			boolean exito = origen == delegado.btn_modificar
					? contactos.modificar(indice, nombres, telefono, email, categoria, favorito)
					: contactos.eliminar(indice);
			if (exito) {
				refrescar();
				limpiarCampos();
			}
		} else if (origen == delegado.btn_exportar) {
			exportarLista();
		} else if (origen == delegado.btn_actualizar_estadisticas) {
			mostrarReporte();
		} else if (origen == delegado.btn_limpiar_estadisticas) {
			delegado.tbl_estadisticas.setText("");
		} else if (origen == delegado.btn_exportar_reporte) {
			exportarReporte();
		}
	}

	private void exportarLista() {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle(t("dialog.exportar.titulo", "Exportar Contactos a CSV"));
		chooser.setSelectedFile(new java.io.File("tabla_contactos.csv"));
		if (chooser.showSaveDialog(delegado) != JFileChooser.APPROVE_OPTION) return;

		java.io.File destino = chooser.getSelectedFile();
		if (!destino.getName().toLowerCase().endsWith(".csv")) {
			destino = new java.io.File(destino.getAbsolutePath() + ".csv");
		}
		try {
			new PersonaDAO(new Persona()).exportarCSV(destino, contactos.getContactos());
			JOptionPane.showMessageDialog(delegado,
					t("msg.exportado.exito", "Exportado exitosamente en:\n") + destino.getAbsolutePath());
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(delegado,
					t("msg.error.exportar", "Error al exportar archivo: ") + ex.getMessage(),
					t("dialog.error.titulo", "Error"), JOptionPane.ERROR_MESSAGE);
		}
	}

	private void exportarReporte() {
		String contenido = reporte.generar(contactos.getContactos());
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle(t("msg.reporte.exportar.titulo", "Exportar Reporte"));
		chooser.setSelectedFile(new java.io.File("reporte_estadisticas.txt"));
		if (chooser.showSaveDialog(delegado) != JFileChooser.APPROVE_OPTION) return;

		try {
			java.nio.file.Files.writeString(chooser.getSelectedFile().toPath(), contenido,
					java.nio.charset.StandardCharsets.UTF_8);
			JOptionPane.showMessageDialog(delegado, t("msg.reporte.exportado", "Reporte exportado con éxito."),
					t("dialog.exito.titulo", "Éxito"), JOptionPane.INFORMATION_MESSAGE);
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(delegado,
					t("msg.reporte.error.escribir", "Error al escribir el archivo:") + " " + ex.getMessage(),
					t("dialog.error.titulo", "Error"), JOptionPane.ERROR_MESSAGE);
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting()) return;
		actualizarEstadoAcciones();
		int fila = delegado.lst_contactos.getSelectedRow();
		if (fila < 0) return;
		int indice = delegado.lst_contactos.convertRowIndexToModel(fila);
		List<Persona> lista = contactos.getContactos();
		if (indice >= 0 && indice < lista.size()) {
			Persona p = lista.get(indice);
			delegado.txt_nombres.setText(p.getNombre());
			delegado.txt_telefono.setText(p.getTelefono());
			delegado.txt_email.setText(p.getEmail());
			delegado.chb_favorito.setSelected(p.isFavorito());
			delegado.cmb_categoria.setSelectedItem(p.getCategoria());
		}
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getSource() == delegado.cmb_categoria) {
			Object sel = delegado.cmb_categoria.getSelectedItem();
			categoria = sel != null ? sel.toString() : "";
		} else if (e.getSource() == delegado.chb_favorito) {
			favorito = delegado.chb_favorito.isSelected();
		}
	}

	// Se ejecuta al cambiar de idioma: re-traduce categorías, tabla, reporte y barra.
	private void cambiarIdioma(Locale nuevo) {
		delegado.actualizarIdioma(nuevo);
		inicializarCategorias();
		tabla.cargar(contactos.getContactos());
		configurarMenuContextual();
		reporte = new ReporteEstadisticas(delegado.getBundle());
		mostrarReporte();
	}

	private void mostrarReporte() {
		String texto = reporte.generar(contactos.getContactos());
		delegado.tbl_estadisticas.setFont(new Font("Monospaced", Font.PLAIN, 12));
		delegado.tbl_estadisticas.setText(texto);
		delegado.tbl_estadisticas.setCaretPosition(0);
	}

	private void guardarReporte() {
		String contenido = reporte.generar(contactos.getContactos());
		new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				reporte.guardar(contenido);
				return null;
			}

			@Override
			protected void done() {
				try {
					get();
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(delegado,
							t("msg.reporte.error", "No se pudo actualizar el reporte estadístico:") + " " + ex.getMessage(),
							t("dialog.error.titulo", "Error"), JOptionPane.ERROR_MESSAGE);
				}
			}
		}.execute();
	}

	private void barraProgreso() {
		int total = contactos.getContactos().size();
		delegado.barra.setMinimum(0);
		delegado.barra.setMaximum(total);
		delegado.barra.setValue(0);

		if (total == 0) {
			delegado.barra.setString(t("progress.vacio", "No hay contactos para cargar"));
			return;
		}

		Locale locale = delegado.getBundle() != null ? delegado.getBundle().getLocale() : Locale.getDefault();
		Thread hilo = new Thread(() -> {
			long inicio = System.nanoTime();
			for (int v = 1; v <= total; v++) {
				final int valor = v;
				final long ms = (System.nanoTime() - inicio) / 1_000_000;
				SwingUtilities.invokeLater(() -> {
					delegado.barra.setValue(valor);
					delegado.barra.setString(String.format(locale,
							t("progress.formato", "%d/%d contactos - %d ms"), valor, total, ms));
				});
				try {
					Thread.sleep(35);
				} catch (InterruptedException ex) {
					Thread.currentThread().interrupt();
					LOGGER.log(Level.WARNING, "La carga visual de contactos fue interrumpida", ex);
					return;
				}
			}
		}, "barra-progreso-contactos");
		hilo.setDaemon(true);
		hilo.start();
	}
}