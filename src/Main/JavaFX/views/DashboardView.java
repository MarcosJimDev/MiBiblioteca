package Main.JavaFX.views;

import Main.Java.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.util.HashMap;

public class DashboardView {

    private final VBox view;

    public DashboardView(HashMap<Integer, Autor> autores, HashMap<Integer, Editorial> editoriales,
                         HashMap<Integer, Libro> libros, HashMap<Integer, LibroDeseado> librosDeseados) {
        view = new VBox(20);
        view.setPadding(new Insets(20));

        Text title = new Text("📚 MiBiblioteca");
        title.getStyleClass().add("page-title");

        Text subtitle = new Text("Gestiona tu biblioteca personal de forma sencilla.");
        subtitle.getStyleClass().add("page-subtitle");

        long leidos = libros.values().stream().filter(l -> l.getAnyoLectura() > 0).count();
        long pendientes = libros.size() - leidos;

        HBox statsRow1 = new HBox(15);
        statsRow1.setAlignment(Pos.CENTER);
        statsRow1.getChildren().addAll(
                statCard("📖", String.valueOf(libros.size()), "Libros totales"),
                statCard("✅", String.valueOf(leidos), "Libros leídos"),
                statCard("⏳", String.valueOf(pendientes), "Pendientes")
        );

        HBox statsRow2 = new HBox(15);
        statsRow2.setAlignment(Pos.CENTER);
        statsRow2.getChildren().addAll(
                statCard("⭐", String.valueOf(librosDeseados.size()), "Deseados"),
                statCard("👤", String.valueOf(autores.size()), "Autores"),
                statCard("🏢", String.valueOf(editoriales.size()), "Editoriales")
        );

        VBox statsRow = new VBox(15);
        statsRow.setAlignment(Pos.CENTER);
        statsRow.getChildren().addAll(statsRow1, statsRow2);

        GridPane infoGrid = new GridPane();
        infoGrid.getStyleClass().add("card");
        infoGrid.setHgap(40);
        infoGrid.setVgap(10);
        infoGrid.setPadding(new Insets(20));

        infoGrid.add(new Label("📊 Resumen rápido"), 0, 0, 2, 1);

        infoGrid.add(new Label("Total páginas:"), 0, 1);
        int totalPag = libros.values().stream().mapToInt(Libro::getNumPaginas).sum();
        infoGrid.add(new Label(String.valueOf(totalPag)), 1, 1);

        infoGrid.add(new Label("Media páginas/libro:"), 0, 2);
        double media = libros.isEmpty() ? 0 : (double) totalPag / libros.size();
        infoGrid.add(new Label(String.format("%.1f", media)), 1, 2);

        libros.values().stream()
                .filter(l -> l.getAnyoLectura() > 0)
                .collect(java.util.stream.Collectors.groupingBy(
                        Libro::getAnyoLectura,
                        java.util.stream.Collectors.counting()
                ))
                .entrySet().stream()
                .max(java.util.Map.Entry.comparingByValue())
                .ifPresent(e -> {
                    infoGrid.add(new Label("Año más leído:"), 0, 3);
                    infoGrid.add(new Label(e.getKey() + " (" + e.getValue() + " libros)"), 1, 3);
                });

        view.getChildren().addAll(title, subtitle, statsRow, infoGrid);
    }

    private VBox statCard(String icon, String value, String label) {
        VBox card = new VBox(5);
        card.getStyleClass().add("stat-card");
        Text iconText = new Text(icon);
        iconText.setStyle("-fx-font-size: 24px;");
        Text valText = new Text(value);
        valText.getStyleClass().add("stat-value");
        Text lblText = new Text(label);
        lblText.getStyleClass().add("stat-label");
        card.getChildren().addAll(iconText, valText, lblText);
        return card;
    }

    public Node getView() { return view; }
}
