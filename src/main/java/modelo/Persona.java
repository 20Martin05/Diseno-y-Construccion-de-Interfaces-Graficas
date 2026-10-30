package modelo;

import java.util.Locale;

public class Persona {
	private int id;
	private String nombre;
	private String telefono;
	private String email;
	private String categoria;
	private boolean favorito;

	public Persona() {
		this.id = 0;
		this.nombre = "";
		this.telefono = "";
		this.email = "";
		this.categoria = "";
		this.favorito = false;
	}

	public Persona(int id, String nombre, String telefono, String email, String categoria, boolean favorito) {
		this.id = id;
		this.nombre = nombre;
		this.telefono = telefono;
		this.email = email;
		this.categoria = categoria;
		this.favorito = favorito;
	}

	public int getId() { return id; }
	public void setId(int id) { this.id = id; }

	public String getNombre() { return nombre != null ? nombre.toUpperCase(Locale.ROOT) : ""; }
	public void setNombre(String nombre) { this.nombre = nombre; }

	public String getTelefono() { return telefono; }
	public void setTelefono(String telefono) { this.telefono = telefono; }

	public String getEmail() { return email != null ? email.toLowerCase(Locale.ROOT) : ""; }
	public void setEmail(String email) { this.email = email; }

	public String getCategoria() { return categoria; }
	public void setCategoria(String categoria) { this.categoria = categoria; }

	public boolean isFavorito() { return favorito; }
	public void setFavorito(boolean favorito) { this.favorito = favorito; }

	public String datosContacto() {
		return String.format(Locale.ROOT, "%d;%s;%s;%s;%s;%s", id, nombre, telefono, email, categoria, favorito);
	}
}

