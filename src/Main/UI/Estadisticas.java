package Main.UI;

import Main.Java.Autor;
import Main.Java.Editorial;
import Main.Java.Libro;
import Main.Utils.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class Estadisticas {
    public static void mostrarMenuEstadisticas() {
        System.out.println("\t1. Número de libros leídos y pendientes.");
        System.out.println("\t2. Libros más largo y más corto.");
        System.out.println("\t3. Total y media de páginas.");
        System.out.println("\t4. Año más productivo.");
        System.out.println("\t5. Top 3 géneros más frecuentes.");
        System.out.println("\t6. Autor con más libros.");
        System.out.println("\t7. Distribución por editorial.");
        System.out.println("\t8. Libros comprados por año.");
        System.out.println("\t9. Volver al menú principal.");
    }

    public static void interactuarMenuEstadisticas(HashMap<Integer, Libro> libros, HashMap<Integer, Autor> autores, HashMap<Integer, Editorial> editoriales, Scanner sc) {
        int opcion = 0;
        do {
            System.out.println();
            mostrarMenuEstadisticas();
            opcion = BibliotecaUI.pedirEntero(sc, "Elige una opción: ");

            switch (opcion) {
                case 1 -> numLibrosLeidosPendientes(libros);
                case 2 -> libroLargoCorto(libros);
                case 3 -> mostrarTotalMediaPaginas(libros);
                case 4 -> mostrarAnyoMasProductivo(libros);
                case 5 -> mostrarTopGeneros(libros);
                case 6 -> mostrarAutorEstrella(libros);
                case 7 -> mostrarDistribucionEditoriales(libros);
                case 8 -> mostrarComprasPorAnyo(libros);
                case 9 -> {
                    System.out.println("Volviendo al menú principal...");
                    Utils.tiempoMuerto();
                }
                default -> System.out.println("ERROR: respuesta desconocida o no registrada.");
            }
        } while (opcion != 9);
    }

    private static void numLibrosLeidosPendientes(HashMap<Integer, Libro> libros) {
        int totalLibrosLeidos = 0, totalLibrosSinLeer = 0;
        System.out.println("*** TOTAL DE LIBROS LEÍDOS Y NO LEÍDOS ***");
        System.out.println("\nCalculando...");
        Utils.tiempoMuerto();
        for (Libro libro : libros.values()) {
            if (libro.getAnyoLectura() > 0) {
                totalLibrosLeidos++;
            } else {
                totalLibrosSinLeer++;
            }
        }
        System.out.println("\nLibros leídos: " + totalLibrosLeidos);
        System.out.println("Libros no leídos: " + totalLibrosSinLeer);
    }

    private static void libroLargoCorto(HashMap<Integer, Libro> libros) {
        int actual;
        System.out.println("*** LIBRO MÁS LARGO Y MÁS CORTO ***");
        System.out.println("\nCalculando...");
        Utils.tiempoMuerto();
        Libro libroCorto = libros.get(1), libroLargo = libros.get(1);
        for (Libro l : libros.values()) {
            actual = l.getNumPaginas();
            if (actual < libroCorto.getNumPaginas()) {
                libroCorto = l;
            }
            if (actual > libroLargo.getNumPaginas()) {
                libroLargo = l;
            }
        }

        System.out.println("\nLibro más largo: " + libroLargo.getTitulo() + " de " + libroLargo.getAutor1().getNombre() + " con " + libroLargo.getNumPaginas() + " páginas.");
        System.out.println("\nLibro más corto: " + libroCorto.getTitulo() + " de " + libroCorto.getAutor1().getNombre() + " con " + libroCorto.getNumPaginas() + " páginas.");
    }

    private static void mostrarTotalMediaPaginas(HashMap<Integer, Libro> libros) {
        int totalPaginas = 0;
        System.out.println("*** TOTAL Y MEDIA DE PÁGINAS ***");
        System.out.println("\nCalculando...");
        Utils.tiempoMuerto();
        for (Libro l : libros.values()) {
            totalPaginas += l.getNumPaginas();
        }
        double media = (double) totalPaginas / libros.size();
        System.out.println("\nTotal de páginas en la librería: " + totalPaginas);
        System.out.println("Media de páginas: " + media);
    }

    private static void mostrarAnyoMasProductivo(HashMap<Integer, Libro> libros) {
        libros.values().stream()
                .filter(l -> l.getAnyoLectura() > 0)
        .collect(java.util.stream.Collectors.groupingBy(
                Libro::getAnyoLectura,
                java.util.stream.Collectors.counting()
        ))
                .entrySet().stream()
                .max(java.util.Map.Entry.comparingByValue())
                .ifPresent(entry -> {
                    System.out.println("Tu año más productivo fue " + entry.getKey() + " con " + entry.getValue() + " libros leídos.");
                });
    }

    public static void mostrarTopGeneros(HashMap<Integer, Libro> libros) {
        System.out.println("\n--- TOP 3 GÉNEROS MÁS FRECUENTES ---");
        libros.values().stream()
                .collect(Collectors.groupingBy(Libro::getCategoria, Collectors.counting()))
            .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(3)
                .forEach(e -> System.out.println(" • " + e.getKey() + ": " + e.getValue() + " libros"));
    }

    public static void mostrarAutorEstrella(HashMap<Integer, Libro> libros) {
        System.out.println("\n--- AUTOR CON MÁS LIBROS ---");
        libros.values().stream()
                .map(l -> l.getAutor1().getNombre())
        // Filtramos para ignorar a Geronimo Stilton (case-insensitive para evitar fallos)
        .filter(nombre -> !nombre.equalsIgnoreCase("Geronimo Stilton") && !nombre.equalsIgnoreCase("Richard Castle") && !nombre.equalsIgnoreCase("Desconocido"))
                .collect(Collectors.groupingBy(nombre -> nombre, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .ifPresentOrElse(
                        e -> System.out.println("El autor con más presencia es '" + e.getKey() + "' con " + e.getValue() + " libros."),
                        () -> System.err.println("No hay datos suficientes.")
                );
    }

    public static void mostrarDistribucionEditoriales(HashMap<Integer, Libro> libros) {
        System.out.println("\n--- DISTRIBUCIÓN POR EDITORIAL ---");
        libros.values().stream()
                .collect(Collectors.groupingBy(l -> l.getEditorial().getGrupoEditorial(), Collectors.counting()))
            .forEach((editorial, cuenta) -> {
            double porcentaje = (cuenta * 100.0) / libros.size();
            System.out.printf(" • %-25s: %d libros (%.1f%%)%n", editorial, cuenta, porcentaje);
        });
    }

    public static void mostrarComprasPorAnyo(HashMap<Integer, Libro> libros) {
        System.out.println("\n--- LIBROS COMPRADOS POR AÑO ---");
        libros.values().stream()
                .filter(l -> l.getAnyoAdquisicion() > 0)
            .collect(Collectors.groupingBy(Libro::getAnyoAdquisicion, TreeMap::new, Collectors.counting()))
            .forEach((anyo, cuenta) -> {
            System.out.println(" • " + anyo + ": " + cuenta + " libros adquiridos.");
        });
    }
}
