package Main.JavaFX.views;

import Main.Java.Conexion;
import Main.Java.Editorial;
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

public class DeleteEditorialView {

    private final VBox view;

    public DeleteEditorialView(HashMap<Integer, Editorial> editoriales) {
        view = new VBox(15);
        view.setPadding(new Insets(20));

        Text title = new Text("❌ Eliminar editorial");
        title.getStyleClass().add("page-title");

        Label subtitle = new Label("Selecciona una editorial y pulsa 'Eliminar'. No se eliminará si tiene libros asociados.");
        subtitle.getStyleClass().add("page-subtitle");
        subtitle.setWrapText(true);

        TableView<EditorialListView.EditorialRow> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        TableColumn<EditorialListView.EditorialRow, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(40);

        TableColumn<EditorialListView.EditorialRow, String> gruCol = new TableColumn<>("Grupo");
        gruCol.setCellValueFactory(new PropertyValueFactory<>("grupoEditorial"));
        gruCol.setPrefWidth(200);

        TableColumn<EditorialListView.EditorialRow, String> firCol = new TableColumn<>("Firma");
        firCol.setCellValueFactory(new PropertyValueFactory<>("firmaEditorial"));
        firCol.setPrefWidth(200);

        table.getColumns().addAll(idCol, gruCol, firCol);

        javafx.collections.ObservableList<EditorialListView.EditorialRow> items = FXCollections.observableArrayList();
        for (Editorial e : editoriales.values()) {
            items.add(new EditorialListView.EditorialRow(e));
        }
        table.setItems(items);
        table.getSelectionModel().selectFirst();

        Label mensaje = new Label();
        mensaje.setVisible(false);
        mensaje.setWrapText(true);

        HBox btnBar = new HBox(10);
        btnBar.setAlignment(Pos.CENTER);

        Button eliminarBtn = new Button("🗑 Eliminar seleccionada");
        eliminarBtn.getStyleClass().add("btn-danger");
        eliminarBtn.setOnAction(e -> {
            EditorialListView.EditorialRow selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                mensaje.setText("❌ Selecciona una editorial primero.");
                mensaje.getStyleClass().setAll("alert-error");
                mensaje.setVisible(true);
                return;
            }

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "¿Eliminar '" + selected.getGrupoEditorial() + " - " + selected.getFirmaEditorial() + "'?",
                    ButtonType.YES, ButtonType.NO);
            confirm.setTitle("Confirmar eliminación");
            if (confirm.showAndWait().orElse(ButtonType.NO) != ButtonType.YES) return;

            String sql = "DELETE FROM editoriales WHERE ID = ?";
            try (Connection con = Conexion.conectar();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, selected.getId());
                int filas = ps.executeUpdate();
                if (filas > 0) {
                    editoriales.remove(selected.getId());
                    items.remove(selected);
                    mensaje.setText("✅ Editorial eliminada correctamente.");
                    mensaje.getStyleClass().setAll("alert-success");
                    mensaje.setVisible(true);
                }
            } catch (SQLException ex) {
                if (ex.getErrorCode() == 1451) {
                    mensaje.setText("❌ No puedes borrar esta editorial porque tiene libros asociados.");
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
            for (Editorial ed : editoriales.values()) {
                items.add(new EditorialListView.EditorialRow(ed));
            }
        });

        btnBar.getChildren().addAll(eliminarBtn, refrescarBtn);

        view.getChildren().addAll(title, subtitle, table, mensaje, btnBar);
    }

    public Node getView() { return view; }
}
