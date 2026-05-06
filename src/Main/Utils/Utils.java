package Main.Utils;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.text.Normalizer;

public class Utils {
    private static final String SI = "Sí";
    private static final String NO = "No";

    public static String convertirBooleanAString(boolean valor) {
        return valor ? SI : NO;
    }

    public static String distintoNulo(Object obj) {
        return (obj == null) ? "N/A" : obj.toString();
    }

    public static Scanner declararScanner() {
        return new Scanner(System.in, StandardCharsets.UTF_8);
    }

    public static String normalizar(Object obj) {
        if (obj == null) return "";
        String texto = obj.toString();
        // NFD separa la letra de su tilde (ej: 'á' -> 'a' + '´')
        String normalizado = Normalizer.normalize(texto, Normalizer.Form.NFD);
        // Quitamos los "garabatos" (acentos) y pasamos a minúsculas
        return normalizado.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "").toLowerCase().trim();
    }

    public static Boolean comprobarSalir(String entrada) {
        if (entrada != null &&entrada.equalsIgnoreCase("salir")) {
            System.out.println("Volviendo al menú principal...");
            return true;
        }
        return false;
    }

    public static void tiempoMuerto() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
