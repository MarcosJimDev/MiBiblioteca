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
import java.util.stream.Collectors;

public class BookFormView {

    private final VBox view;
    private final HashMap<Integer, Libro> libros;
    private final HashMap<Integer, Autor> autores;
    private final HashMap<Integer, Editorial> editoriales;
    private final Runnable onSuccess;

    private TextField tituloField, paginasField, autor2Field, generoField, categoriaField;
    private ComboBox<String> autor1Combo, editorialCombo;
    private TextField anyoAdqField, anyoLectField;
    private Label mensaje;

    public BookFormView(HashMap<Integer, Libro> libros, HashMap<Integer, Autor> autores,
                        HashMap<Integer, Editorial> editoriales, Runnable onSuccess) {
        this.libros = libros;
        this.autores = autores;
        this.editoriales = editoriales;
        this.onSuccess = onSuccess;

        view = new VBox(15);
        view.setPadding(new Insets(20));

        Text title = new Text("➕ Añadir nuevo libro");
        title.getStyleClass().add("page-title");

        mensaje = new Label();
        mensaje.setVisible(false);

        ScrollPane formScroll = new ScrollPane(createForm());
        formScroll.setFitToWidth(true);
        formScroll.setBorder(null);

        view.getChildren().addAll(title, mensaje, formScroll);
    }

    private GridPane createForm() {
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
        generoField.setPromptText("Ej: Novela, Ensayo...");

        categoriaField = new TextField();
        categoriaField.getStyleClass().add("form-field");
        categoriaField.setPromptText("Ej: Ficción, Historia...");

        anyoAdqField = new TextField();
        anyoAdqField.getStyleClass().add("form-field");
        anyoAdqField.setPromptText("Ej: 2026");

        anyoLectField = new TextField();
        anyoLectField.getStyleClass().add("form-field");
        anyoLectField.setPromptText("0 = no leído");

        int row = 0;
        addLabel(grid, "Título *:", row, 0); grid.add(tituloField, 1, row++);
        addLabel(grid, "Páginas:", row, 0); grid.add(paginasField, 1, row++);
        addLabel(grid, "Autor principal *:", row, 0); grid.add(autor1Combo, 1, row++);
        addLabel(grid, "Autor secundario:", row, 0); grid.add(autor2Field, 1, row++);
        addLabel(grid, "Editorial *:", row, 0); grid.add(editorialCombo, 1, row++);
        addLabel(grid, "Género:", row, 0); grid.add(generoField, 1, row++);
        addLabel(grid, "Categoría:", row, 0); grid.add(categoriaField, 1, row++);
        addLabel(grid, "Año adquisición:", row, 0); grid.add(anyoAdqField, 1, row++);
        addLabel(grid, "Año lectura:", row, 0); grid.add(anyoLectField, 1, row++);

        HBox btnBar = new HBox(10);
        Button guardar = new Button("💾 Guardar libro");
        guardar.getStyleClass().add("btn-success");
        guardar.setOnAction(e -> guardarLibro());

        Button limpiar = new Button("🗑 Limpiar");
        limpiar.getStyleClass().add("btn-secondary");
        limpiar.setOnAction(e -> limpiarFormulario());

        btnBar.getChildren().addAll(guardar, limpiar);
        grid.add(btnBar, 0, row, 2, 1);

        return grid;
    }

    private void addLabel(GridPane grid, String text, int row, int col) {
        Label lbl = new Label(text);
        lbl.getStyleClass().add("form-label");
        grid.add(lbl, col, row);
    }

    private void guardarLibro() {
        mensaje.setVisible(false);
        String titulo = tituloField.getText().trim();
        if (titulo.isEmpty()) {
            mostrarError("El título es obligatorio.");
            return;
        }

        for (Libro l : libros.values()) {
            if (Utils.normalizar(l.getTitulo()).equalsIgnoreCase(Utils.normalizar(titulo))) {
                mostrarError("Este libro ya existe en la base de datos.");
                return;
            }
        }

        int numPaginas = 0;
        try {
            if (!paginasField.getText().trim().isEmpty())
                numPaginas = Integer.parseInt(paginasField.getText().trim());
        } catch (NumberFormatException e) {
            mostrarError("Número de páginas inválido.");
            return;
        }

        String autor1Nombre = autor1Combo.getValue();
        if (autor1Nombre == null || autor1Nombre.trim().isEmpty()) {
            mostrarError("El autor principal es obligatorio.");
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
            mostrarError("El autor '" + autor1Nombre + "' no existe. Créalo primero.");
            return;
        }

        String autor2Nombre = autor2Field.getText().trim();
        Autor a2Obj = null;
        Integer idAutor2Val = null;
        if (!autor2Nombre.isEmpty()) {
            if (Utils.normalizar(autor2Nombre).equals(Utils.normalizar(a1Obj.getNombre()))) {
                mostrarError("El autor 2 no puede ser el mismo que el autor 1.");
                return;
            }
            for (Autor a : autores.values()) {
                if (Utils.normalizar(a.getNombre()).contains(Utils.normalizar(autor2Nombre))) {
                    a2Obj = a;
                    break;
                }
            }
            if (a2Obj == null) {
                mostrarError("El autor secundario '" + autor2Nombre + "' no existe.");
                return;
            }
            idAutor2Val = a2Obj.getId();
        }

        String editorialNombre = editorialCombo.getValue();
        if (editorialNombre == null || editorialNombre.trim().isEmpty()) {
            mostrarError("La editorial es obligatoria.");
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
            mostrarError("La editorial '" + editorialNombre + "' no existe.");
            return;
        }

        String genero = generoField.getText().trim();
        String categoria = categoriaField.getText().trim();

        int anyoAdquisicion = 0;
        try {
            if (!anyoAdqField.getText().trim().isEmpty())
                anyoAdquisicion = Integer.parseInt(anyoAdqField.getText().trim());
        } catch (NumberFormatException e) {
            mostrarError("Año de adquisición inválido.");
            return;
        }

        int anyoLectura = 0;
        try {
            if (!anyoLectField.getText().trim().isEmpty())
                anyoLectura = Integer.parseInt(anyoLectField.getText().trim());
        } catch (NumberFormatException e) {
            mostrarError("Año de lectura inválido.");
            return;
        }

        String sql = "INSERT INTO libros (Titulo, NumPaginas, ID_Autor1, ID_Autor2, Genero, Categoria, ID_Grupo_Editorial, Anyo_Lectura, Anyo_Adquisicion) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

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
            ps.setInt(8, anyoLectura);
            ps.setInt(9, anyoAdquisicion);

            int filas = ps.executeUpdate();
            if (filas > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    Libro nuevo = new Libro(id, titulo, numPaginas, a1Obj, a2Obj,
                            genero.isEmpty() ? "N/A" : genero,
                            categoria.isEmpty() ? "N/A" : categoria,
                            edObj, anyoLectura, anyoAdquisicion);
                    libros.put(id, nuevo);
                    mostrarExito("¡Libro '" + titulo + "' añadido correctamente!");
                    limpiarFormulario();
                    if (onSuccess != null) onSuccess.run();
                }
            }
        } catch (SQLException e) {
            mostrarError("Error SQL: " + e.getMessage());
        }
    }

    private void limpiarFormulario() {
        tituloField.clear();
        paginasField.clear();
        autor1Combo.setValue(null);
        autor2Field.clear();
        editorialCombo.setValue(null);
        generoField.clear();
        categoriaField.clear();
        anyoAdqField.clear();
        anyoLectField.clear();
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
