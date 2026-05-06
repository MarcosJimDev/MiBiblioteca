package Main.UI;

import Main.DAO.AutorDAO;
import Main.DAO.EditorialDAO;
import Main.DAO.LibroDAO;
import Main.Java.Autor;
import Main.Java.Editorial;
import Main.Java.Libro;
import Main.Utils.Utils;

import java.util.HashMap;
import java.util.Scanner;

public class Agregar {
    public static void mostrarMenuAgregar() {
        System.out.println("\t1. Agregar nuevo libro.");
        System.out.println("\t2. Agregar nuevo autor.");
        System.out.println("\t3. Agregar nueva editorial.");
        System.out.println("\t4. Agregar nueva firma editorial.");
        System.out.println("\t5. Volver al menú principal.");
    }

    public static void interactuarMenuAgregar(HashMap<Integer, Libro> libros, HashMap<Integer, Autor> autores, HashMap<Integer, Editorial> editoriales, Scanner sc) {
        int opcion = 0;
        do {
            System.out.println();
            mostrarMenuAgregar();
            opcion = BibliotecaUI.pedirEntero(sc, "Elige una opción: ");

            switch (opcion) {
                case 1 -> LibroDAO.agregarNuevoLibro(libros, autores, editoriales, sc);
                case 2 -> AutorDAO.agregarNuevoAutor(autores, sc);
                case 3 -> EditorialDAO.agregarNuevaEditorial(editoriales, sc);
                case 4 -> EditorialDAO.agregarNuevaFirma(editoriales, sc);
                case 5 -> {
                    System.out.println("Volviendo al menú principal...");
                    Utils.tiempoMuerto();
                }
                default -> System.out.println("ERROR: respuesta desconocida o no registrada.");
            }
        } while (opcion != 5);
    }
}
