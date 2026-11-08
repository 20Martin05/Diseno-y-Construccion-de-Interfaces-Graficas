package modelo;

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
		this.nombre = normalizarNombre(nombre);
		this.telefono = telefono;
		this.email = normalizarEmail(email);
		this.categoria = categoria;
		this.favorito = favorito;
	}

	public int getId() { return id; }
	public void setId(int id) { this.id = id; }

	public String getNombre() { return nombre; }
	public void setNombre(String nombre) { this.nombre = normalizarNombre(nombre); }

	public String getTelefono() { return telefono; }
	public void setTelefono(String telefono) { this.telefono = telefono; }

	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = normalizarEmail(email); }

	public String getCategoria() { return categoria; }
	public void setCategoria(String categoria) { this.categoria = categoria; }

	public boolean isFavorito() { return favorito; }
	public void setFavorito(boolean favorito) { this.favorito = favorito; }

	public String datosContacto() {
		return id + ";" + nombre + ";" + telefono + ";" + email + ";" + categoria + ";" + favorito;
	}

	private static String normalizarNombre(String valor) {
		return valor == null ? "" : valor.toUpperCase(java.util.Locale.ROOT);
	}

	private static String normalizarEmail(String valor) {
		return valor == null ? "" : valor.toLowerCase(java.util.Locale.ROOT);
	}
}
