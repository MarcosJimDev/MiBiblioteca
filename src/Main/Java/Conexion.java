package Main.Java;

import java.sql.*;

public class Conexion {
    static final String URL = "jdbc:mysql://127.0.0.1:3306/MiBiblioteca";
    static final String USER = "root";
    static final String PASS = "root";

    public static Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
