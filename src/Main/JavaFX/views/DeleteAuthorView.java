package Main.JavaFX.views;

import Main.Java.Autor;
import Main.Java.Conexion;
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

public class DeleteAuthorView {

    private final VBox view;

    public DeleteAuthorView(HashMap<Integer, Autor> autores) {
        view = new VBox(15);
        view.setPadding(new Insets(20));

        Text title = new Text("❌ Eliminar autor");
        title.getStyleClass().add("page-title");

        Label subtitle = new Label("Selecciona un autor y pulsa 'Eliminar'. No se eliminará si tiene libros asociados.");
        subtitle.getStyleClass().add("page-subtitle");
        subtitle.setWrapText(true);

        TableView<AuthorListView.AutorRow> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        TableColumn<AuthorListView.AutorRow, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(40);

        TableColumn<AuthorListView.AutorRow, String> nomCol = new TableColumn<>("Nombre");
        nomCol.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        nomCol.setPrefWidth(300);

        table.getColumns().addAll(idCol, nomCol);

        javafx.collections.ObservableList<AuthorListView.AutorRow> items = FXCollections.observableArrayList();
        for (Autor a : autores.values()) {
            items.add(new AuthorListView.AutorRow(a));
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
            AuthorListView.AutorRow selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                mensaje.setText("❌ Selecciona un autor primero.");
                mensaje.getStyleClass().setAll("alert-error");
                mensaje.setVisible(true);
                return;
            }

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "¿Eliminar a '" + selected.getNombre() + "'? (fallará si tiene libros)",
                    ButtonType.YES, ButtonType.NO);
            confirm.setTitle("Confirmar eliminación");
            if (confirm.showAndWait().orElse(ButtonType.NO) != ButtonType.YES) return;

            String sql = "DELETE FROM autores WHERE ID = ?";
            try (Connection con = Conexion.conectar();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, selected.getId());
                int filas = ps.executeUpdate();
                if (filas > 0) {
                    autores.remove(selected.getId());
                    items.remove(selected);
                    mensaje.setText("✅ Autor eliminado correctamente.");
                    mensaje.getStyleClass().setAll("alert-success");
                    mensaje.setVisible(true);
                }
            } catch (SQLException ex) {
                if (ex.getErrorCode() == 1451) {
                    mensaje.setText("❌ No puedes borrar este autor porque tiene libros asociados.");
                } else {
                    mensaje.setText("❌ Error SQL: " + ex.getMessage());
                }
                mensaje.getStyleClass().setAll("alert-error");
                mensaje.setVisible(true);
            }
        });

        Button refrescarBtn = new Button("🔄 Refrescar");
        refrescarBtn.getStyleClass().add("btn-secondary");
        refrescarBtn.setOnAction(e -> {
            items.clear();
            for (Autor a : autores.values()) {
                items.add(new AuthorListView.AutorRow(a));
            }
        });

        btnBar.getChildren().addAll(eliminarBtn, refrescarBtn);

        view.getChildren().addAll(title, subtitle, table, mensaje, btnBar);
    }

    public Node getView() { return view; }
}
