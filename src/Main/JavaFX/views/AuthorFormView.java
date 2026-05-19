package Main.JavaFX.views;

import Main.Java.*;
import Main.Utils.Utils;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;

public class AuthorFormView {

    private final VBox view;
    private final HashMap<Integer, Autor> autores;
    private final Runnable onSuccess;
    private TextField nombreField, nacionalidadField, fechaField;
    private Label mensaje;

    public AuthorFormView(HashMap<Integer, Autor> autores, Runnable onSuccess) {
        this.autores = autores;
        this.onSuccess = onSuccess;

        view = new VBox(15);
        view.setPadding(new Insets(20));

        Text title = new Text("➕ Añadir nuevo autor");
        title.getStyleClass().add("page-title");

        mensaje = new Label();
        mensaje.setVisible(false);
        mensaje.setWrapText(true);

        GridPane grid = new GridPane();
        grid.getStyleClass().add("card");
        grid.setHgap(15);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        nombreField = new TextField();
        nombreField.getStyleClass().add("form-field");
        nombreField.setPromptText("Nombre completo");

        nacionalidadField = new TextField();
        nacionalidadField.getStyleClass().add("form-field");
        nacionalidadField.setPromptText("Ej: Española");

        fechaField = new TextField();
        fechaField.getStyleClass().add("form-field");
        fechaField.setPromptText("YYYY-MM-DD");

        addLabel(grid, "Nombre *:", 0, 0); grid.add(nombreField, 0, 1);
        addLabel(grid, "Nacionalidad:", 1, 0); grid.add(nacionalidadField, 1, 1);
        addLabel(grid, "Fecha nacimiento:", 2, 0); grid.add(fechaField, 2, 1);

        HBox btnBar = new HBox(10);
        Button guardar = new Button("💾 Guardar autor");
        guardar.getStyleClass().add("btn-success");
        guardar.setOnAction(e -> guardarAutor());

        Button limpiar = new Button("🗑 Limpiar");
        limpiar.getStyleClass().add("btn-secondary");
        limpiar.setOnAction(e -> { nombreField.clear(); nacionalidadField.clear(); fechaField.clear(); });

        btnBar.getChildren().addAll(guardar, limpiar);
        grid.add(btnBar, 0, 3, 2, 1);

        view.getChildren().addAll(title, mensaje, grid);
    }

    private void addLabel(GridPane grid, String text, int row, int col) {
        Label lbl = new Label(text);
        lbl.getStyleClass().add("form-label");
        grid.add(lbl, col, row);
    }

    private void guardarAutor() {
        mensaje.setVisible(false);
        String nombre = nombreField.getText().trim();
        if (nombre.isEmpty()) {
            mostrarError("El nombre es obligatorio.");
            return;
        }

        for (Autor a : autores.values()) {
            if (Utils.normalizar(a.getNombre()).contains(Utils.normalizar(nombre))) {
                mostrarError("El autor '" + nombre + "' ya existe (o uno muy similar).");
                return;
            }
        }

        String nacionalidad = nacionalidadField.getText().trim();
        Date fecha = null;
        if (!fechaField.getText().trim().isEmpty()) {
            try {
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate ld = LocalDate.parse(fechaField.getText().trim(), fmt);
                fecha = Date.valueOf(ld);
            } catch (DateTimeParseException e) {
                mostrarError("Formato de fecha inválido. Use YYYY-MM-DD.");
                return;
            }
        }

        String sql = "INSERT INTO autores (Nombre, Nacionalidad, Fecha_Nacimiento) VALUES (?, ?, ?)";
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, nombre);
            ps.setString(2, nacionalidad.isEmpty() ? "N/A" : nacionalidad);
            ps.setDate(3, fecha);

            int filas = ps.executeUpdate();
            if (filas > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    Autor nuevo = new Autor(id, nombre, nacionalidad.isEmpty() ? "N/A" : nacionalidad, fecha);
                    autores.put(id, nuevo);
                    mostrarExito("¡Autor '" + nombre + "' añadido con ID " + id + "!");
                    nombreField.clear();
                    nacionalidadField.clear();
                    fechaField.clear();
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
