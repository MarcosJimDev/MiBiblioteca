package Main.JavaFX.views;

import Main.Java.Editorial;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.util.HashMap;

public class EditorialListView {

    private final VBox view;

    public EditorialListView(HashMap<Integer, Editorial> editoriales) {
        view = new VBox(10);
        view.setPadding(new Insets(20));

        Text title = new Text("🏢 Listado de editoriales");
        title.getStyleClass().add("page-title");
        Text subtitle = new Text("Total: " + editoriales.size() + " editoriales.");
        subtitle.getStyleClass().add("page-subtitle");

        TableView<EditorialRow> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        TableColumn<EditorialRow, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(40);

        TableColumn<EditorialRow, String> gruCol = new TableColumn<>("Grupo Editorial");
        gruCol.setCellValueFactory(new PropertyValueFactory<>("grupoEditorial"));
        gruCol.setPrefWidth(200);

        TableColumn<EditorialRow, String> firCol = new TableColumn<>("Firma Editorial");
        firCol.setCellValueFactory(new PropertyValueFactory<>("firmaEditorial"));
        firCol.setPrefWidth(200);

        table.getColumns().addAll(idCol, gruCol, firCol);

        javafx.collections.ObservableList<EditorialRow> items = FXCollections.observableArrayList();
        for (Editorial e : editoriales.values()) {
            items.add(new EditorialRow(e));
        }
        table.setItems(items);

        view.getChildren().addAll(title, subtitle, table);
    }

    public Node getView() { return view; }

    public static class EditorialRow {
        private final int id;
        private final String grupoEditorial;
        private final String firmaEditorial;

        public EditorialRow(Editorial e) {
            this.id = e.getId();
            this.grupoEditorial = e.getGrupoEditorial();
            this.firmaEditorial = e.getFirmaEditorial() != null ? e.getFirmaEditorial() : "N/A";
        }

        public int getId() { return id; }
        public String getGrupoEditorial() { return grupoEditorial; }
        public String getFirmaEditorial() { return firmaEditorial; }
    }
}
