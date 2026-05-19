package Main.UI;

import Main.Config.AppInfo;
import Main.DAO.*;
import Main.Java.Autor;
import Main.Java.Editorial;
import Main.Java.Libro;
import Main.Java.LibroDeseado;
import Main.Utils.Utils;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class BibliotecaUI {
    public static void mostrarMenu() {
        System.out.println("\nElige una opción: ");
        System.out.println("\t1. Mostrar todos los libros.");
        System.out.println("\t2. Mostrar todos los libros leídos.");
        System.out.println("\t3. Mostrar todos los autores.");
        System.out.println("\t4. Mostrar todos las editoriales.");
        System.out.println("\t5. Menú agregar registros.");
        System.out.println("\t6. Menú eliminar registros.");
        System.out.println("\t7. Buscador.");
        System.out.println("\t8. Actualizar año de lectura.");
        System.out.println("\t9. Ver estadísticas.");
        System.out.println("\t0. Salir del programa.");
    }

    public static void mostrarBienvenida(HashMap<Integer, Libro> libros, HashMap<Integer, LibroDeseado> librosDeseados) {
        System.out.println("*** BIENVENIDO A MIBIBLIOTECA ***");
        System.out.println("Actualmente hay un total de " + libros.size() + " libros y " + librosDeseados.size() + " libros deseados.");
    }

    public static void interaccionMenu(HashMap<Integer, Libro> libros, HashMap<Integer, Autor> autores, HashMap<Integer, Editorial> editoriales, HashMap<Integer, LibroDeseado> librosDeseados, Scanner sc) {
        String opcion = "";
        int opcionNum;

        do {
            opcionNum = -1;
            BibliotecaUI.mostrarMenu();

            try {
                opcion = sc.nextLine();
            } catch (NoSuchElementException e) {
                System.err.println("ERROR: debes introducir un valor o un comando. " + e);
            }

            if (opcion.equals("--version")) {
                System.out.println(AppInfo.getFullVersion());
            } else if (!opcion.isEmpty() && opcion.length() <= 2) {
                try {
                    opcionNum = Integer.parseInt(opcion);

                    switch (opcionNum) {
                        case 1 ->BibliotecaUI.mostrarLibrosPaginados(libros, sc);
                        case 2 -> BibliotecaUI.mostrarLibrosPaginadosLeidos(libros, sc);
                        case 3 -> BibliotecaUI.mostrarAutoresPaginados(autores, sc);
                        case 4 -> {
                            System.out.println("\n--- LISTADO DE EDITORIALES ---");
                            System.out.printf("%-3s | %-28s | %-20s%n",
                                    "ID", "GRUPO EDITORIAL", "FIRMA EDITORIAL");
                            System.out.println("-".repeat(53));

                            for (Editorial e : editoriales.values()) {
                                e.mostrarInfoEditorial();
                            }
                        }
                        case 5 -> Agregar.interactuarMenuAgregar(libros, autores, editoriales, librosDeseados, sc);
                        case 6 -> Eliminar.interactuarMenuEliminar(libros, autores, editoriales, librosDeseados, sc);
                        case 7 -> Buscador.interactuarMenuBuscador(libros, autores, editoriales, sc);
                        case 8 -> LibroDAO.actualizarAnyoLectura(libros, sc);
                        case 9 -> Estadisticas.interactuarMenuEstadisticas(libros, autores, editoriales, sc);
                        case 0 -> System.out.println("Saliendo del programa...");
                        default -> System.out.println("ERROR: opción no registrada.");
                    }
                } catch (NumberFormatException e) {
                    System.err.println("ERROR: debes introducir un número para elegir una opción. " + e);
                }
            } else {
                System.out.println("ERROR: comando o elemento no registrado.");
            }
        } while(opcionNum != 0);
    }

    public static void mostrarLibrosPaginados(HashMap<Integer, Libro> librosMap, Scanner sc) {
        List<Libro> listaLibros = new ArrayList<>(librosMap.values());
        int totalLibros = listaLibros.size();
        int tamanoPagina = 50;
        int paginaActual = 0;
        int totalPaginas = (int) Math.ceil((double) totalLibros / tamanoPagina);

        while (true) {
            int inicio = paginaActual * tamanoPagina;
            int fin = Math.min(inicio + tamanoPagina, totalLibros);

            System.out.println("\n" + " ".repeat(100) + "--- LISTADO DE LIBROS (Página " + (paginaActual + 1) + " de " + totalPaginas + ") ---");
            System.out.printf("%-3s | %-80s | %4s | %-27s | %-20s | %-15s | %-30s | %-30s | %-8s | %4s%n",
                    "ID", "TÍTULO", "NPAG", "AUTOR 1", "AUTOR 2", "GÉNERO", "CATEGORÍA", "EDITORIAL", "LCT", "ADQ");
            System.out.println("-".repeat(248));

            for (int i = inicio; i < fin; i++) {
                listaLibros.get(i).mostrarInfoLibro();
            }

            System.out.println("\n[S] Siguiente página | [A] Anterior página | [M] Menú Principal");
            System.out.print("Elige una opción: ");
            String opcion = sc.nextLine().toUpperCase();

            if (opcion.equals("S") && (paginaActual + 1) < totalPaginas) {
                paginaActual++;
            } else if (opcion.equals("A") && paginaActual > 0) {
                paginaActual--;
            } else if (opcion.equals("M")) {
                break;
            } else {
                System.out.println("Opción no válida o no hay más páginas.");
            }
        }
    }

    public static void mostrarLibrosPaginadosLeidos(HashMap<Integer, Libro> librosMap, Scanner sc) {
        List<Libro> listaLibros = new ArrayList<>(librosMap.values());
        int totalLibros = listaLibros.size();
        int tamanoPagina = 50;
        int paginaActual = 0;
        int totalPaginas = (int) Math.ceil((double) totalLibros / tamanoPagina);

        while (true) {
            int inicio = paginaActual * tamanoPagina;
            int fin = Math.min(inicio + tamanoPagina, totalLibros);

            System.out.println("\n" + " ".repeat(100) + "--- LISTADO DE LIBROS (Página " + (paginaActual + 1) + " de " + totalPaginas + ") ---");
            System.out.printf("%-3s | %-80s | %4s | %-27s | %-20s | %-15s | %-30s | %-30s | %-8s | %4s%n",
                    "ID", "TÍTULO", "NPAG", "AUTOR 1", "AUTOR 2", "GÉNERO", "CATEGORÍA", "EDITORIAL", "LCT", "ADQ");
            System.out.println("-".repeat(248));

            for (int i = inicio; i < fin; i++) {
                if (listaLibros.get(i).getAnyoLectura() > 0)
                    listaLibros.get(i).mostrarInfoLibro();
            }

            System.out.println("\n[S] Siguiente página | [A] Anterior página | [M] Menú Principal");
            System.out.print("Elige una opción: ");
            String opcion = sc.nextLine().toUpperCase();

            if (opcion.equals("S") && (paginaActual + 1) < totalPaginas) {
                paginaActual++;
            } else if (opcion.equals("A") && paginaActual > 0) {
                paginaActual--;
            } else if (opcion.equals("M")) {
                break;
            } else {
                System.out.println("Opción no válida o no hay más páginas.");
            }
        }
    }

    public static void mostrarAutoresPaginados(HashMap<Integer, Autor> autoresMap, Scanner sc) {
        List<Autor> listaAutores = new ArrayList<>(autoresMap.values());
        int totalLibros = listaAutores.size();
        int tamanoPagina = 10;
        int paginaActual = 0;
        int totalPaginas = (int) Math.ceil((double) totalLibros / tamanoPagina);

        while (true) {
            int inicio = paginaActual * tamanoPagina;
            int fin = Math.min(inicio + tamanoPagina, totalLibros);

            System.out.println("\n" + " ".repeat(10) + "--- LISTADO DE LIBROS (Página " + (paginaActual + 1) + " de " + totalPaginas + ") ---");
            System.out.printf("%-3s | %-30s | %-15s | %-12s%n",
                    "ID", "NOMBRE COMPLETO", "NACIONALIDAD", "F. NACIM.");
            System.out.println("-".repeat(68));

            for (int i = inicio; i < fin; i++) {
                listaAutores.get(i).mostrarInfoAutor();
            }

            System.out.println("\n[S] Siguiente página | [A] Anterior página | [M] Menú Principal");
            System.out.print("Elige una opción: ");
            String opcion = sc.nextLine().toUpperCase();

            if (opcion.equals("S") && (paginaActual + 1) < totalPaginas) {
                paginaActual++;
            } else if (opcion.equals("A") && paginaActual > 0) {
                paginaActual--;
            } else if (opcion.equals("M")) {
                break;
            } else {
                System.out.println("Opción no válida o no hay más páginas.");
            }
        }
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
        if (Utils.comprobarSalir(busqueda)) return;

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
            librosEncontrados.sort((l1, l2) -> l1.getTitulo().compareToIgnoreCase(l2.getTitulo()));

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

    public static void mostrarLibrosPaginadosPorAnyoLectura(HashMap<Integer, Libro> librosMap, Scanner sc) {
        int tamanoPagina = 50;
        int paginaActual = 0;

        int busqueda = pedirEntero(sc, "Introduce el año que deseas buscar: ");

        ArrayList<Libro> librosEncontrados = new ArrayList<>();
        for (Libro l : librosMap.values()) {
            if (l.getAnyoLectura() == busqueda) {
                librosEncontrados.add(l);
            }
        }

        int totalPaginas = (int) Math.ceil((double) librosEncontrados.size() / tamanoPagina);

        if (!librosEncontrados.isEmpty()) {
            while (true) {
                int inicio = paginaActual * tamanoPagina;
                int fin = Math.min(inicio + tamanoPagina, librosEncontrados.size());

                System.out.println("\n" + " ".repeat(100) + "--- LISTADO DE LIBROS (Página " + (paginaActual + 1) + " de " + totalPaginas + ") ---");
                System.out.printf("%-3s | %-80s | %4s | %-27s | %-20s | %-15s | %-30s | %-30s | %-8s | %4s%n",
                        "ID", "TÍTULO", "NPAG", "AUTOR 1", "AUTOR 2", "GÉNERO", "CATEGORÍA", "EDITORIAL", "LCT", "ADQ");
                System.out.println("-".repeat(248));

                for (int i = inicio; i < fin; i++) {
                    librosEncontrados.get(i).mostrarInfoLibro();
                }

                System.out.println("\n[S] Siguiente página | [A] Anterior página | [M] Menú Principal");
                System.out.print("Elige una opción: ");
                String opcion = sc.nextLine().toUpperCase();

                if (opcion.equals("S") && (paginaActual + 1) < totalPaginas) {
                    paginaActual++;
                } else if (opcion.equals("A") && paginaActual > 0) {
                    paginaActual--;
                } else if (opcion.equals("M")) {
                    break;
                } else {
                    System.out.println("Opción no válida o no hay más páginas.");
                }
            }
        } else {
            System.out.println("No se han obtenido resultados con el año: " + busqueda);
        }
    }

    public static void mostrarLibrosPaginadosPorAnyoComprado(HashMap<Integer, Libro> librosMap, Scanner sc) {
        int tamanoPagina = 50;
        int paginaActual = 0;

        int busqueda = pedirEntero(sc, "Introduce el año que deseas buscar: ");

        ArrayList<Libro> librosEncontrados = new ArrayList<>();
        for (Libro l : librosMap.values()) {
            if (l.getAnyoAdquisicion() == busqueda) {
                librosEncontrados.add(l);
            }
        }

        int totalPaginas = (int) Math.ceil((double) librosEncontrados.size() / tamanoPagina);

        if (!librosEncontrados.isEmpty()) {
            while (true) {
                int inicio = paginaActual * tamanoPagina;
                int fin = Math.min(inicio + tamanoPagina, librosEncontrados.size());

                System.out.println("\n" + " ".repeat(100) + "--- LISTADO DE LIBROS (Página " + (paginaActual + 1) + " de " + totalPaginas + ") ---");
                System.out.printf("%-3s | %-80s | %4s | %-27s | %-20s | %-15s | %-30s | %-30s | %-8s | %4s%n",
                        "ID", "TÍTULO", "NPAG", "AUTOR 1", "AUTOR 2", "GÉNERO", "CATEGORÍA", "EDITORIAL", "LCT", "ADQ");
                System.out.println("-".repeat(248));

                for (int i = inicio; i < fin; i++) {
                    librosEncontrados.get(i).mostrarInfoLibro();
                }

                System.out.println("\n[S] Siguiente página | [A] Anterior página | [M] Menú Principal");
                System.out.print("Elige una opción: ");
                String opcion = sc.nextLine().toUpperCase();

                if (opcion.equals("S") && (paginaActual + 1) < totalPaginas) {
                    paginaActual++;
                } else if (opcion.equals("A") && paginaActual > 0) {
                    paginaActual--;
                } else if (opcion.equals("M")) {
                    break;
                } else {
                    System.out.println("Opción no válida o no hay más páginas.");
                }
            }
        } else {
            System.out.println("No se han obtenido resultados con el año: " + busqueda);
        }
    }
}
