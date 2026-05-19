package Main.JavaFX.views;

import Main.Java.*;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.sql.*;
import java.util.HashMap;

public class UpdateReadingYearView {

    private final VBox view;
    private final HashMap<Integer, Libro> libros;
    private TableView<BookListView.LibroRow> table;
    private Label mensaje;
    private TextField anyoField;

    public UpdateReadingYearView(HashMap<Integer, Libro> libros) {
        this.libros = libros;

        view = new VBox(15);
        view.setPadding(new Insets(20));

        Text title = new Text("📝 Actualizar año de lectura");
        title.getStyleClass().add("page-title");

        Label subtitle = new Label("Selecciona un libro de la tabla, introduce el nuevo año y pulsa 'Actualizar'.");
        subtitle.getStyleClass().add("page-subtitle");
        subtitle.setWrapText(true);

        table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        TableColumn<BookListView.LibroRow, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(40);

        TableColumn<BookListView.LibroRow, String> titCol = new TableColumn<>("Título");
        titCol.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        titCol.setPrefWidth(300);

        TableColumn<BookListView.LibroRow, String> lecCol = new TableColumn<>("Año lectura");
        lecCol.setCellValueFactory(new PropertyValueFactory<>("lectura"));
        lecCol.setPrefWidth(80);

        table.getColumns().addAll(idCol, titCol, lecCol);

        javafx.collections.ObservableList<BookListView.LibroRow> items = FXCollections.observableArrayList();
        for (Libro l : libros.values()) {
            items.add(new BookListView.LibroRow(l));
        }
        table.setItems(items);
        table.getSelectionModel().selectFirst();

        mensaje = new Label();
        mensaje.setVisible(false);
        mensaje.setWrapText(true);

        HBox updateBar = new HBox(10);
        updateBar.setAlignment(Pos.CENTER_LEFT);

        anyoField = new TextField();
        anyoField.getStyleClass().add("form-field");
        anyoField.setPromptText("Nuevo año (0 = no leído)");
        anyoField.setPrefWidth(150);

        Button updateBtn = new Button("💾 Actualizar año");
        updateBtn.getStyleClass().add("btn-primary");
        updateBtn.setOnAction(e -> actualizar());

        Button refreshBtn = new Button("🔄 Refrescar");
        refreshBtn.getStyleClass().add("btn-secondary");
        refreshBtn.setOnAction(e -> {
            items.clear();
            for (Libro l : libros.values()) {
                items.add(new BookListView.LibroRow(l));
            }
        });

        updateBar.getChildren().addAll(new Label("Año:"), anyoField, updateBtn, refreshBtn);

        view.getChildren().addAll(title, subtitle, table, updateBar, mensaje);
    }

    private void actualizar() {
        mensaje.setVisible(false);
        BookListView.LibroRow selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            mostrarMensaje("Selecciona un libro primero.", "error");
            return;
        }

        int nuevoAnyo;
        try {
            nuevoAnyo = Integer.parseInt(anyoField.getText().trim());
            if (nuevoAnyo < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            mostrarMensaje("Introduce un año válido (0 = no leído).", "error");
            return;
        }

        String sql = "UPDATE libros SET Anyo_Lectura = ? WHERE ID = ?";
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, nuevoAnyo);
            ps.setInt(2, selected.getId());

            int filas = ps.executeUpdate();
            if (filas > 0) {
                Libro libro = libros.get(selected.getId());
                if (libro != null) {
                    libro.setAnyoLectura(nuevoAnyo);
                }

                // Refresh table
                javafx.collections.ObservableList<BookListView.LibroRow> items = FXCollections.observableArrayList();
                for (Libro l : libros.values()) {
                    items.add(new BookListView.LibroRow(l));
                }
                table.setItems(items);

                String estado = nuevoAnyo > 0 ? "Leído en " + nuevoAnyo : "Marcado como NO leído";
                mostrarMensaje("✅ '" + selected.getTitulo() + "' actualizado. " + estado, "exito");
                anyoField.clear();
            }
        } catch (SQLException e) {
            mostrarMensaje("Error SQL: " + e.getMessage(), "error");
        }
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
}
