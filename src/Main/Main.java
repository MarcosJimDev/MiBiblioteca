package Main;

import Main.Java.*;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        HashMap<Integer, Autor> autores = DAO.cargarAutores();
        HashMap<Integer, Editorial> editoriales = DAO.cargarEditoriales();
        HashMap<Integer, Libro> libros = DAO.cargarLibros(autores, editoriales);

        for (Autor a : autores.values()) {
            a.mostrarInfoAutor();
        }
        System.out.println();
        for (Editorial e : editoriales.values()) {
            e.mostrarInfoEditorial();
        }
        System.out.println();
        for (Libro l : libros.values()) {
            l.mostrarInfoLibro();
        }
    }

    public static void mostrarMenu() {
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
}
