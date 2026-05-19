package Main.JavaFX;

import Main.DAO.*;
import Main.Java.*;
import Main.JavaFX.controllers.MainController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.HashMap;

public class MainFX extends Application {

    private static HashMap<Integer, Autor> autores;
    private static HashMap<Integer, Editorial> editoriales;
    private static HashMap<Integer, Libro> libros;
    private static HashMap<Integer, LibroDeseado> librosDeseados;

    @Override
    public void start(Stage stage) {
        autores = AutorDAO.cargarAutores();
        editoriales = EditorialDAO.cargarEditoriales();
        libros = LibroDAO.cargarLibros(autores, editoriales);
        librosDeseados = LibroDeseadoDAO.cargarLibrosDeseados(autores, editoriales);

        MainController controller = new MainController(autores, editoriales, libros, librosDeseados);

        Scene scene = new Scene(controller.getRoot(), 1280, 800);
        scene.getStylesheets().add(getClass().getResource("/Main/JavaFX/styles.css").toExternalForm());

        stage.setTitle("MiBiblioteca");
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.setScene(scene);
        stage.show();
    }

    public static HashMap<Integer, Autor> getAutores() { return autores; }
    public static HashMap<Integer, Editorial> getEditoriales() { return editoriales; }
    public static HashMap<Integer, Libro> getLibros() { return libros; }
    public static HashMap<Integer, LibroDeseado> getLibrosDeseados() { return librosDeseados; }

    public static void main(String[] args) {
        launch(args);
    }
}
