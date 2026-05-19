package Main.JavaFX.views;

import Main.Java.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.util.*;
import java.util.stream.Collectors;

public class BookListView {

    private final VBox view;
    private final HashMap<Integer, Libro> libros;
    private final HashMap<Integer, Autor> autores;
    private final HashMap<Integer, Editorial> editoriales;

    private TableView<LibroRow> table;
    private ObservableList<LibroRow> allRowsOriginal;
    private ObservableList<LibroRow> filteredRows;
    private int currentPage = 0;
    private static final int PAGE_SIZE = 50;
    private Label pageLabel;
    private ComboBox<String> filterCombo;
    private ComboBox<String> authorFilterCombo;
    private ComboBox<String> editorialFilterCombo;

    public BookListView(HashMap<Integer, Libro> libros, HashMap<Integer, Autor> autores,
                        HashMap<Integer, Editorial> editoriales) {
        this.libros = libros;
        this.autores = autores;
        this.editoriales = editoriales;

        view = new VBox(10);
        view.setPadding(new Insets(20));

        Text title = new Text("📖 Listado de libros");
        title.getStyleClass().add("page-title");
        Text subtitle = new Text("Total: " + libros.size() + " libros.");
        subtitle.getStyleClass().add("page-subtitle");

        HBox filterBar = createFilterBar();

        table = createTable();
        allRowsOriginal = FXCollections.observableArrayList();
        loadAllRows();
        filteredRows = FXCollections.observableArrayList(allRowsOriginal);
        HBox paginationBar = createPaginationBar();
        applyFilters();
        updatePage();

        view.getChildren().addAll(title, subtitle, filterBar, table, paginationBar);
    }

    private HBox createFilterBar() {
        HBox bar = new HBox(10);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(5, 0, 5, 0));

        filterCombo = new ComboBox<>();
        filterCombo.getItems().addAll("Todos", "Leídos", "No leídos");
        filterCombo.setValue("Todos");
        filterCombo.setOnAction(e -> { currentPage = 0; applyFilters(); updatePage(); });

        authorFilterCombo = new ComboBox<>();
        authorFilterCombo.getItems().add("Todos");
        authorFilterCombo.getItems().addAll(
                autores.values().stream().map(Autor::getNombre).sorted().collect(Collectors.toList())
        );
        authorFilterCombo.setValue("Todos");
        authorFilterCombo.setOnAction(e -> { currentPage = 0; applyFilters(); updatePage(); });

        editorialFilterCombo = new ComboBox<>();
        editorialFilterCombo.getItems().add("Todos");
        editorialFilterCombo.getItems().addAll(
                editoriales.values().stream().map(Editorial::getDisplayName).sorted().distinct().collect(Collectors.toList())
        );
        editorialFilterCombo.setValue("Todos");
        editorialFilterCombo.setOnAction(e -> { currentPage = 0; applyFilters(); updatePage(); });

        Button resetBtn = new Button("Limpiar filtros");
        resetBtn.getStyleClass().add("btn-secondary");
        resetBtn.setOnAction(e -> {
            filterCombo.setValue("Todos");
            authorFilterCombo.setValue("Todos");
            editorialFilterCombo.setValue("Todos");
            currentPage = 0;
            applyFilters();
            updatePage();
        });

