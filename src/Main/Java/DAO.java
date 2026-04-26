package Main.Java;

import java.sql.*;
import java.util.*;

public class DAO {
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

    public static HashMap<Integer, Libro> cargarLibros(HashMap<Integer, Autor> autores, HashMap<Integer, Editorial> editoriales) {
        HashMap<Integer, Libro> libros = new HashMap<>();

        String sql = "select * from libros";

        try (Connection con = Conexion.conectar()) {
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int idAutor1 = rs.getInt("ID_Autor1");
                int idAutor2 = rs.getInt("ID_Autor2");
                int idEditorial = rs.getInt("ID_Grupo_Editorial");

                Autor autor1 = autores.get(idAutor1);
                Autor autor2 = null;
                if (!rs.wasNull() && idAutor2 != 0) {
                    autor2 = autores.get(idAutor2);
                }
                Editorial editorial = editoriales.get(idEditorial);

                Libro libro = new Libro(rs.getInt("ID"), rs.getString("Titulo"), rs.getInt("NumPaginas"), autor1, autor2, rs.getString("Genero"), rs.getString("Categoria"), editorial, rs.getInt("Anyo_Lectura"), rs.getInt("Anyo_Adquisicion"), rs.getBoolean("Leido"));
                libros.put(libro.getId(), libro);
            }
        } catch (SQLException e) {
            System.out.println("ERROR: se ha producido un error al conectarse a la base de datos " + e);
        }
        return libros;
    }
}
