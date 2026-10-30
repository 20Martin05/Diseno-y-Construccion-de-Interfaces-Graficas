package modelo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PersonaDAO {
	private static final Logger LOGGER = Logger.getLogger(PersonaDAO.class.getName());
	private static final Path DATA_DIRECTORY = resolverDirectorioDatos();
	private static final String CONTACTS_FILE = "tabla_contactos.csv";

	private File archivo;
	private final Persona persona;

	public PersonaDAO(Persona persona) {
		this.persona = persona;
		this.archivo = DATA_DIRECTORY.toFile();
		prepararArchivo();
	}

	private void prepararArchivo() {
		if (!archivo.exists()) {
			if (!archivo.mkdirs() && !archivo.isDirectory()) {
				LOGGER.severe("No se pudo crear el directorio de datos: " + archivo.getAbsolutePath());
				return;
			}
		}
		archivo = new File(archivo, CONTACTS_FILE);
		if (!archivo.exists()) {
			try {
				if (!archivo.createNewFile() && !archivo.exists()) {
					LOGGER.severe("No se pudo crear el archivo de contactos: " + archivo.getAbsolutePath());
				}
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, "No se pudo crear el archivo de contactos", e);
			}
		}
	}

	public void escribirArchivo() throws IOException {
		try (BufferedWriter escribir = Files.newBufferedWriter(archivo.toPath(), StandardCharsets.UTF_8,
				StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
			escribir.write(persona.datosContacto());
			escribir.newLine();
		}
	}

	public List<Persona> leerArchivo() throws IOException {
		List<Persona> personas = new ArrayList<>();
		if (!Files.exists(archivo.toPath())) {
			return personas;
		}
		try (var lector = Files.newBufferedReader(archivo.toPath(), StandardCharsets.UTF_8)) {
			String linea;
			while ((linea = lector.readLine()) != null) {
				if (linea.isBlank()) continue;
				String[] partes = linea.split(";", -1);
				if (partes.length >= 6) {
					// Omitir cabecera si la primera columna no es un número (ej. "ID")
					if (partes[0].equalsIgnoreCase("id") || !partes[0].matches("\\d+")) {
						continue;
					}
					try {
						Persona p = new Persona();
						p.setId(Integer.parseInt(partes[0]));
						p.setNombre(partes[1]);
						p.setTelefono(partes[2]);
						p.setEmail(partes[3]);
						p.setCategoria(partes[4]);
						p.setFavorito(Boolean.parseBoolean(partes[5]));
						personas.add(p);
					} catch (NumberFormatException e) {
						LOGGER.log(Level.WARNING, "Línea corrupta omitida en CSV por ID inválido", e);
					}
				}
			}
		}
		return personas;
	}

	private static Path resolverDirectorioDatos() {
		Path rutaProyecto = Paths.get("src", "main", "resources", "files");
		if (Files.isDirectory(rutaProyecto)) {
			return rutaProyecto;
		}
		return Paths.get("data", "files");
	}

	public void actualizarContactos(List<Persona> personas) throws IOException {
		try (BufferedWriter escritor = Files.newBufferedWriter(archivo.toPath(), StandardCharsets.UTF_8,
				StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
			for (Persona p : personas) {
				escritor.write(p.datosContacto());
				escritor.newLine();
			}
		}
	}

	public void exportarCSV(File destino, List<Persona> lista) throws IOException {
		try (BufferedWriter bw = Files.newBufferedWriter(destino.toPath(), StandardCharsets.UTF_8)) {
			bw.write("ID;NOMBRE;TELÉFONO;EMAIL;CATEGORIA;FAVORITO");
			bw.newLine();
			for (Persona p : lista) {
				bw.write(p.datosContacto());
				bw.newLine();
			}
		}
	}
}
