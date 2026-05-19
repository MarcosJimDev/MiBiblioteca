package Main.UI;

import Main.DAO.AutorDAO;
import Main.DAO.EditorialDAO;
import Main.DAO.LibroDAO;
import Main.DAO.LibroDeseadoDAO;
import Main.Java.Autor;
import Main.Java.Editorial;
import Main.Java.Libro;
import Main.Java.LibroDeseado;
import Main.Utils.Utils;

import java.util.HashMap;
import java.util.Scanner;

public class Eliminar {
    public static void mostrarMenuEliminar() {
        System.out.println("\t1. Eliminar libro.");
        System.out.println("\t2. Eliminar autor.");
        System.out.println("\t3. Eliminar editorial.");
        System.out.println("\t4. Eliminar libro deseado.");
        System.out.println("\t5. Volver al menú principal.");
    }

    public static void interactuarMenuEliminar(HashMap<Integer, Libro> libros, HashMap<Integer, Autor> autores, HashMap<Integer, Editorial> editoriales, HashMap<Integer, LibroDeseado> librosDeseados, Scanner sc) {
        int opcion = 0;
        do {
            System.out.println();
            mostrarMenuEliminar();
            opcion = BibliotecaUI.pedirEntero(sc, "Elige una opción: ");

            switch (opcion) {
                case 1 -> LibroDAO.eliminarLibro(libros, sc);
                case 2 -> AutorDAO.eliminarAutor(autores, sc);
                case 3 -> EditorialDAO.eliminarEditorial(editoriales, sc);
                case 4 -> LibroDeseadoDAO.eliminarLibroDeseado(librosDeseados, libros, autores, editoriales, sc);
                case 5 -> {
                    System.out.println("Volviendo al menú principal...");
                    Utils.tiempoMuerto();
                }
                default -> System.out.println("ERROR: respuesta desconocida o no registrada.");
            }
        } while (opcion != 5);
    }
}
