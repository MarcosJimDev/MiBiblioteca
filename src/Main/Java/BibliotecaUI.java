package Main.Java;

import java.util.HashMap;

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
        System.out.println("\t0. Salir del programa.");
    }

    public static void mostrarBienvenida(HashMap<Integer, Libro> libros) {
        System.out.println("*** BIENVENIDO A MIBIBLIOTECA ***");
        System.out.println("Actualmente hay un total de " + libros.size() + " libros.");
    }

    public static Libro pedirDatosLibroNuevo() {
        return null;
    }
}
