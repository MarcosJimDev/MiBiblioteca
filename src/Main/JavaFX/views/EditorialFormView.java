package Main.JavaFX.views;

import Main.Java.*;
import Main.Utils.Utils;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.sql.*;
import java.util.HashMap;

public class EditorialFormView {

    private final VBox view;
    private final HashMap<Integer, Editorial> editoriales;
    private final Runnable onSuccess;
    private TextField grupoField, firmaField;
    private Label mensaje;

    public EditorialFormView(HashMap<Integer, Editorial> editoriales, Runnable onSuccess) {
        this.editoriales = editoriales;
        this.onSuccess = onSuccess;

        view = new VBox(15);
        view.setPadding(new Insets(20));

        Text title = new Text("🏢 Añadir nueva editorial / firma");
        title.getStyleClass().add("page-title");

        mensaje = new Label();
        mensaje.setVisible(false);
        mensaje.setWrapText(true);

        GridPane grid = new GridPane();
        grid.getStyleClass().add("card");
        grid.setHgap(15);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        grupoField = new TextField();
        grupoField.getStyleClass().add("form-field");
        grupoField.setPromptText("Nombre del grupo editorial");

        firmaField = new TextField();
        firmaField.getStyleClass().add("form-field");
        firmaField.setPromptText("Firma / sello (dejar vacío = grupo)");

        addLabel(grid, "Grupo Editorial *:", 0, 0);
        grid.add(grupoField, 0, 1);
        addLabel(grid, "Firma Editorial:", 1, 0);
        grid.add(firmaField, 1, 1);

        HBox btnBar = new HBox(10);

        Button guardarGrupo = new Button("💾 Guardar grupo nuevo");
        guardarGrupo.getStyleClass().add("btn-success");
        guardarGrupo.setOnAction(e -> guardarGrupo());

        Button guardarFirma = new Button("📎 Añadir firma a grupo existente");
        guardarFirma.getStyleClass().add("btn-primary");
        guardarFirma.setOnAction(e -> guardarFirma());

        Button limpiar = new Button("🗑 Limpiar");
        limpiar.getStyleClass().add("btn-secondary");
        limpiar.setOnAction(e -> { grupoField.clear(); firmaField.clear(); });

        btnBar.getChildren().addAll(guardarGrupo, guardarFirma, limpiar);
        grid.add(btnBar, 0, 3, 2, 1);

        view.getChildren().addAll(title, mensaje, grid);
    }

    private void addLabel(GridPane grid, String text, int row, int col) {
        Label lbl = new Label(text);
        lbl.getStyleClass().add("form-label");
        grid.add(lbl, col, row);
    }

    private void guardarGrupo() {
        mensaje.setVisible(false);
        String grupo = grupoField.getText().trim();
        if (grupo.isEmpty()) {
            mostrarError("El nombre del grupo editorial es obligatorio.");
            return;
        }

        for (Editorial e : editoriales.values()) {
            if (Utils.normalizar(e.getGrupoEditorial()).contains(Utils.normalizar(grupo))) {
                mostrarError("La editorial '" + grupo + "' ya existe.");
                return;
            }
        }

        String firma = firmaField.getText().trim();

        String sql = "INSERT INTO editoriales (Grupo_Editorial, Firma_Editorial) VALUES (?, ?)";
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, grupo);
            ps.setString(2, firma.isEmpty() ? grupo : firma);

            int filas = ps.executeUpdate();
            if (filas > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    Editorial nueva = new Editorial(id, grupo, firma.isEmpty() ? grupo : firma);
                    editoriales.put(id, nueva);
                    mostrarExito("¡Editorial '" + grupo + "' añadida con ID " + id + "!");
                    grupoField.clear();
                    firmaField.clear();
                    if (onSuccess != null) onSuccess.run();
                }
            }
        } catch (SQLException e) {
            mostrarError("Error SQL: " + e.getMessage());
        }
    }

    private void guardarFirma() {
        mensaje.setVisible(false);
        String grupo = grupoField.getText().trim();
        if (grupo.isEmpty()) {
            mostrarError("Introduce el nombre del grupo editorial existente.");
            return;
        }

        Editorial editorial = null;
        for (Editorial e : editoriales.values()) {
            if (Utils.normalizar(e.getGrupoEditorial()).contains(Utils.normalizar(grupo))) {
                editorial = e;
                break;
            }
        }

        if (editorial == null) {
            mostrarError("La editorial '" + grupo + "' no existe. Agrégala primero como grupo.");
            return;
        }

        String firma = firmaField.getText().trim();
        if (firma.isEmpty()) {
            mostrarError("La firma editorial es obligatoria para añadir un sello.");
            return;
        }

        String sql = "INSERT INTO editoriales (Grupo_Editorial, Firma_Editorial) VALUES (?, ?)";
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, editorial.getGrupoEditorial());
            ps.setString(2, firma);

            int filas = ps.executeUpdate();
            if (filas > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    Editorial nueva = new Editorial(id, editorial.getGrupoEditorial(), firma);
                    editoriales.put(id, nueva);
                    mostrarExito("¡Firma '" + firma + "' añadida a '" + editorial.getGrupoEditorial() + "'!");
                    grupoField.clear();
                    firmaField.clear();
                    if (onSuccess != null) onSuccess.run();
                }
            }
        } catch (SQLException e) {
            mostrarError("Error SQL: " + e.getMessage());
        }
    }

    private void mostrarError(String msg) {
        mensaje.setText("❌ " + msg);
        mensaje.getStyleClass().setAll("alert-error");
        mensaje.setVisible(true);
    }

    private void mostrarExito(String msg) {
        mensaje.setText("✅ " + msg);
        mensaje.getStyleClass().setAll("alert-success");
        mensaje.setVisible(true);
    }

    public Node getView() { return view; }
}
