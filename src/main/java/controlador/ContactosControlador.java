package controlador;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import modelo.Persona;
import modelo.PersonaDAO;
import vista.ventana;

// Validación y operaciones CRUD sobre la lista de contactos. Muestra los mensajes al usuario.
public class ContactosControlador {
    private static final Logger LOGGER = Logger.getLogger(ContactosControlador.class.getName());

    private final ventana delegado;
    private final List<Persona> contactos;

    public ContactosControlador(ventana delegado, List<Persona> contactos) {
        this.delegado = delegado;
        this.contactos = contactos;
    }

    public List<Persona> getContactos() {
        return contactos;
    }

    private String t(String clave, String respaldo) {
        var bundle = delegado.getBundle();
        return bundle != null ? bundle.getString(clave) : respaldo;
    }

    private void info(String mensaje) {
        JOptionPane.showMessageDialog(delegado, mensaje);
    }

    private void error(String mensaje) {
        JOptionPane.showMessageDialog(delegado, mensaje, t("dialog.error.titulo", "Error"), JOptionPane.ERROR_MESSAGE);
    }

    public boolean datosValidos(String nombres, String telefono, String email, String categoria) {
        if (nombres.isBlank() || telefono.isBlank() || email.isBlank()) {
            info(t("msg.campos.vacios", "Todos los campos deben ser llenados."));
            return false;
        }
        if (!nombres.matches("[\\p{L} .'-]{2,80}")) {
            info(t("msg.nombre.invalido", "El nombre contiene caracteres no válidos."));
            return false;
        }
        if (!telefono.matches("\\d{7,15}")) {
            info(t("msg.telefono.invalido", "El teléfono debe contener entre 7 y 15 dígitos."));
            return false;
        }
        if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            info(t("msg.email.invalido", "Ingrese un correo electrónico válido."));
            return false;
        }
        if (categoria.isBlank() || categoria.equals(t("cat.default", "Elija una Categoria"))) {
            info(t("msg.categoria.invalida", "Elija una categoría."));
            return false;
        }
        return true;
    }

    // Devuelve true si se guardó correctamente.
    public boolean agregar(String nombres, String telefono, String email, String categoria, boolean favorito) {
        if (!datosValidos(nombres, telefono, email, categoria)) return false;
        int nuevoId = contactos.stream().mapToInt(Persona::getId).max().orElse(0) + 1;
        Persona persona = new Persona(nuevoId, nombres, telefono, email, categoria, favorito);
        try {
            new PersonaDAO(persona).escribirArchivo();
            contactos.add(persona);
            info(t("msg.contacto.registrado", "Contacto Registrado!!!"));
            return true;
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Fallo al escribir en disco el nuevo contacto", ex);
            error(t("msg.error.guardar", "Error al guardar el contacto: ") + ex.getMessage());
            return false;
        }
    }

    // Modifica el contacto de la posición indicada (índice del modelo).
    public boolean modificar(int indice, String nombres, String telefono, String email, String categoria, boolean favorito) {
        if (!datosValidos(nombres, telefono, email, categoria)) return false;
        try {
            int id = contactos.get(indice).getId();
            contactos.set(indice, new Persona(id, nombres, telefono, email, categoria, favorito));
            new PersonaDAO(new Persona()).actualizarContactos(contactos);
            info(t("msg.modificado.exito", "Modificado con Éxito"));
            return true;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Fallo al actualizar el contacto en índice: " + indice, ex);
            error(t("msg.error.modificar", "Error al modificar el contacto"));
            return false;
        }
    }

    // Elimina el contacto de la posición indicada (índice del modelo).
    public boolean eliminar(int indice) {
        try {
            contactos.remove(indice);
            new PersonaDAO(new Persona()).actualizarContactos(contactos);
            info(t("msg.eliminado.exito", "Eliminado con Éxito"));
            return true;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Fallo al eliminar el contacto en índice: " + indice, ex);
            error(t("msg.error.eliminar", "Error al eliminar el contacto"));
            return false;
        }
    }
}