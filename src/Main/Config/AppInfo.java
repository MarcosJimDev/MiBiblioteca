package Main.Config;

public class AppInfo {
    private static final String NOMBRE = "MiBiblioteca";
    private static final String VERSION = "1.2.0";
    private static final String AUTOR = "Marcos Jiménez";
    private static final String FECHA_LANZAMIENTO = "27 de abril de 2026";

    public static String getFullVersion() {
        return NOMBRE + " v" + VERSION +  " por " + AUTOR + "\nLanzado el: " + FECHA_LANZAMIENTO;
    }
}
