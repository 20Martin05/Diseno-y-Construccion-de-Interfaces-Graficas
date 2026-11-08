package controlador;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import modelo.Persona;

// Genera el texto del reporte estadístico según el idioma activo y lo guarda en disco.
public class ReporteEstadisticas {
    private static final String LINEA_DOBLE = "=".repeat(73);
    private static final String LINEA_SIMPLE = "-".repeat(73);
    private static final String[][] PALABRAS_CATEGORIA = {
            {"familia|family|família", "familia"},
            {"amig|friend", "amigos"},
            {"trabaj|work|trabalho", "trabajo"},
            {"universidad|university|universidade", "universidad"},
            {"client", "clientes"},
            {"emergen", "emergencias"}
    };
    private static final Pattern[] PATRONES_CATEGORIA = {
            Pattern.compile(".*(familia|family|família).*"),
            Pattern.compile(".*(amig|friend).*"),
            Pattern.compile(".*(trabaj|work|trabalho).*"),
            Pattern.compile(".*(universidad|university|universidade).*"),
            Pattern.compile(".*(client).*"),
            Pattern.compile(".*(emergen).*")
    };

    private final ResourceBundle bundle;
    private final Locale locale;

    public ReporteEstadisticas(ResourceBundle bundle) {
        this.bundle = bundle;
        this.locale = bundle != null ? bundle.getLocale() : Locale.getDefault();
    }

    private String t(String clave, String respaldo) {
        return bundle != null ? bundle.getString(clave) : respaldo;
    }

    public String generar(List<Persona> lista) {
        if (lista == null || lista.isEmpty()) {
            return t("stats.vacio", "No hay contactos disponibles para generar el reporte.");
        }

        int total = lista.size();
        int fav = 0;
        Map<String, Integer> porCategoria = new LinkedHashMap<>();
        String sinCategoria = t("stats.sin.categoria", "Sin Categoría");

        for (Persona p : lista) {
            if (p == null) continue;
            if (p.isFavorito()) fav++;
            porCategoria.merge(claveCategoria(p.getCategoria(), sinCategoria), 1, Integer::sum);
        }
        int noFav = total - fav;

        StringBuilder sb = new StringBuilder();
        sb.append(LINEA_DOBLE).append("\n");
        sb.append(String.format("%-73s%n", "                    " + t("stats.titulo.reporte", "REPORTE ESTADÍSTICO DE CONTACTOS")));
        sb.append(LINEA_DOBLE).append("\n\n");
        sb.append(String.format("- %s: %d%n%n%n", t("stats.total.registrados", "TOTAL DE CONTACTOS REGISTRADOS"), total));

        sb.append(LINEA_SIMPLE).append("\n");
        sb.append("  ").append(t("stats.desglose.favoritos", "DESGLOSE POR ESTADO DE FAVORITOS")).append(":\n");
        sb.append(LINEA_SIMPLE).append("\n");
        sb.append(filaPorcentaje(t("stats.favoritos", "Favoritos"), 13, fav, total)).append("\n");
        sb.append(filaPorcentaje(t("stats.no.favoritos", "No Favoritos"), 13, noFav, total)).append("\n\n");

        sb.append(LINEA_SIMPLE).append("\n");
        sb.append("  ").append(t("stats.distribucion.categorias", "DISTRIBUCIÓN POR CATEGORÍAS")).append(":\n");
        sb.append(LINEA_SIMPLE).append("\n");
        for (Map.Entry<String, Integer> entry : porCategoria.entrySet()) {
            sb.append(filaPorcentaje(entry.getKey(), 18, entry.getValue(), total)).append("\n");
        }
        sb.append("\n");

        String fecha = LocalDateTime.now().format(
                DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.MEDIUM).withLocale(locale));
        sb.append(LINEA_DOBLE).append("\n");
        sb.append(String.format(locale, "  %s: %s%n", t("stats.fecha.calculo", "Fecha de cálculo"), fecha));
        sb.append(LINEA_DOBLE).append("\n");

        return sb.toString();
    }

    private String filaPorcentaje(String etiqueta, int ancho, int cantidad, int total) {
        double pct = cantidad * 100.0 / total;
        return String.format(locale, "- %-" + ancho + "s: %-5d (%.1f%%)%n", etiqueta, cantidad, pct);
    }

    private String claveCategoria(String cruda, String sinCategoria) {
        if (cruda == null || cruda.isBlank()) return sinCategoria;
        String l = cruda.trim().toLowerCase(Locale.ROOT);
        for (int i = 0; i < PATRONES_CATEGORIA.length; i++) {
            if (PATRONES_CATEGORIA[i].matcher(l).matches()) {
                return t("cat." + PALABRAS_CATEGORIA[i][1], cruda);
            }
        }
        return t("cat.otros", "Otros");
    }

    // Escribe el reporte en src/main/resources/files (o data/files si no existe el proyecto).
    public void guardar(String contenido) throws IOException {
        Path ruta = Paths.get("src", "main", "resources", "files", "reporte_estadisticas.txt");
        if (!Files.isDirectory(ruta.getParent())) {
            ruta = Paths.get("data", "files", "reporte_estadisticas.txt");
        }
        Files.createDirectories(ruta.getParent());
        Files.writeString(ruta, contenido, StandardCharsets.UTF_8);
    }
}
