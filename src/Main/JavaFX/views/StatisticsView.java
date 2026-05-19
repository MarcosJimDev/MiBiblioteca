package Main.JavaFX.views;

import Main.Java.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.util.*;
import java.util.stream.Collectors;

public class StatisticsView {

    private final VBox view;

    public StatisticsView(HashMap<Integer, Libro> libros, HashMap<Integer, Autor> autores,
                          HashMap<Integer, Editorial> editoriales) {
        view = new VBox(20);
        view.setPadding(new Insets(20));

        Text title = new Text("📊 Estadísticas");
        title.getStyleClass().add("page-title");

        long leidos = libros.values().stream().filter(l -> l.getAnyoLectura() > 0).count();
        long pendientes = libros.size() - leidos;
        int totalPag = libros.values().stream().mapToInt(Libro::getNumPaginas).sum();
        double mediaPag = libros.isEmpty() ? 0 : (double) totalPag / libros.size();

        // Fila de estadísticas rápidas
        HBox statsRow = new HBox(15);
        statsRow.setAlignment(Pos.CENTER);
        statsRow.getChildren().addAll(
                miniStat("📖", String.valueOf(libros.size()), "Total libros"),
                miniStat("✅", String.valueOf(leidos), "Leídos"),
                miniStat("⏳", String.valueOf(pendientes), "Pendientes"),
                miniStat("📄", String.valueOf(totalPag), "Páginas totales"),
                miniStat("📏", String.format("%.0f", mediaPag), "Media páginas")
        );

        // Libro más largo y más corto
        GridPane extremeGrid = new GridPane();
        extremeGrid.getStyleClass().add("card");
        extremeGrid.setHgap(20);
        extremeGrid.setVgap(5);
        extremeGrid.setPadding(new Insets(15));

        if (!libros.isEmpty()) {
            Libro masLargo = libros.values().stream().max(Comparator.comparingInt(Libro::getNumPaginas)).orElse(null);
            Libro masCorto = libros.values().stream().min(Comparator.comparingInt(Libro::getNumPaginas)).orElse(null);

            extremeGrid.add(new Label("🏆 Más largo:"), 0, 0);
            extremeGrid.add(new Label((masLargo != null ? masLargo.getTitulo() + " (" + masLargo.getNumPaginas() + " págs)" : "N/A")), 1, 0);
            extremeGrid.add(new Label("📏 Más corto:"), 0, 1);
            extremeGrid.add(new Label((masCorto != null ? masCorto.getTitulo() + " (" + masCorto.getNumPaginas() + " págs)" : "N/A")), 1, 1);
        }

        // Año más productivo
        extremeGrid.add(new Label("📅 Año más leído:"), 0, 2);
        libros.values().stream()
                .filter(l -> l.getAnyoLectura() > 0)
                .collect(Collectors.groupingBy(Libro::getAnyoLectura, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .ifPresentOrElse(
                        e -> extremeGrid.add(new Label(e.getKey() + " (" + e.getValue() + " libros)"), 1, 2),
                        () -> extremeGrid.add(new Label("Sin datos"), 1, 2)
                );

        // Top 3 géneros
        VBox topGenres = new VBox(5);
        topGenres.getStyleClass().add("card");
        topGenres.setPadding(new Insets(15));
        topGenres.getChildren().add(new Label("🎭 Top 3 géneros (categorías):"));

        libros.values().stream()
                .collect(Collectors.groupingBy(Libro::getCategoria, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(3)
                .forEach(e -> topGenres.getChildren().add(new Label("  " + e.getKey() + ": " + e.getValue() + " libros")));

        if (libros.isEmpty()) {
            topGenres.getChildren().add(new Label("  Sin datos"));
        }

        // Gráfico de libros por año
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Año");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Libros");
        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Libros comprados por año");
        chart.setAnimated(false);
        chart.setPrefHeight(250);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Adquisiciones");

        Map<Integer, Long> porAnyo = libros.values().stream()
                .filter(l -> l.getAnyoAdquisicion() > 0)
                .collect(Collectors.groupingBy(Libro::getAnyoAdquisicion, TreeMap::new, Collectors.counting()));

        porAnyo.forEach((anyo, count) -> series.getData().add(new XYChart.Data<>(String.valueOf(anyo), count)));

        if (series.getData().isEmpty()) {
            series.getData().add(new XYChart.Data<>("Sin datos", 0));
        }
        chart.getData().add(series);

        // Autor estrella
        VBox starAuthor = new VBox(5);
        starAuthor.getStyleClass().add("card");
        starAuthor.setPadding(new Insets(15));
        starAuthor.getChildren().add(new Label("⭐ Autor con más libros:"));

        libros.values().stream()
                .map(l -> l.getAutor1().getNombre())
                .filter(n -> !n.equalsIgnoreCase("Geronimo Stilton") && !n.equalsIgnoreCase("Richard Castle") && !n.equalsIgnoreCase("Desconocido"))
                .collect(Collectors.groupingBy(n -> n, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .ifPresentOrElse(
                        e -> starAuthor.getChildren().add(new Label("  " + e.getKey() + " con " + e.getValue() + " libros")),
                        () -> starAuthor.getChildren().add(new Label("  Sin datos"))
                );

        // Distribución por editorial (top 5)
        VBox edDist = new VBox(5);
        edDist.getStyleClass().add("card");
        edDist.setPadding(new Insets(15));
        edDist.getChildren().add(new Label("🏢 Top 5 editoriales:"));

        libros.values().stream()
                .collect(Collectors.groupingBy(l -> l.getEditorial().getDisplayName(), Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .forEach(e -> {
                    double pct = (e.getValue() * 100.0) / libros.size();
                    edDist.getChildren().add(new Label(String.format("  %s: %d libros (%.1f%%)", e.getKey(), e.getValue(), pct)));
                });

        if (libros.isEmpty()) {
            edDist.getChildren().add(new Label("  Sin datos"));
        }

        // Layout en cuadrícula
        HBox topRow = new HBox(15);
        topRow.getChildren().addAll(extremeGrid, topGenres);
        topRow.setAlignment(Pos.TOP_LEFT);

        HBox bottomRow = new HBox(15);
        bottomRow.getChildren().addAll(starAuthor, edDist);
        bottomRow.setAlignment(Pos.TOP_LEFT);

        view.getChildren().addAll(title, statsRow, topRow, chart, bottomRow);
    }

    private VBox miniStat(String icon, String value, String label) {
        VBox card = new VBox(3);
        card.getStyleClass().add("stat-card");
        Text iconText = new Text(icon);
        iconText.setStyle("-fx-font-size: 20px;");
        Text valText = new Text(value);
        valText.getStyleClass().add("stat-value");
        valText.setStyle("-fx-font-size: 22px;");
        Text lblText = new Text(label);
        lblText.getStyleClass().add("stat-label");
        card.getChildren().addAll(iconText, valText, lblText);
        return card;
    }

    public Node getView() { return view; }
}
