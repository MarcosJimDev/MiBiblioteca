package Main.JavaFX.views;

import Main.Java.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.sql.*;
import java.util.HashMap;

public class DeleteBookView {

    private final VBox view;
    private final HashMap<Integer, Libro> libros;

    public DeleteBookView(HashMap<Integer, Libro> libros) {
        this.libros = libros;

        view = new VBox(15);
        view.setPadding(new Insets(20));

        Text title = new Text("❌ Eliminar libro");
        title.getStyleClass().add("page-title");

        Label subtitle = new Label("Selecciona un libro de la tabla y pulsa 'Eliminar'.");
        subtitle.getStyleClass().add("page-subtitle");

        TableView<BookListView.LibroRow> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        TableColumn<BookListView.LibroRow, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("id"));
        idCol.setPrefWidth(40);

        TableColumn<BookListView.LibroRow, String> titCol = new TableColumn<>("Título");
        titCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("titulo"));
        titCol.setPrefWidth(300);

        TableColumn<BookListView.LibroRow, String> autCol = new TableColumn<>("Autor");
        autCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("autor1"));
        autCol.setPrefWidth(150);

        table.getColumns().addAll(idCol, titCol, autCol);

        javafx.collections.ObservableList<BookListView.LibroRow> items = javafx.collections.FXCollections.observableArrayList();
        for (Libro l : libros.values()) {
            items.add(new BookListView.LibroRow(l));
        }
        table.setItems(items);
        table.getSelectionModel().selectFirst();

        Label mensaje = new Label();
        mensaje.setVisible(false);
        mensaje.setWrapText(true);

        HBox btnBar = new HBox(10);
        btnBar.setAlignment(Pos.CENTER);

        Button eliminarBtn = new Button("🗑 Eliminar seleccionado");
        eliminarBtn.getStyleClass().add("btn-danger");
        eliminarBtn.setOnAction(e -> {
            BookListView.LibroRow selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                mensaje.setText("❌ Selecciona un libro primero.");
                mensaje.getStyleClass().setAll("alert-error");
                mensaje.setVisible(true);
                return;
            }

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "¿Eliminar '" + selected.getTitulo() + "'?",
                    ButtonType.YES, ButtonType.NO);
            confirm.setTitle("Confirmar eliminación");
            if (confirm.showAndWait().orElse(ButtonType.NO) != ButtonType.YES) return;

            String sql = "DELETE FROM libros WHERE ID = ?";
            try (Connection con = Conexion.conectar();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, selected.getId());
                int filas = ps.executeUpdate();
                if (filas > 0) {
                    libros.remove(selected.getId());
                    items.remove(selected);
                    mensaje.setText("✅ Libro eliminado correctamente.");
                    mensaje.getStyleClass().setAll("alert-success");
                    mensaje.setVisible(true);
                }
            } catch (SQLException ex) {
                mensaje.setText("❌ Error SQL: " + ex.getMessage());
                mensaje.getStyleClass().setAll("alert-error");
                mensaje.setVisible(true);
            }
        });

        Button refrescarBtn = new Button("🔄 Refrescar");
        refrescarBtn.getStyleClass().add("btn-secondary");
        refrescarBtn.setOnAction(e -> {
            items.clear();
            for (Libro l : libros.values()) {
                items.add(new BookListView.LibroRow(l));
            }
        });

        btnBar.getChildren().addAll(eliminarBtn, refrescarBtn);

        view.getChildren().addAll(title, subtitle, table, mensaje, btnBar);
    }

    public Node getView() { return view; }
}
