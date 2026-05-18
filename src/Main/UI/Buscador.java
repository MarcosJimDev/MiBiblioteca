package Main.UI;

import Main.Java.*;
import Main.Utils.Utils;
import jdk.jshell.execution.Util;

import java.util.*;

public class Buscador {
    public static void mostrarMenuBuscador() {
        System.out.println("\t1. Buscar libro.");
        System.out.println("\t2. Buscar autor.");
        System.out.println("\t3. Buscar grupo editorial.");
        System.out.println("\t4. Buscar libro por grupo editorial.");
        System.out.println("\t5. Buscar libro por autor.");
        System.out.println("\t6. Buscar libro por año de lectura.");
        System.out.println("\t7. Buscar libro por año de compra.");
        System.out.println("\t8. Volver al menú principal.");
    }

    public static void interactuarMenuBuscador(HashMap<Integer, Libro> libros, HashMap<Integer, Autor> autores, HashMap<Integer, Editorial> editoriales, Scanner sc) {
        int opcion = 0;
        do {
            System.out.println();
            mostrarMenuBuscador();
            opcion = BibliotecaUI.pedirEntero(sc, "Elige una opción: ");

            switch (opcion) {
                case 1 -> BibliotecaUI.buscarLibro(libros, sc);
                case 2 -> BibliotecaUI.buscarAutor(autores, sc);
                case 3 -> BibliotecaUI.buscarEditorial(editoriales, sc);
                case 4 -> BibliotecaUI.filtrarLibrosPorEditorial(libros, sc);
                case 5 -> BibliotecaUI.filtrarLibrosPorAutor(libros, sc);
                case 6 -> BibliotecaUI.mostrarLibrosPaginadosPorAnyoLectura(libros, sc);
                case 7 -> BibliotecaUI.mostrarLibrosPaginadosPorAnyoComprado(libros, sc);
                case 8 -> {
                    System.out.println("Volviendo al menú principal...");
                    Utils.tiempoMuerto();
                }
                default -> System.out.println("ERROR: respuesta desconocida o no registrada.");
            }
        } while (opcion != 8);
    }
}
