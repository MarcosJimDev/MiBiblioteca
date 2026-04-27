package Main.DAO;

import Main.Java.Autor;
import Main.UI.BibliotecaUI;
import Main.Java.Conexion;
import Main.Utils.Utils;

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
                Autor autor = new Autor(rs.getInt("ID"), rs.getString("Nombre"), rs.getString("Nacionalidad"), rs.getDate("Fecha_Nacimiento"));
                autores.put(autor.getId(), autor);
            }
        } catch (SQLException e) {
            System.out.println("ERROR: se ha producido un error al conectarse a la base de datos " + e);
        }
        return autores;
    }

    public static void agregarNuevoAutor(HashMap<Integer, Autor> autoresMap, Scanner sc) {
        System.out.println("\n--- AGREGAR NUEVO AUTOR ---");

        System.out.println("Nombre completo (obligatorio): ");
        String nombre = BibliotecaUI.campoObligatorio(sc, "Nombre completo (obligatorio): ");

        if (nombre.equalsIgnoreCase("salir"))
            return;

        for (Autor a : autoresMap.values()) {
            if (Utils.normalizar(a.getNombre()).contains(Utils.normalizar(nombre))) {
                System.err.println("ERROR: El autor '" + nombre + "' ya existe (o uno muy similar).");
                return;
            }
        }

        String nacionalidad = BibliotecaUI.pedirCadena(sc, "Nacionalidad: ");
        System.out.println("Fecha de nacimiento (formato YYYY-MM-DD): ");
        java.sql.Date fecha = BibliotecaUI.dateValido(sc, "Por favor, introduce una fecha válida (YYYY-MM-DD): ");

        String sql = "INSERT INTO autores (Nombre, Nacionalidad, Fecha_Nacimiento) VALUES (?, ?, ?)";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, nombre);
            ps.setString(2, Utils.distintoNulo(nacionalidad));
            ps.setDate(3, fecha);

            int filas = ps.executeUpdate();
            if (filas > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int idGenerado = rs.getInt(1);
                    Autor nuevoAutor = new Autor(idGenerado, nombre, nacionalidad, fecha);
                    autoresMap.put(idGenerado, nuevoAutor);
                    System.out.println("¡Autor '" + nombre + "' agregado correctamente con ID: " + idGenerado + "!");
                }
            }
        } catch (SQLException e) {
            System.err.println("ERROR SQL al agregar autor: " + e.getMessage());
        }
    }

    public static void eliminarAutor(HashMap<Integer, Autor> autoresMap, Scanner sc) {
        System.out.println("\n--- ELIMINAR AUTOR ---");

        System.out.println("Introduce el nombre completo del autor: ");
        String nombreABuscar = BibliotecaUI.campoObligatorio(sc, "Introduce el nombre completo del autor: ");

        if (nombreABuscar.equalsIgnoreCase("salir"))
            return;

        Autor autorEncontrado = null;
        for (Autor a : autoresMap.values()) {
            if (Utils.normalizar(a.getNombre()).contains(Utils.normalizar(nombreABuscar))) {
                autorEncontrado = a;
                break;
            }
        }

        if (autorEncontrado == null) {
            System.err.println("ERROR: No existe ningún autor llamado '" + nombreABuscar + "'.");
            return;
        }

        boolean confirmar = BibliotecaUI.pedirBoolean(sc, "¿Seguro que quieres borrar a '" + autorEncontrado.getNombre() + "'? (Esto fallará si tiene libros asociados)");
        if (!confirmar) return;

        String sql = "DELETE FROM autores WHERE ID = ?";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, autorEncontrado.getId());
            ps.executeUpdate();

            autoresMap.remove(autorEncontrado.getId());
            System.out.println("¡Autor eliminado correctamente!");

        } catch (SQLException e) {
            if (e.getErrorCode() == 1451) {
                System.err.println("ERROR: No puedes borrar este autor porque tiene libros asociados en tu biblioteca.");
            } else {
                System.err.println("ERROR SQL: " + e.getMessage());
            }
        }
    }
}
