package Main.JavaFX.views;

import Main.Java.Autor;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.util.HashMap;

public class AuthorListView {

    private final VBox view;

    public AuthorListView(HashMap<Integer, Autor> autores) {
        view = new VBox(10);
        view.setPadding(new Insets(20));

        Text title = new Text("👤 Listado de autores");
        title.getStyleClass().add("page-title");
        Text subtitle = new Text("Total: " + autores.size() + " autores.");
        subtitle.getStyleClass().add("page-subtitle");

        TableView<AutorRow> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        TableColumn<AutorRow, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(40);

        TableColumn<AutorRow, String> nomCol = new TableColumn<>("Nombre");
        nomCol.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        nomCol.setPrefWidth(200);

        TableColumn<AutorRow, String> nacCol = new TableColumn<>("Nacionalidad");
        nacCol.setCellValueFactory(new PropertyValueFactory<>("nacionalidad"));
        nacCol.setPrefWidth(120);

        TableColumn<AutorRow, String> fecCol = new TableColumn<>("Fecha Nacimiento");
        fecCol.setCellValueFactory(new PropertyValueFactory<>("fechaNacimiento"));
        fecCol.setPrefWidth(120);

        table.getColumns().addAll(idCol, nomCol, nacCol, fecCol);

        javafx.collections.ObservableList<AutorRow> items = FXCollections.observableArrayList();
        for (Autor a : autores.values()) {
            items.add(new AutorRow(a));
        }
        table.setItems(items);

        table.setRowFactory(tv -> {
            TableRow<AutorRow> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && !row.isEmpty()) {
                    AutorRow ar = row.getItem();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Detalle del autor");
                    alert.setHeaderText(ar.getNombre());
                    alert.setContentText(
                            "ID: " + ar.getId() + "\n" +
                            "Nombre: " + ar.getNombre() + "\n" +
                            "Nacionalidad: " + ar.getNacionalidad() + "\n" +
                            "Fecha nacimiento: " + ar.getFechaNacimiento()
                    );
                    alert.showAndWait();
                }
            });
            return row;
        });

        view.getChildren().addAll(title, subtitle, table);
    }

    public Node getView() { return view; }

    public static class AutorRow {
        private final int id;
        private final String nombre;
        private final String nacionalidad;
        private final String fechaNacimiento;

        public AutorRow(Autor a) {
            this.id = a.getId();
            this.nombre = a.getNombre();
            this.nacionalidad = a.getNacionalidad() != null ? a.getNacionalidad() : "N/A";
            this.fechaNacimiento = a.getFechaNacimiento() != null ? a.getFechaNacimiento().toString() : "N/A";
        }

        public int getId() { return id; }
        public String getNombre() { return nombre; }
        public String getNacionalidad() { return nacionalidad; }
        public String getFechaNacimiento() { return fechaNacimiento; }
    }
}
