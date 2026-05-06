package Main;

import Main.DAO.*;
import Main.Java.*;
import Main.UI.*;
import Main.Utils.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        HashMap<Integer, Autor> autores = AutorDAO.cargarAutores();
        HashMap<Integer, Editorial> editoriales = EditorialDAO.cargarEditoriales();
        HashMap<Integer, Libro> libros = LibroDAO.cargarLibros(autores, editoriales);

        Scanner sc = Utils.declararScanner();

        BibliotecaUI.mostrarBienvenida(libros);

        BibliotecaUI.interaccionMenu(libros, autores, editoriales, sc);

        sc.close();
    }
}
