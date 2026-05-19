package Main.JavaFX.views;

import Main.Java.*;
import Main.Utils.Utils;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.util.*;
import java.util.stream.Collectors;

public class SearchView {

    private final VBox view;
    private final HashMap<Integer, Libro> libros;
    private final HashMap<Integer, Autor> autores;
    private final HashMap<Integer, Editorial> editoriales;

    private TextField searchField;
    private ComboBox<String> typeCombo;
    private TableView<BookListView.LibroRow> resultTable;
    private Label resultCount;

    public SearchView(HashMap<Integer, Libro> libros, HashMap<Integer, Autor> autores,
                      HashMap<Integer, Editorial> editoriales) {
        this.libros = libros;
        this.autores = autores;
        this.editoriales = editoriales;

        view = new VBox(15);
        view.setPadding(new Insets(20));

        Text title = new Text("🔍 Buscador");
        title.getStyleClass().add("page-title");

        HBox searchBar = new HBox(10);
        searchBar.setAlignment(Pos.CENTER_LEFT);

        typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll(
                "Buscar libro por título",
                "Buscar autor por nombre",
                "Buscar editorial por nombre",
                "Filtrar libros por editorial",
                "Filtrar libros por autor",
                "Filtrar libros por año lectura",
                "Filtrar libros por año compra"
        );
        typeCombo.setValue("Buscar libro por título");
        typeCombo.getStyleClass().add("form-field");

        searchField = new TextField();
        searchField.getStyleClass().add("form-field");
        searchField.setPromptText("Escribe tu búsqueda...");
        searchField.setPrefWidth(300);
        searchField.setOnAction(e -> buscar());

        Button searchBtn = new Button("🔍 Buscar");
        searchBtn.getStyleClass().add("btn-primary");
        searchBtn.setOnAction(e -> buscar());

        searchBar.getChildren().addAll(typeCombo, searchField, searchBtn);

        resultCount = new Label("");
        resultCount.getStyleClass().add("page-subtitle");

        resultTable = new TableView<>();
        resultTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        TableColumn<BookListView.LibroRow, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(40);

        TableColumn<BookListView.LibroRow, String> titCol = new TableColumn<>("Título");
        titCol.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        titCol.setPrefWidth(200);

        TableColumn<BookListView.LibroRow, Integer> pagCol = new TableColumn<>("Págs");
        pagCol.setCellValueFactory(new PropertyValueFactory<>("numPaginas"));
        pagCol.setPrefWidth(45);

        TableColumn<BookListView.LibroRow, String> a1Col = new TableColumn<>("Autor");
        a1Col.setCellValueFactory(new PropertyValueFactory<>("autor1"));
        a1Col.setPrefWidth(130);

        TableColumn<BookListView.LibroRow, String> edCol = new TableColumn<>("Editorial");
        edCol.setCellValueFactory(new PropertyValueFactory<>("editorial"));
        edCol.setPrefWidth(120);

        TableColumn<BookListView.LibroRow, String> lecCol = new TableColumn<>("Lectura");
        lecCol.setCellValueFactory(new PropertyValueFactory<>("lectura"));
        lecCol.setPrefWidth(60);

        resultTable.getColumns().addAll(idCol, titCol, pagCol, a1Col, edCol, lecCol);

        TextArea detailArea = new TextArea();
        detailArea.setEditable(false);
        detailArea.setPrefHeight(150);
        detailArea.setPromptText("Haz doble clic en un resultado para ver detalles aquí.");
        detailArea.getStyleClass().add("card");

        resultTable.setRowFactory(tv -> {
            TableRow<BookListView.LibroRow> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && !row.isEmpty()) {
                    BookListView.LibroRow r = row.getItem();
                    detailArea.setText(
                            "ID: " + r.getId() + "\n" +
                            "Título: " + r.getTitulo() + "\n" +
                            "Páginas: " + r.getNumPaginas() + "\n" +
                            "Autor: " + r.getAutor1() + "\n" +
                            "Autor secundario: " + (r.getAutor2() != null ? r.getAutor2() : "N/A") + "\n" +
                            "Género: " + r.getGenero() + "\n" +
                            "Categoría: " + r.getCategoria() + "\n" +
                            "Editorial: " + r.getEditorial() + "\n" +
                            "Lectura: " + r.getLectura() + "\n" +
                            "Adquisición: " + r.getAnyoAdquisicion()
                    );
                }
            });
            return row;
        });

        VBox resultsBox = new VBox(10);
        resultsBox.getChildren().addAll(resultCount, resultTable, detailArea);
        VBox.setVgrow(resultTable, Priority.ALWAYS);

        view.getChildren().addAll(title, searchBar, resultsBox);
    }

    private void buscar() {
        String query = searchField.getText().trim();
        String tipo = typeCombo.getValue();

        if (tipo.equals("Buscar libro por título")) {
            buscarLibrosPorTitulo(query);
        } else if (tipo.equals("Buscar autor por nombre")) {
            buscarAutor(query);
        } else if (tipo.equals("Buscar editorial por nombre")) {
            buscarEditorial(query);
        } else if (tipo.equals("Filtrar libros por editorial")) {
            filtrarPorEditorial(query);
        } else if (tipo.equals("Filtrar libros por autor")) {
            filtrarPorAutor(query);
        } else if (tipo.equals("Filtrar libros por año lectura")) {
            filtrarPorAnyoLectura(query);
        } else if (tipo.equals("Filtrar libros por año compra")) {
            filtrarPorAnyoCompra(query);
        }
    }

    private void buscarLibrosPorTitulo(String query) {
        if (query.isEmpty()) { resultCount.setText("Introduce un título."); resultTable.setItems(FXCollections.observableArrayList()); return; }

        String norm = Utils.normalizar(query);
        List<BookListView.LibroRow> results = libros.values().stream()
                .filter(l -> Utils.normalizar(l.getTitulo()).contains(norm))
                .map(BookListView.LibroRow::new)
                .sorted(Comparator.comparing(BookListView.LibroRow::getTitulo, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());

        resultTable.setItems(FXCollections.observableArrayList(results));
        resultCount.setText("Coincidencias: " + results.size());
    }

    private void buscarAutor(String query) {
        if (query.isEmpty()) { resultCount.setText("Introduce un nombre."); resultTable.setItems(FXCollections.observableArrayList()); return; }

        String norm = Utils.normalizar(query);
        List<Autor> found = autores.values().stream()
                .filter(a -> Utils.normalizar(a.getNombre()).contains(norm))
                .collect(Collectors.toList());

        if (found.isEmpty()) {
            resultCount.setText("No se encontraron autores.");
            resultTable.setItems(FXCollections.observableArrayList());
            return;
        }

        StringBuilder sb = new StringBuilder("Autores encontrados: " + found.size() + "\n");
        found.forEach(a -> sb.append("• ").append(a.getNombre())
                .append(" (").append(a.getNacionalidad() != null ? a.getNacionalidad() : "N/A").append(")\n"));

        resultCount.setText(sb.toString());
        resultTable.setItems(FXCollections.observableArrayList());
    }

    private void buscarEditorial(String query) {
        if (query.isEmpty()) { resultCount.setText("Introduce un nombre."); resultTable.setItems(FXCollections.observableArrayList()); return; }

        String norm = Utils.normalizar(query);
        List<Editorial> found = editoriales.values().stream()
                .filter(e -> Utils.normalizar(e.getDisplayName()).contains(norm))
                .collect(Collectors.toList());

        if (found.isEmpty()) {
            resultCount.setText("No se encontraron editoriales.");
            resultTable.setItems(FXCollections.observableArrayList());
            return;
        }

        StringBuilder sb = new StringBuilder("Editoriales encontradas: " + found.size() + "\n");
        found.forEach(e -> sb.append("• ").append(e.getDisplayName()).append("\n"));

        resultCount.setText(sb.toString());
        resultTable.setItems(FXCollections.observableArrayList());
    }

    private void filtrarPorEditorial(String query) {
        if (query.isEmpty()) { resultCount.setText("Introduce el nombre de la editorial."); resultTable.setItems(FXCollections.observableArrayList()); return; }

        String norm = Utils.normalizar(query);
        List<BookListView.LibroRow> results = libros.values().stream()
                .filter(l -> Utils.normalizar(l.getEditorial().getDisplayName()).contains(norm))
                .map(BookListView.LibroRow::new)
                .collect(Collectors.toList());

        resultTable.setItems(FXCollections.observableArrayList(results));
        resultCount.setText("Libros de '" + query + "': " + results.size());
    }

    private void filtrarPorAutor(String query) {
        if (query.isEmpty()) { resultCount.setText("Introduce el nombre del autor."); resultTable.setItems(FXCollections.observableArrayList()); return; }

        String norm = Utils.normalizar(query);
        List<BookListView.LibroRow> results = libros.values().stream()
                .filter(l -> Utils.normalizar(l.getAutor1().getNombre()).contains(norm) ||
                        (l.getAutor2() != null && Utils.normalizar(l.getAutor2().getNombre()).contains(norm)))
                .map(BookListView.LibroRow::new)
                .collect(Collectors.toList());

        resultTable.setItems(FXCollections.observableArrayList(results));
        resultCount.setText("Libros de '" + query + "': " + results.size());
    }

    private void filtrarPorAnyoLectura(String query) {
        try {
            int anyo = Integer.parseInt(query);
            List<BookListView.LibroRow> results = libros.values().stream()
                    .filter(l -> l.getAnyoLectura() == anyo)
                    .map(BookListView.LibroRow::new)
                    .collect(Collectors.toList());
            resultTable.setItems(FXCollections.observableArrayList(results));
            resultCount.setText("Libros leídos en " + anyo + ": " + results.size());
        } catch (NumberFormatException e) {
            resultCount.setText("Introduce un año válido (ej: 2024).");
            resultTable.setItems(FXCollections.observableArrayList());
        }
    }

    private void filtrarPorAnyoCompra(String query) {
        try {
            int anyo = Integer.parseInt(query);
            List<BookListView.LibroRow> results = libros.values().stream()
                    .filter(l -> l.getAnyoAdquisicion() == anyo)
                    .map(BookListView.LibroRow::new)
                    .collect(Collectors.toList());
            resultTable.setItems(FXCollections.observableArrayList(results));
            resultCount.setText("Libros comprados en " + anyo + ": " + results.size());
        } catch (NumberFormatException e) {
            resultCount.setText("Introduce un año válido (ej: 2024).");
            resultTable.setItems(FXCollections.observableArrayList());
        }
    }

    public Node getView() { return view; }
}
