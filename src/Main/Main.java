package Main;

import Main.Config.AppInfo;
import Main.Java.*;
import Main.Utils.Utils;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        HashMap<Integer, Autor> autores = AutorDAO.cargarAutores();
        HashMap<Integer, Editorial> editoriales = EditorialDAO.cargarEditoriales();
        HashMap<Integer, Libro> libros = LibroDAO.cargarLibros(autores, editoriales);

        Scanner sc = Utils.declararScaner();

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
                            System.out.println("--- LISTADO DE LIBROS ---");
                            for (Libro l : libros.values()) {
                                l.mostrarInfoLibro();
                            }
                            break;
                        case 2:
                            System.out.println("--- LISTADO DE AUTORES ---");
                            for (Autor a : autores.values()) {
                                a.mostrarInfoAutor();
                            }
                            break;
                        case 3:
                            System.out.println("--- LISTADO DE EDITORIALES ---");
                            for (Editorial e : editoriales.values()) {
                                e.mostrarInfoEditorial();
                            }
                            break;
                        case 4:
                            LibroDAO.agregarNuevoLibro(libros, autores, editoriales);
                            break;
                        case 5:
                            AutorDAO.agregarNuevoAutor(autores);
                            break;
                        case 6:
                            EditorialDAO.agregarNuevaEditorial(editoriales);
                            break;
                        case 7:
                            //TODO
                            System.out.println("En desarrollo.");
                            break;
                        case 8:
                            //TODO
                            System.out.println("En desarrollo.");
                            break;
                        case 9:
                            //TODO
                            System.out.println("En desarrollo.");
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
