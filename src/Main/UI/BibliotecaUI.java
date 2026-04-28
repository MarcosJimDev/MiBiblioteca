package Main.UI;

import Main.Java.Autor;
import Main.Java.Libro;
import Main.Java.Editorial;
import Main.Utils.Utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class BibliotecaUI {
    public static void mostrarMenu() {
        System.out.println("\nElige una opción: ");
        System.out.println("\t1. Mostrar todos los libros.");
        System.out.println("\t2. Mostrar todos los autores.");
        System.out.println("\t3. Mostrar todos las editoriales.");
        System.out.println("\t4. Agregar nuevo libro.");
        System.out.println("\t5. Agregar nuevo autor.");
        System.out.println("\t6. Agregar nueva editorial.");
        System.out.println("\t7. Eliminar libro.");
        System.out.println("\t8. Eliminar autor.");
        System.out.println("\t9. Eliminar editorial.");
        System.out.println("\t10. Buscar libro.");
        System.out.println("\t11. Buscar autor.");
        System.out.println("\t12. Buscar grupo editorial.");
        System.out.println("\t13. Buscar libro por grupo editorial.");
        System.out.println("\t14. Buscar libro por autor.");
        System.out.println("\t15. Actualizar año de lectura (libro).");
        System.out.println("\t0. Salir del programa.");
    }

    public static void mostrarBienvenida(HashMap<Integer, Libro> libros) {
        System.out.println("*** BIENVENIDO A MIBIBLIOTECA ***");
        System.out.println("Actualmente hay un total de " + libros.size() + " libros.");
    }

    public static String pedirCadena(Scanner sc, String mensaje) {
        System.out.print(mensaje);
        String entrada = sc.nextLine().trim();
        return entrada;
    }

    public static int pedirEntero(Scanner sc, String mensaje) {
        int numero = 0;
        boolean valido = false;

        do {
            System.out.print(mensaje);
            String entrada = sc.nextLine();
            try {
                numero = Integer.parseInt(entrada);
                if (numero >= 0)
                    valido = true;
            } catch (NumberFormatException e) {
                System.err.println("ERROR: Debes introducir un número entero válido.");
            }
        } while (!valido);
        return numero;
    }

    public static double pedirDouble(Scanner sc, String mensaje) {
        double numero = 0;
        boolean valido = false;

        while (!valido) {
            try {
                System.out.print(mensaje);
                String entrada = sc.nextLine();
                numero = Double.parseDouble(entrada);
                valido = true;
            } catch (NumberFormatException e) {
                System.err.println("ERROR: Debes introducir un número entero válido.");
            }
        }
        return numero;
    }

    public static boolean pedirBoolean(Scanner sc, String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String entrada = sc.nextLine().trim();

            if (entrada.equalsIgnoreCase("s")) return true;
            if (entrada.equalsIgnoreCase("n")) return false;

            System.out.println("Por favor, introduce 'S' para Sí o 'N' para No.");
        }
    }

    public static Date dateValido(Scanner sc, String mensaje) {
        Date fecha = null;
        String fechaUsuario;
        do {
            System.out.println(mensaje);
            fechaUsuario = sc.nextLine();
            try {
                DateTimeFormatter formateador = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate fechaLocal = LocalDate.parse(fechaUsuario, formateador);
                fecha = Date.valueOf(fechaLocal);
            } catch (DateTimeParseException e) {
                System.out.println("Formato inválido.");
            }
        } while (fechaUsuario.equalsIgnoreCase("") || fecha == null);

        return fecha;
    }

    public static String campoObligatorio(Scanner sc, String mensaje) {
        String cadena;
        System.out.println(mensaje);
        do {
            cadena = sc.nextLine();
            if (cadena.equalsIgnoreCase("")) {
                System.err.println("ERROR: el campo no puede estar vacío.");
                System.out.println(mensaje);
            }
        } while (cadena.equalsIgnoreCase(""));
        return cadena;
    }

    public static void buscarLibro(HashMap<Integer, Libro> libros, Scanner sc) {
        String busqueda = campoObligatorio(sc, "Introduce el título del libro: ");
        if (busqueda.equalsIgnoreCase("salir")) return;

        String busquedaNorm = Utils.normalizar(busqueda);
        List<Libro> librosEncontrados = new ArrayList<>();

        for (Libro l : libros.values()) {
            if (Utils.normalizar(l.getTitulo()).contains(busquedaNorm)) {
                librosEncontrados.add(l);
            }
        }

        if (librosEncontrados.isEmpty()) {
            System.err.println("ERROR: '" + busqueda + "' no coincide con ningún título.");
        } else {
            System.out.println("Coincidencias: " + librosEncontrados.size());
            for (Libro l : librosEncontrados) {
                l.mostrarLibro();
            }
        }
    }

    public static void buscarAutor(HashMap<Integer, Autor> autores, Scanner sc) {
        String busqueda = campoObligatorio(sc, "Introduce el nombre del autor: ");
        if (busqueda.equalsIgnoreCase("salir")) return;

        String busquedaNorm = Utils.normalizar(busqueda);
        List<Autor> autoresEncontrados = new ArrayList<>();

        for (Autor a : autores.values()) {
            if (Utils.normalizar(a.getNombre()).contains(busquedaNorm)) {
                autoresEncontrados.add(a);
            }
        }

        if (autoresEncontrados.isEmpty()) {
            System.err.println("ERROR: No se han encontrado autores que coincidan con '" + busqueda + "'.");
        } else {
            System.out.println("Coincidencias encontradas: " + autoresEncontrados.size());
            for (Autor a : autoresEncontrados) {
                a.mostrarAutor();
            }
        }
    }

    public static void buscarEditorial(HashMap<Integer, Editorial> editoriales, Scanner sc) {
        String busqueda = campoObligatorio(sc, "Introduce el nombre del grupo editorial: ");
        if (busqueda.equalsIgnoreCase("salir")) return;

        String busquedaNorm = Utils.normalizar(busqueda);
        List<Editorial> editorialesEncontradas = new ArrayList<>();

        for (Editorial e : editoriales.values()) {
            if (Utils.normalizar(e.getGrupoEditorial()).contains(busquedaNorm)) {
                editorialesEncontradas.add(e);
            }
        }

        if (editorialesEncontradas.isEmpty()) {
            System.err.println("ERROR: No se han encontrado editoriales con el nombre '" + busqueda + "'.");
        } else {
            System.out.println("Coincidencias: " + editorialesEncontradas.size());
            for (Editorial e : editorialesEncontradas) {
                e.mostrarEditorial();
            }
        }
    }

    public static void filtrarLibrosPorEditorial(HashMap<Integer, Libro> libros, Scanner sc) {
        String busqueda = campoObligatorio(sc, "Introduce el nombre del grupo editorial: ");
        if (busqueda.equalsIgnoreCase("salir")) return;

        String busquedaNorm = Utils.normalizar(busqueda);
        List<Libro> librosEncontrados = new ArrayList<>();

        for (Libro l : libros.values()) {
            // Accedemos al grupo editorial del libro
            if (Utils.normalizar(l.getEditorial().getGrupoEditorial()).contains(busquedaNorm)) {
                librosEncontrados.add(l);
            }
        }

        if (librosEncontrados.isEmpty()) {
            System.err.println("No se han encontrado libros de la editorial: " + busqueda);
        } else {
            System.out.println("Libros encontrados para la editorial '" + busqueda + "': " + librosEncontrados.size());
            for (Libro l : librosEncontrados) {
                l.mostrarLibro();
            }
        }
    }

    public static void filtrarLibrosPorAutor(HashMap<Integer, Libro> libros, Scanner sc) {
        String busqueda = campoObligatorio(sc, "Introduce el nombre del autor: ");
        if (busqueda.equalsIgnoreCase("salir")) return;

        String busquedaNorm = Utils.normalizar(busqueda);
        List<Libro> librosEncontrados = new ArrayList<>();

        for (Libro l : libros.values()) {
            boolean coincideAutor1 = Utils.normalizar(l.getAutor1().getNombre()).contains(busquedaNorm);
            boolean coincideAutor2 = (l.getAutor2() != null) && Utils.normalizar(l.getAutor2().getNombre()).contains(busquedaNorm);

            if (coincideAutor1 || coincideAutor2) {
                librosEncontrados.add(l);
            }
        }

        if (librosEncontrados.isEmpty()) {
            System.err.println("No se han encontrado libros de: " + busqueda);
        } else {
            System.out.println("Libros encontrados de " + busqueda + ": " + librosEncontrados.size());
            for (Libro l : librosEncontrados) {
                l.mostrarLibro();
            }
        }
    }
}
