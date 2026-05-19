package Main.JavaFX.controllers;

import Main.Java.*;
import Main.JavaFX.views.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.HashMap;

public class MainController {

    private final HashMap<Integer, Autor> autores;
    private final HashMap<Integer, Editorial> editoriales;
    private final HashMap<Integer, Libro> libros;
    private final HashMap<Integer, LibroDeseado> librosDeseados;

    private final BorderPane root;
    private final StackPane contentArea;
    private final ToggleGroup navGroup = new ToggleGroup();

    public MainController(HashMap<Integer, Autor> autores, HashMap<Integer, Editorial> editoriales,
                          HashMap<Integer, Libro> libros, HashMap<Integer, LibroDeseado> librosDeseados) {
        this.autores = autores;
        this.editoriales = editoriales;
        this.libros = libros;
        this.librosDeseados = librosDeseados;

        root = new BorderPane();
        contentArea = new StackPane();
        contentArea.getStyleClass().add("content-area");

        root.setLeft(createSidebar());
        root.setCenter(contentArea);

        showView(new DashboardView(autores, editoriales, libros, librosDeseados).getView());
    }

    private Node createSidebar() {
        VBox sidebar = new VBox();
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(220);
        sidebar.setMinWidth(220);
        sidebar.setMaxWidth(220);

        VBox header = new VBox();
        header.getStyleClass().add("sidebar-header");
        header.setPrefHeight(40);

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        VBox navItems = new VBox();
        navItems.setSpacing(1);

        addNavSection(navItems, "PRINCIPAL");
        addNavButton(navItems, "🏠  Inicio", () -> showView(new DashboardView(autores, editoriales, libros, librosDeseados).getView()));

        addNavSection(navItems, "LIBROS");
        addNavButton(navItems, "📖  Ver libros", () -> showView(new BookListView(libros, autores, editoriales).getView()));
        addNavButton(navItems, "➕  Añadir libro", () -> showView(new BookFormView(libros, autores, editoriales, this::refreshAll).getView()));
        addNavButton(navItems, "❌  Eliminar libro", () -> showView(new DeleteBookView(libros).getView()));
        addNavButton(navItems, "📝  Año lectura", () -> showView(new UpdateReadingYearView(libros).getView()));

        addNavSection(navItems, "AUTORES");
        addNavButton(navItems, "👤  Ver autores", () -> showView(new AuthorListView(autores).getView()));
        addNavButton(navItems, "➕  Añadir autor", () -> showView(new AuthorFormView(autores, this::refreshAll).getView()));
        addNavButton(navItems, "❌  Eliminar autor", () -> showView(new DeleteAuthorView(autores).getView()));

        addNavSection(navItems, "EDITORIALES");
        addNavButton(navItems, "🏢  Ver editoriales", () -> showView(new EditorialListView(editoriales).getView()));
        addNavButton(navItems, "➕  Añadir editorial", () -> showView(new EditorialFormView(editoriales, this::refreshAll).getView()));
        addNavButton(navItems, "❌  Eliminar editorial", () -> showView(new DeleteEditorialView(editoriales).getView()));

        addNavSection(navItems, "DESEADOS");
        addNavButton(navItems, "⭐  Lista deseos", () -> showView(new WishlistView(librosDeseados, libros, autores, editoriales, this::refreshAll).getView()));

        addNavSection(navItems, "CONSULTAS");
        addNavButton(navItems, "🔍  Buscar", () -> showView(new SearchView(libros, autores, editoriales).getView()));
        addNavButton(navItems, "📊  Estadísticas", () -> showView(new StatisticsView(libros, autores, editoriales).getView()));

        scroll.setContent(navItems);
        sidebar.getChildren().addAll(header, scroll);
        return sidebar;
    }

    private void addNavSection(VBox parent, String text) {
        Label label = new Label(text);
        label.getStyleClass().add("nav-section");
        label.setPadding(new Insets(15, 15, 3, 20));
        parent.getChildren().add(label);
    }

    private void addNavButton(VBox parent, String text, Runnable action) {
        ToggleButton btn = new ToggleButton(text);
        btn.getStyleClass().add("nav-btn");
        btn.setToggleGroup(navGroup);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setOnAction(e -> {
            if (btn.isSelected()) action.run();
        });
        parent.getChildren().add(btn);
    }

    private void showView(Node view) {
        contentArea.getChildren().clear();
        ScrollPane scroll = new ScrollPane(view);
        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);
        contentArea.getChildren().add(scroll);
    }

    public BorderPane getRoot() {
        return root;
    }

    private void refreshAll() {
        HashMap<Integer, Autor> nuevosAutores = Main.DAO.AutorDAO.cargarAutores();
        HashMap<Integer, Editorial> nuevasEditoriales = Main.DAO.EditorialDAO.cargarEditoriales();
        HashMap<Integer, Libro> nuevosLibros = Main.DAO.LibroDAO.cargarLibros(nuevosAutores, nuevasEditoriales);
        HashMap<Integer, LibroDeseado> nuevosDeseados = Main.DAO.LibroDeseadoDAO.cargarLibrosDeseados(nuevosAutores, nuevasEditoriales);

        autores.clear(); autores.putAll(nuevosAutores);
        editoriales.clear(); editoriales.putAll(nuevasEditoriales);
        libros.clear(); libros.putAll(nuevosLibros);
        librosDeseados.clear(); librosDeseados.putAll(nuevosDeseados);
    }


}
