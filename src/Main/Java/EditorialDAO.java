package Main.Java;

import java.sql.*;
import java.util.*;

public class EditorialDAO {
    public static HashMap<Integer, Editorial> cargarEditoriales() {
        HashMap<Integer, Editorial> editoriales = new HashMap<>();

        String sql = "select * from editoriales";

        try (Connection con = Conexion.conectar()) {
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Editorial editorial = new Editorial(rs.getInt("ID"), rs.getString("Grupo_Editorial"), rs.getString("Firma_Editorial"));
                editoriales.put(editorial.getId(), editorial);
            }
        } catch (SQLException e) {
            System.out.println("ERROR: se ha producido un error al conectarse a la base de datos " + e);
        }
        return editoriales;
    }
}
