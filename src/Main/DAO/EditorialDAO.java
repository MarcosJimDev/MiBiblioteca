package Main.DAO;

import Main.UI.BibliotecaUI;
import Main.Java.Conexion;
import Main.Java.Editorial;
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
                Editorial editorial = new Editorial(rs.getInt("ID"), rs.getString("Grupo_Editorial"),
                        rs.getString("Firma_Editorial"));
                editoriales.put(editorial.getId(), editorial);
            }
        } catch (SQLException e) {
            System.out.println("ERROR: se ha producido un error al conectarse a la base de datos " + e);
        }
        return editoriales;
    }

    public static void agregarNuevaEditorial(HashMap<Integer, Editorial> editorialesMap, Scanner sc) {
        System.out.println("\n--- AGREGAR NUEVA EDITORIAL ---");

        String grupo = BibliotecaUI.campoObligatorio(sc, "Nombre del Grupo Editorial (obligatorio): ").trim();
        if (Utils.comprobarSalir(grupo))
            return;

        for (Editorial e : editorialesMap.values()) {
            if (Utils.normalizar(e.getGrupoEditorial()).contains(Utils.normalizar(grupo))) {
                System.err.println("ERROR: La editorial '" + grupo + "' ya existe en el sistema.");
                return;
            }
        }

        String firma = BibliotecaUI.pedirCadena(sc, "Firma Editorial: ").trim();
        if (Utils.comprobarSalir(firma))
            return;

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
                    Editorial nuevaEd = new Editorial(idGenerado, grupo, firma);
                    editorialesMap.put(idGenerado, nuevaEd);
                    System.out.println("¡Editorial '" + grupo + "' agregada con ID: " + idGenerado + "!");
                }
            }
        } catch (SQLException e) {
            System.err.println("ERROR SQL al agregar editorial: " + e.getMessage());
        }
    }

    public static void agregarNuevaFirma(HashMap<Integer, Editorial> editorialesMap, Scanner sc) {
        System.out.println("\n--- AGREGAR NUEVA FIRMA EDITORIAL ---");

        String grupo = BibliotecaUI.campoObligatorio(sc, "**BÚSQUEDA**\nNombre del Grupo Editorial (obligatorio): ").trim();
        if (Utils.comprobarSalir(grupo))
            return;

        Editorial editorial = null;
        for (Editorial e : editorialesMap.values()) {
            if (Utils.normalizar(e.getGrupoEditorial()).contains(Utils.normalizar(grupo))) {
                editorial = e;
                break;
            }
        }

        if (editorial == null) {
            System.err.println("ERROR: La editorial '" + grupo + "' no existe en el sistema.");
            return;
        }

        String firma = BibliotecaUI.campoObligatorio(sc, "Firma Editorial: ").trim();
        if (Utils.comprobarSalir(firma))
            return;

        if (editorial != null) {
            String sql = "INSERT INTO editoriales (Grupo_Editorial, Firma_Editorial) VALUES (?, ?)";

            try (Connection con = Conexion.conectar();
                 PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                ps.setString(1, editorial.getGrupoEditorial());
                ps.setString(2, firma);

                int filas = ps.executeUpdate();
                if (filas > 0) {
                    ResultSet rs = ps.getGeneratedKeys();
                    if (rs.next()) {
                        int idGenerado = rs.getInt(1);
                        Editorial nuevaFirma = new Editorial(idGenerado, editorial.getGrupoEditorial(), firma);
                        editorialesMap.put(idGenerado, nuevaFirma);
                        System.out.println("¡La firma '" + grupo + "' agregada con ID: " + idGenerado + "!");
                    }
                }
            } catch (SQLException e) {
                System.err.println("ERROR SQL al agregar editorial: " + e.getMessage());
            }
        }
    }

    public static void eliminarEditorial(HashMap<Integer, Editorial> editorialesMap, Scanner sc) {
        System.out.println("\n--- ELIMINAR EDITORIAL ---");

        String busqueda = BibliotecaUI.pedirCadena(sc, "Introduce el ID del Grupo Editorial: ").trim();
        if (Utils.comprobarSalir(busqueda))
            return;

        int idEditorial = 0;
        try {
            idEditorial = Integer.parseInt(busqueda);
        } catch (NumberFormatException e) {
            System.err.println("ERROR: debes introducir un número válido.");
            return;
        }

        Editorial coincidencias = null;
        for (Editorial e : editorialesMap.values()) {
            if (e.getId() == idEditorial) {
                coincidencias = e;
                break;
            }
        }

        if (coincidencias == null) {
            System.err.println("No se encontró ninguna editorial con ID: " + idEditorial + ".");
            return;
        }

        ejecutarBorradoEditorial(coincidencias, editorialesMap, sc);
    }

    private static void ejecutarBorradoEditorial(Editorial ed, HashMap<Integer, Editorial> mapa, Scanner sc) {
        boolean confirmar = BibliotecaUI.pedirBoolean(sc, "¿Seguro que quieres borrar '" + ed.getGrupoEditorial() + "'?");
        if (!confirmar) return;

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement("DELETE FROM editoriales WHERE ID = ?")) {

            ps.setInt(1, ed.getId());
            ps.executeUpdate();

            mapa.remove(ed.getId());
            System.out.println("¡Editorial eliminada correctamente!");

        } catch (SQLException e) {
            if (e.getErrorCode() == 1451) {
                System.err.println("ERROR: No puedes borrar esta editorial porque tiene libros asociados.");
            } else {
                System.err.println("Error SQL: " + e.getMessage());
            }
        }
    }
}
