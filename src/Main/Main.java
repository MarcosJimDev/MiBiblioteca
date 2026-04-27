package Main;

import Main.Config.AppInfo;
import Main.DAO.AutorDAO;
import Main.DAO.EditorialDAO;
import Main.DAO.LibroDAO;
import Main.Java.*;
import Main.UI.BibliotecaUI;
import Main.Utils.Utils;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        HashMap<Integer, Autor> autores = AutorDAO.cargarAutores();
        HashMap<Integer, Editorial> editoriales = EditorialDAO.cargarEditoriales();
        HashMap<Integer, Libro> libros = LibroDAO.cargarLibros(autores, editoriales);

        Scanner sc = Utils.declararScanner();

        BibliotecaUI.mostrarBienvenida(libros);

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
            } else if (opcion.length() == 1) {
                try {
                    opcionNum = Integer.parseInt(opcion);

                    switch (opcionNum) {
                        case 1:
                            System.out.println("\n" + " ".repeat(100) + "--- LISTADO DE LIBROS ---");
                            // Formato idéntico al de Libro.java
                            System.out.printf("%-3s | %-80s | %4s | %-27s | %-20s | %-15s | %-30s | %-30s | %4s | %4s | %-3s%n",
                                    "ID", "TÍTULO", "NPAG", "AUTOR 1", "AUTOR 2", "GÉNERO", "CATEGORÍA", "EDITORIAL", "LCT", "ADQ", "L?");
                            System.out.println("-".repeat(239)); // Línea divisoria larga

                            for (Libro l : libros.values()) {
                                l.mostrarInfoLibro();
                            }
                            break;
                        case 2:
                            System.out.println("\n--- LISTADO DE AUTORES ---");
                            System.out.printf("%-3s | %-30s | %-15s | %-12s%n",
                                    "ID", "NOMBRE COMPLETO", "NACIONALIDAD", "F. NACIM.");
                            System.out.println("-".repeat(68));

                            for (Autor a : autores.values()) {
                                a.mostrarInfoAutor();
                            }
                            break;
                        case 3:
                            System.out.println("\n--- LISTADO DE EDITORIALES ---");
                            System.out.printf("%-3s | %-28s | %-20s%n",
                                    "ID", "GRUPO EDITORIAL", "FIRMA EDITORIAL");
                            System.out.println("-".repeat(53));

                            for (Editorial e : editoriales.values()) {
                                e.mostrarInfoEditorial();
                            }
                            break;
                        case 4:
                            LibroDAO.agregarNuevoLibro(libros, autores, editoriales, sc);
                            break;
                        case 5:
                            AutorDAO.agregarNuevoAutor(autores, sc);
                            break;
                        case 6:
                            EditorialDAO.agregarNuevaEditorial(editoriales, sc);
                            break;
                        case 7:
                            LibroDAO.eliminarLibro(libros, sc);
                            break;
                        case 8:
                            AutorDAO.eliminarAutor(autores, sc);
                            break;
                        case 9:
                            EditorialDAO.eliminarEditorial(editoriales, sc);
                            break;
                        case 0:
                            System.out.println("Saliendo del programa...");
                            break;
                        default:
                            System.out.println("ERROR: opción no registrada.");
                    }
                } catch (NumberFormatException e) {
                    System.err.println("ERROR: debes introducir un número para elegir una opción. " + e);
                }
            } else {
                System.out.println("ERROR: comando o elemento no registrado.");
            }
        } while(opcionNum != 0);
        sc.close();
    }
}