        bar.getChildren().addAll(
                new Label("Leído:"), filterCombo,
                new Label("Autor:"), authorFilterCombo,
                new Label("Editorial:"), editorialFilterCombo,
                resetBtn
        );
        return bar;
    }

    private TableView<LibroRow> createTable() {
        TableView<LibroRow> t = new TableView<>();
        t.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        TableColumn<LibroRow, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(40);

        TableColumn<LibroRow, String> titCol = new TableColumn<>("Título");
        titCol.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        titCol.setPrefWidth(200);

        TableColumn<LibroRow, Integer> pagCol = new TableColumn<>("Págs");
        pagCol.setCellValueFactory(new PropertyValueFactory<>("numPaginas"));
        pagCol.setPrefWidth(50);

        TableColumn<LibroRow, String> a1Col = new TableColumn<>("Autor Principal");
        a1Col.setCellValueFactory(new PropertyValueFactory<>("autor1"));
        a1Col.setPrefWidth(130);

        TableColumn<LibroRow, String> a2Col = new TableColumn<>("Autor Sec.");
        a2Col.setCellValueFactory(new PropertyValueFactory<>("autor2"));
        a2Col.setPrefWidth(100);

        TableColumn<LibroRow, String> genCol = new TableColumn<>("Género");
        genCol.setCellValueFactory(new PropertyValueFactory<>("genero"));
        genCol.setPrefWidth(80);

        TableColumn<LibroRow, String> catCol = new TableColumn<>("Categoría");
        catCol.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        catCol.setPrefWidth(100);

        TableColumn<LibroRow, String> edCol = new TableColumn<>("Editorial");
        edCol.setCellValueFactory(new PropertyValueFactory<>("editorial"));
        edCol.setPrefWidth(120);

        TableColumn<LibroRow, String> lecCol = new TableColumn<>("Lectura");
        lecCol.setCellValueFactory(new PropertyValueFactory<>("lectura"));
        lecCol.setPrefWidth(60);

        TableColumn<LibroRow, Integer> adqCol = new TableColumn<>("Adq.");
        adqCol.setCellValueFactory(new PropertyValueFactory<>("anyoAdquisicion"));
        adqCol.setPrefWidth(50);

        t.getColumns().addAll(idCol, titCol, pagCol, a1Col, a2Col, genCol, catCol, edCol, lecCol, adqCol);
        t.setRowFactory(tv -> {
            TableRow<LibroRow> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && !row.isEmpty()) {
                    showBookDetail(row.getItem());
                }
            });
            return row;
        });

        return t;
    }

    private void showBookDetail(LibroRow row) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Detalle del libro");
        alert.setHeaderText(row.getTitulo());

        String autor2Str = row.getAutor2() == null || row.getAutor2().equals("N/A") ? "N/A" : row.getAutor2();
        String lectura = row.getLectura().equals("No leído") ? row.getLectura() : "Leído en " + row.getLectura();

        alert.setContentText(
                "ID: " + row.getId() + "\n" +
                "Título: " + row.getTitulo() + "\n" +
                "Páginas: " + row.getNumPaginas() + "\n" +
                "Autor principal: " + row.getAutor1() + "\n" +
                "Autor secundario: " + autor2Str + "\n" +
                "Género: " + row.getGenero() + "\n" +
                "Categoría: " + row.getCategoria() + "\n" +
                "Editorial: " + row.getEditorial() + "\n" +
                "Año lectura: " + lectura + "\n" +
                "Año adquisición: " + row.getAnyoAdquisicion()
        );
        alert.showAndWait();
    }

    private HBox createPaginationBar() {
        HBox bar = new HBox(10);
        bar.setAlignment(Pos.CENTER);
        bar.getStyleClass().add("pagination-bar");

        Button prevBtn = new Button("◀ Anterior");
        prevBtn.getStyleClass().add("btn-secondary");
        prevBtn.setOnAction(e -> { if (currentPage > 0) { currentPage--; updatePage(); } });

        pageLabel = new Label();

        Button nextBtn = new Button("Siguiente ▶");
        nextBtn.getStyleClass().add("btn-secondary");
        nextBtn.setOnAction(e -> {
            int totalPages = (int) Math.ceil((double) filteredRows.size() / PAGE_SIZE);
            if (currentPage + 1 < totalPages) { currentPage++; updatePage(); }
        });

        bar.getChildren().addAll(prevBtn, pageLabel, nextBtn);
        return bar;
    }

    private void loadAllRows() {
        allRowsOriginal.clear();
        for (Libro l : libros.values()) {
            allRowsOriginal.add(new LibroRow(l));
        }
    }

    private void applyFilters() {
        List<LibroRow> filtered = new ArrayList<>(allRowsOriginal);

        String lecturaFilter = filterCombo.getValue();
        if ("Leídos".equals(lecturaFilter)) {
            filtered.removeIf(r -> r.getLectura().equals("No leído"));
        } else if ("No leídos".equals(lecturaFilter)) {
            filtered.removeIf(r -> !r.getLectura().equals("No leído"));
        }

        String authorFilter = authorFilterCombo.getValue();
        if (authorFilter != null && !"Todos".equals(authorFilter)) {
            filtered.removeIf(r -> !r.getAutor1().equals(authorFilter) &&
                    (r.getAutor2() == null || !r.getAutor2().equals(authorFilter)));
        }

        String editorialFilter = editorialFilterCombo.getValue();
        if (editorialFilter != null && !"Todos".equals(editorialFilter)) {
            filtered.removeIf(r -> !r.getEditorial().equals(editorialFilter));
        }

        filteredRows = FXCollections.observableArrayList(filtered);
    }

    private void updatePage() {
        int totalPages = Math.max(1, (int) Math.ceil((double) filteredRows.size() / PAGE_SIZE));
        if (currentPage >= totalPages) currentPage = totalPages - 1;

        int from = currentPage * PAGE_SIZE;
        int to = Math.min(from + PAGE_SIZE, filteredRows.size());

        table.setItems(FXCollections.observableArrayList(filteredRows.subList(from, to)));
        pageLabel.setText("Página " + (currentPage + 1) + " de " + totalPages +
                " (" + filteredRows.size() + " libros)");
    }

    public Node getView() { return view; }

    public static class LibroRow {
        private final int id;
        private final String titulo;
        private final int numPaginas;
        private final String autor1;
        private final String autor2;
        private final String genero;
        private final String categoria;
        private final String editorial;
        private final String lectura;
        private final int anyoAdquisicion;

        public LibroRow(Libro l) {
            this.id = l.getId();
            this.titulo = l.getTitulo();
            this.numPaginas = l.getNumPaginas();
            this.autor1 = l.getAutor1() != null ? l.getAutor1().getNombre() : "N/A";
            this.autor2 = l.getAutor2() != null ? l.getAutor2().getNombre() : null;
            this.genero = l.getGenero() != null ? l.getGenero() : "N/A";
            this.categoria = l.getCategoria() != null ? l.getCategoria() : "N/A";
            this.editorial = l.getEditorial() != null ? l.getEditorial().getDisplayName() : "N/A";
            this.lectura = l.getAnyoLectura() > 0 ? String.valueOf(l.getAnyoLectura()) : "No leído";
            this.anyoAdquisicion = l.getAnyoAdquisicion();
        }

        public int getId() { return id; }
        public String getTitulo() { return titulo; }
        public int getNumPaginas() { return numPaginas; }
        public String getAutor1() { return autor1; }
        public String getAutor2() { return autor2; }
        public String getGenero() { return genero; }
        public String getCategoria() { return categoria; }
        public String getEditorial() { return editorial; }
        public String getLectura() { return lectura; }
        public int getAnyoAdquisicion() { return anyoAdquisicion; }
    }
}
