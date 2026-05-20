package Main.JavaFX.views;

import Main.Java.*;
import Main.Utils.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.awt.Desktop;
import java.net.URI;
import java.sql.*;
import java.util.HashMap;
import java.util.stream.Collectors;

public class WishlistView {

    private final VBox view;
    private final HashMap<Integer, LibroDeseado> librosDeseados;
    private final HashMap<Integer, Libro> libros;
    private final HashMap<Integer, Autor> autores;
    private final HashMap<Integer, Editorial> editoriales;
    private final Runnable onRefresh;

    private TableView<WishlistRow> table;
    private ObservableList<WishlistRow> items;
    private Label mensaje;

    private TextField tituloField, paginasField, autor2Field, generoField, categoriaField, enlaceField;
    private ComboBox<String> autor1Combo, editorialCombo;

    public WishlistView(HashMap<Integer, LibroDeseado> librosDeseados, HashMap<Integer, Libro> libros,
                        HashMap<Integer, Autor> autores, HashMap<Integer, Editorial> editoriales,
                        Runnable onRefresh) {
        this.librosDeseados = librosDeseados;
        this.libros = libros;
        this.autores = autores;
        this.editoriales = editoriales;
        this.onRefresh = onRefresh;

        view = new VBox(15);
        view.setPadding(new Insets(20));

        Text title = new Text("⭐ Lista de Deseados");
        title.getStyleClass().add("page-title");

        // Tabla de libros deseados con enlaces clicables
        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        TableColumn<WishlistRow, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(40);

        TableColumn<WishlistRow, String> titCol = new TableColumn<>("Título");
        titCol.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        titCol.setPrefWidth(150);

        TableColumn<WishlistRow, Integer> pagCol = new TableColumn<>("Págs");
        pagCol.setCellValueFactory(new PropertyValueFactory<>("numPaginas"));
        pagCol.setPrefWidth(45);

        TableColumn<WishlistRow, String> a1Col = new TableColumn<>("Autor");
        a1Col.setCellValueFactory(new PropertyValueFactory<>("autor1"));
        a1Col.setPrefWidth(120);

        TableColumn<WishlistRow, String> genCol = new TableColumn<>("Género");
        genCol.setCellValueFactory(new PropertyValueFactory<>("genero"));
        genCol.setPrefWidth(80);

        TableColumn<WishlistRow, String> edCol = new TableColumn<>("Editorial");
        edCol.setCellValueFactory(new PropertyValueFactory<>("editorial"));
        edCol.setPrefWidth(120);

        TableColumn<WishlistRow, String> enlCol = new TableColumn<>("Enlace");
        enlCol.setCellValueFactory(new PropertyValueFactory<>("enlaceCompra"));
        enlCol.setPrefWidth(180);
        enlCol.setCellFactory(col -> new TableCell<WishlistRow, String>() {
            private final Hyperlink link = new Hyperlink();
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.trim().isEmpty()) {
                    setGraphic(null);
                    setText("N/A");
                } else {
                    String url = item.startsWith("http") ? item : "https://" + item;
                    link.setText("🔗 Abrir enlace");
                    link.setOnAction(e -> {
                        try {
                            if (Desktop.isDesktopSupported()) {
                                Desktop.getDesktop().browse(new URI(url));
                            } else {
                                Alert alert = new Alert(Alert.AlertType.WARNING,
                                        "No se puede abrir enlaces en este entorno.");
                                alert.showAndWait();
                            }
                        } catch (Exception ex) {
                            Alert alert = new Alert(Alert.AlertType.ERROR,
                                    "No se pudo abrir el enlace:\n" + ex.getMessage());
                            alert.showAndWait();
                        }
                    });
                    link.setTooltip(new Tooltip(url));
                    setGraphic(link);
                    setText(null);
                }
            }
        });

        table.getColumns().addAll(idCol, titCol, pagCol, a1Col, genCol, edCol, enlCol);

        items = FXCollections.observableArrayList();
        refreshTable();

        // Botones de acción sobre la tabla
        HBox actionBar = new HBox(10);
        actionBar.setAlignment(Pos.CENTER_LEFT);

        Button eliminarBtn = new Button("🗑 Eliminar seleccionado");
        eliminarBtn.getStyleClass().add("btn-danger");
        eliminarBtn.setOnAction(e -> eliminarSeleccionado());

        Button moverBtn = new Button("📦 Mover a adquiridos");
        moverBtn.getStyleClass().add("btn-primary");
        moverBtn.setOnAction(e -> moverAAdquiridos());

        Button refrescarBtn = new Button("🔄 Refrescar");
        refrescarBtn.getStyleClass().add("btn-secondary");
        refrescarBtn.setOnAction(e -> refreshTable());

        actionBar.getChildren().addAll(eliminarBtn, moverBtn, refrescarBtn);

        mensaje = new Label();
        mensaje.setVisible(false);
        mensaje.setWrapText(true);

        // Sección de añadir nuevo libro deseado
        Separator sep = new Separator();
        Text addTitle = new Text("➕ Añadir nuevo libro a la lista de deseados");
        addTitle.getStyleClass().add("page-subtitle");
        addTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        GridPane formGrid = createAddForm();

        VBox.setVgrow(table, Priority.ALWAYS);
        view.getChildren().addAll(title, table, actionBar, mensaje, sep, addTitle, formGrid);
    }

    private GridPane createAddForm() {
        GridPane grid = new GridPane();
        grid.getStyleClass().add("card");
        grid.setHgap(15);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        tituloField = new TextField();
        tituloField.getStyleClass().add("form-field");
        tituloField.setPromptText("Título del libro");

        paginasField = new TextField();
        paginasField.getStyleClass().add("form-field");
        paginasField.setPromptText("Número de páginas");

        autor1Combo = new ComboBox<>();
        autor1Combo.getStyleClass().add("form-field");
        autor1Combo.setPromptText("Selecciona autor principal");
        autor1Combo.setEditable(true);
        autor1Combo.getItems().addAll(
                autores.values().stream().map(Autor::getNombre).sorted().collect(Collectors.toList())
        );

        autor2Field = new TextField();
        autor2Field.getStyleClass().add("form-field");
        autor2Field.setPromptText("Nombre exacto del autor secundario");

        editorialCombo = new ComboBox<>();
        editorialCombo.getStyleClass().add("form-field");
        editorialCombo.setPromptText("Selecciona firma editorial");
        editorialCombo.setEditable(true);
        editorialCombo.getItems().addAll(
                editoriales.values().stream().map(Editorial::getDisplayName).sorted().collect(Collectors.toList())
        );

        generoField = new TextField();
        generoField.getStyleClass().add("form-field");
        generoField.setPromptText("Ej: Novela");

        categoriaField = new TextField();
        categoriaField.getStyleClass().add("form-field");
        categoriaField.setPromptText("Ej: Ficción");

        enlaceField = new TextField();
        enlaceField.getStyleClass().add("form-field");
        enlaceField.setPromptText("https://... (opcional)");

        int row = 0;
        addFormLabel(grid, "Título *:", row, 0); grid.add(tituloField, 1, row++);
        addFormLabel(grid, "Páginas:", row, 0); grid.add(paginasField, 1, row++);
        addFormLabel(grid, "Autor principal *:", row, 0); grid.add(autor1Combo, 1, row++);
        addFormLabel(grid, "Autor secundario:", row, 0); grid.add(autor2Field, 1, row++);
        addFormLabel(grid, "Editorial *:", row, 0); grid.add(editorialCombo, 1, row++);
        addFormLabel(grid, "Género:", row, 0); grid.add(generoField, 1, row++);
        addFormLabel(grid, "Categoría:", row, 0); grid.add(categoriaField, 1, row++);
        addFormLabel(grid, "Enlace compra:", row, 0); grid.add(enlaceField, 1, row++);

        HBox btnBar = new HBox(10);
        Button guardarBtn = new Button("💾 Añadir a deseados");
        guardarBtn.getStyleClass().add("btn-success");
        guardarBtn.setOnAction(e -> agregarLibroDeseado());

        Button limpiarBtn = new Button("🗑 Limpiar");
        limpiarBtn.getStyleClass().add("btn-secondary");
        limpiarBtn.setOnAction(e -> limpiarFormulario());

        btnBar.getChildren().addAll(guardarBtn, limpiarBtn);
        grid.add(btnBar, 0, row, 2, 1);

        return grid;
    }

    private void addFormLabel(GridPane grid, String text, int row, int col) {
        Label lbl = new Label(text);
        lbl.getStyleClass().add("form-label");
        grid.add(lbl, col, row);
    }

    private void agregarLibroDeseado() {
        mensaje.setVisible(false);
        String titulo = tituloField.getText().trim();
        if (titulo.isEmpty()) {
            mostrarMensaje("El título es obligatorio.", "error");
            return;
        }

        for (LibroDeseado ld : librosDeseados.values()) {
            if (Utils.normalizar(ld.getTitulo()).equalsIgnoreCase(Utils.normalizar(titulo))) {
                mostrarMensaje("Este libro ya existe en la lista de deseados.", "error");
                return;
            }
        }

        int numPaginas = 0;
        try {
            if (!paginasField.getText().trim().isEmpty())
                numPaginas = Integer.parseInt(paginasField.getText().trim());
        } catch (NumberFormatException e) {
            mostrarMensaje("Número de páginas inválido.", "error");
            return;
        }

        String autor1Nombre = autor1Combo.getValue();
        if (autor1Nombre == null || autor1Nombre.trim().isEmpty()) {
            mostrarMensaje("El autor principal es obligatorio.", "error");
            return;
        }
        Autor a1Obj = null;
        for (Autor a : autores.values()) {
            if (Utils.normalizar(a.getNombre()).contains(Utils.normalizar(autor1Nombre))) {
                a1Obj = a;
                break;
            }
        }
        if (a1Obj == null) {
            mostrarMensaje("El autor '" + autor1Nombre + "' no existe. Créalo primero.", "error");
            return;
        }

        String autor2Nombre = autor2Field.getText().trim();
        Autor a2Obj = null;
        Integer idAutor2Val = null;
        if (!autor2Nombre.isEmpty()) {
            if (Utils.normalizar(autor2Nombre).equals(Utils.normalizar(a1Obj.getNombre()))) {
                mostrarMensaje("El autor 2 no puede ser el mismo que el autor 1.", "error");
                return;
            }
            for (Autor a : autores.values()) {
                if (Utils.normalizar(a.getNombre()).contains(Utils.normalizar(autor2Nombre))) {
                    a2Obj = a;
                    break;
                }
            }
            if (a2Obj == null) {
                mostrarMensaje("El autor secundario '" + autor2Nombre + "' no existe.", "error");
                return;
            }
            idAutor2Val = a2Obj.getId();
        }

        String editorialNombre = editorialCombo.getValue();
        if (editorialNombre == null || editorialNombre.trim().isEmpty()) {
            mostrarMensaje("La editorial es obligatoria.", "error");
            return;
        }
        Editorial edObj = null;
        for (Editorial e : editoriales.values()) {
            if (Utils.normalizar(e.getDisplayName()).contains(Utils.normalizar(editorialNombre))) {
                edObj = e;
                break;
            }
        }
        if (edObj == null) {
            mostrarMensaje("La editorial '" + editorialNombre + "' no existe.", "error");
            return;
        }

        String genero = generoField.getText().trim();
        String categoria = categoriaField.getText().trim();
        String enlace = enlaceField.getText().trim();

        String sql = "INSERT INTO librosDeseados (Titulo, NumPaginas, ID_Autor1, ID_Autor2, Genero, Categoria, ID_Grupo_Editorial, Enlace_Compra) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, titulo);
            ps.setInt(2, numPaginas);
            ps.setInt(3, a1Obj.getId());
            if (idAutor2Val != null) ps.setInt(4, idAutor2Val);
            else ps.setNull(4, java.sql.Types.INTEGER);
            ps.setString(5, genero.isEmpty() ? "N/A" : genero);
            ps.setString(6, categoria.isEmpty() ? "N/A" : categoria);
            ps.setInt(7, edObj.getId());
            ps.setString(8, enlace.isEmpty() ? null : enlace);

            int filas = ps.executeUpdate();
            if (filas > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    LibroDeseado nuevo = new LibroDeseado(id, titulo, numPaginas, a1Obj, a2Obj,
                            genero.isEmpty() ? "N/A" : genero,
                            categoria.isEmpty() ? "N/A" : categoria,
                            edObj, enlace);
                    librosDeseados.put(id, nuevo);
                    mostrarMensaje("¡'" + titulo + "' añadido a la lista de deseados!", "exito");
                    limpiarFormulario();
                    refreshTable();
                    if (onRefresh != null) onRefresh.run();
                }
            }
        } catch (SQLException e) {
            mostrarMensaje("Error SQL: " + e.getMessage(), "error");
        }
    }

    private void eliminarSeleccionado() {
        mensaje.setVisible(false);
        WishlistRow selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            mostrarMensaje("Selecciona un libro de la lista de deseados.", "error");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Eliminar permanentemente '" + selected.getTitulo() + "' de la lista de deseados?",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Eliminar de deseados");
        if (confirm.showAndWait().orElse(ButtonType.NO) != ButtonType.YES) return;

        String sql = "DELETE FROM librosDeseados WHERE ID = ?";
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, selected.getId());
            int filas = ps.executeUpdate();
            if (filas > 0) {
                librosDeseados.remove(selected.getId());
                mostrarMensaje("✅ Libro eliminado de la lista de deseados.", "exito");
                refreshTable();
                if (onRefresh != null) onRefresh.run();
            }
        } catch (SQLException e) {
            mostrarMensaje("Error SQL: " + e.getMessage(), "error");
        }
    }

    private void moverAAdquiridos() {
        mensaje.setVisible(false);
        WishlistRow selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            mostrarMensaje("Selecciona un libro de la lista de deseados.", "error");
            return;
        }

        for (Libro l : libros.values()) {
            if (Utils.normalizar(l.getTitulo()).equalsIgnoreCase(Utils.normalizar(selected.getTitulo()))) {
                mostrarMensaje("Este libro ya existe en la lista de adquiridos.", "error");
                return;
            }
        }

        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle("Mover a adquiridos");
        dialog.setHeaderText("Datos para '" + selected.getTitulo() + "'");

        ButtonType confirmarBtn = new ButtonType("Confirmar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmarBtn, ButtonType.CANCEL);

        GridPane dialogGrid = new GridPane();
        dialogGrid.setHgap(10);
        dialogGrid.setVgap(10);
        dialogGrid.setPadding(new Insets(20));

        TextField anyoAdqField = new TextField();
        anyoAdqField.setPromptText("Ej: 2026");
        TextField anyoLectField = new TextField();
        anyoLectField.setPromptText("0 = no leído");

        dialogGrid.add(new Label("Año adquisición:"), 0, 0);
        dialogGrid.add(anyoAdqField, 1, 0);
        dialogGrid.add(new Label("Año lectura:"), 0, 1);
        dialogGrid.add(anyoLectField, 1, 1);

        dialog.getDialogPane().setContent(dialogGrid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmarBtn) {
                try {
                    int adq = Integer.parseInt(anyoAdqField.getText().trim());
                    int lec = Integer.parseInt(anyoLectField.getText().trim());
                    return adq >= 0 && lec >= 0 ? (adq << 16) | lec : null;
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(result -> {
            int anyoAdq = result >> 16;
            int anyoLect = result & 0xFFFF;

            String sqlInsert = "INSERT INTO libros (Titulo, NumPaginas, ID_Autor1, ID_Autor2, Genero, Categoria, ID_Grupo_Editorial, Anyo_Lectura, Anyo_Adquisicion) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (Connection con = Conexion.conectar();
                 PreparedStatement ps = con.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {

                ps.setString(1, selected.getTitulo());
                ps.setInt(2, selected.getNumPaginas());
                ps.setInt(3, selected.getIdAutor1());
                if (selected.getIdAutor2() != null) ps.setInt(4, selected.getIdAutor2());
                else ps.setNull(4, java.sql.Types.INTEGER);
                ps.setString(5, selected.getGenero());
                ps.setString(6, selected.getCategoria());
                ps.setInt(7, selected.getIdEditorial());
                ps.setInt(8, anyoLect);
                ps.setInt(9, anyoAdq);

                int filas = ps.executeUpdate();
                if (filas > 0) {
                    ResultSet rs = ps.getGeneratedKeys();
                    if (rs.next()) {
                        int idLibro = rs.getInt(1);

                        String sqlDelete = "DELETE FROM librosDeseados WHERE ID = ?";
                        try (PreparedStatement psDel = con.prepareStatement(sqlDelete)) {
                            psDel.setInt(1, selected.getId());
                            psDel.executeUpdate();
                        }

                        Autor a1 = autores.get(selected.getIdAutor1());
                        Autor a2 = selected.getIdAutor2() != null ? autores.get(selected.getIdAutor2()) : null;
                        Editorial ed = editoriales.get(selected.getIdEditorial());

                        Libro nuevoLibro = new Libro(idLibro, selected.getTitulo(), selected.getNumPaginas(),
                                a1, a2, selected.getGenero(), selected.getCategoria(),
                                ed, anyoLect, anyoAdq);

                        libros.put(idLibro, nuevoLibro);
                        librosDeseados.remove(selected.getId());

                        mostrarMensaje("✅ '" + selected.getTitulo() + "' movido a libros adquiridos.", "exito");
                        refreshTable();
                        if (onRefresh != null) onRefresh.run();
                    }
                }
            } catch (SQLException e) {
                mostrarMensaje("Error SQL: " + e.getMessage(), "error");
            }
        });
    }

    private void refreshTable() {
        items.clear();
        for (LibroDeseado ld : librosDeseados.values()) {
            items.add(new WishlistRow(ld));
        }
        table.setItems(items);
    }

    private void limpiarFormulario() {
        tituloField.clear();
        paginasField.clear();
        autor1Combo.setValue(null);
        autor2Field.clear();
        editorialCombo.setValue(null);
        generoField.clear();
        categoriaField.clear();
        enlaceField.clear();
    }

    private void mostrarMensaje(String msg, String tipo) {
        mensaje.setText(msg);
        if (tipo.equals("error")) {
            mensaje.getStyleClass().setAll("alert-error");
        } else {
            mensaje.getStyleClass().setAll("alert-success");
        }
        mensaje.setVisible(true);
    }

    public Node getView() { return view; }

    public static class WishlistRow {
        private final int id;
        private final String titulo;
        private final int numPaginas;
        private final int idAutor1;
        private final Integer idAutor2;
        private final String autor1;
        private final String autor2;
        private final String genero;
        private final String categoria;
        private final int idEditorial;
        private final String editorial;
        private final String enlaceCompra;

        public WishlistRow(LibroDeseado ld) {
            this.id = ld.getId();
            this.titulo = ld.getTitulo();
            this.numPaginas = ld.getNumPaginas();
            this.idAutor1 = ld.getAutor1() != null ? ld.getAutor1().getId() : 0;
            this.idAutor2 = ld.getAutor2() != null ? ld.getAutor2().getId() : null;
            this.autor1 = ld.getAutor1() != null ? ld.getAutor1().getNombre() : "N/A";
            this.autor2 = ld.getAutor2() != null ? ld.getAutor2().getNombre() : null;
            this.genero = ld.getGenero() != null ? ld.getGenero() : "N/A";
            this.categoria = ld.getCategoria() != null ? ld.getCategoria() : "N/A";
            this.idEditorial = ld.getEditorial() != null ? ld.getEditorial().getId() : 0;
            this.editorial = ld.getEditorial() != null ? ld.getEditorial().getDisplayName() : "N/A";
            this.enlaceCompra = ld.getEnlaceCompra() != null ? ld.getEnlaceCompra() : "";
        }

        public int getId() { return id; }
        public String getTitulo() { return titulo; }
        public int getNumPaginas() { return numPaginas; }
        public int getIdAutor1() { return idAutor1; }
        public Integer getIdAutor2() { return idAutor2; }
        public String getAutor1() { return autor1; }
        public String getAutor2() { return autor2; }
        public String getGenero() { return genero; }
        public String getCategoria() { return categoria; }
        public int getIdEditorial() { return idEditorial; }
        public String getEditorial() { return editorial; }
        public String getEnlaceCompra() { return enlaceCompra; }
    }
}
