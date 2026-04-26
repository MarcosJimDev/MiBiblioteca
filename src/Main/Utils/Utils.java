package Main.Utils;

import java.sql.*;

public class Utils {
    private static final String SI = "Sí";
    private static final String NO = "No";

    public static String convertirBooleanAString(boolean valor) {
        return valor ? SI : NO;
    }

    public static String distintoNulo(Object obj) {
        return (obj == null) ? "N/A" : obj.toString();
    }
}
