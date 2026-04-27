package Main.Java;

import java.sql.*;
import java.util.*;

public class AutorDAO {
    public static HashMap<Integer, Autor> cargarAutores() {
        HashMap<Integer, Autor> autores = new HashMap<>();

        String sql = "select * from autores";

        try (Connection con = Conexion.conectar()) {
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Autor autor = new Autor(rs.getInt("ID"), rs.getString("Nombre"), rs.getString("Apellidos"), rs.getString("Nacionalidad"), rs.getDate("Fecha_Nacimiento"));
                autores.put(autor.getId(), autor);
            }
        } catch (SQLException e) {
            System.out.println("ERROR: se ha producido un error al conectarse a la base de datos " + e);
        }
        return autores;
    }
}
