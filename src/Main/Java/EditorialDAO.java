package Main.Java;

import Main.Utils.Utils;

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

    public static void agregarNuevaEditorial(HashMap<Integer, Editorial> editorialesMap) {
        Scanner sc = Utils.declararScaner();
        System.out.println("\n--- AGREGAR NUEVA EDITORIAL ---");

        String grupo = BibliotecaUI.pedirCadena(sc, "Nombre del Grupo Editorial (obligatorio): ");

        // Verificación en el HashMap
        for (Editorial e : editorialesMap.values()) {
            if (e.getGrupoEditorial().equalsIgnoreCase(grupo)) {
                System.err.println("ERROR: La editorial '" + grupo + "' ya existe.");
                return;
            }
        }

        String firma = BibliotecaUI.pedirCadena(sc, "Firma Editorial: ");

        String sql = "INSERT INTO editoriales (Grupo_Editorial, Firma_Editorial) VALUES (?, ?)";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, grupo);
            ps.setString(2, Utils.distintoNulo(firma));

            int filas = ps.executeUpdate();
            if (filas > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int idGenerado = rs.getInt(1);
                    // Sincronización
                    Editorial nuevaEd = new Editorial(idGenerado, grupo, firma);
                    editorialesMap.put(idGenerado, nuevaEd);
                    System.out.println("¡Editorial '" + grupo + "' agregada con ID: " + idGenerado + "!");
                }
            }
        } catch (SQLException e) {
            System.err.println("ERROR SQL al agregar editorial: " + e.getMessage());
        }
    }

    public static void eliminarEditorial(HashMap<Integer, Editorial> editorialesMap) {
        Scanner sc = Utils.declararScaner();
        System.out.println("\n--- ELIMINAR EDITORIAL ---");

        String firmaABuscar = BibliotecaUI.pedirCadena(sc, "Introduce la Firma Editorial a eliminar: ");

        Editorial edEncontrada = null;
        for (Editorial e : editorialesMap.values()) {
            // Usamos la firma para localizarla en el HashMap
            if (e.getFirmaEditorial() != null && e.getFirmaEditorial().equalsIgnoreCase(firmaABuscar.trim())) {
                edEncontrada = e;
                break;
            }
        }

        if (edEncontrada == null) {
            System.err.println("ERROR: No se encontró ninguna editorial con la firma '" + firmaABuscar + "'.");
            return;
        }

        boolean confirmar = BibliotecaUI.pedirBoolean(sc, "¿Borrar la editorial '" + edEncontrada.getGrupoEditorial() + "'?");
        if (!confirmar) return;

        String sql = "DELETE FROM editoriales WHERE ID = ?";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, edEncontrada.getId());
            ps.executeUpdate();

            editorialesMap.remove(edEncontrada.getId());
            System.out.println("¡Editorial eliminada con éxito!");

        } catch (SQLException e) {
            if (e.getErrorCode() == 1451) {
                System.err.println("ERROR: No puedes borrar esta editorial; todavía tienes libros de ella.");
            } else {
                System.err.println("ERROR SQL: " + e.getMessage());
            }
        }
    }
}
