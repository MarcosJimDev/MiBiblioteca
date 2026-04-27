package Main.DAO;

import java.sql.*;
import java.util.*;

import Main.Java.*;
import Main.UI.BibliotecaUI;
import Main.Utils.Utils;

public class LibroDAO {

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

                Libro libro = new Libro(rs.getInt("ID"), rs.getString("Titulo"), rs.getInt("NumPaginas"), autor1,
                        autor2, rs.getString("Genero"), rs.getString("Categoria"), editorial, rs.getInt("Anyo_Lectura"),
                        rs.getInt("Anyo_Adquisicion"), rs.getBoolean("Leido"));
                libros.put(libro.getId(), libro);
            }
        } catch (SQLException e) {
            System.out.println("ERROR: se ha producido un error al conectarse a la base de datos " + e);
        }
        return libros;
    }

    public static void agregarNuevoLibro(HashMap<Integer, Libro> librosMap, HashMap<Integer, Autor> autoresMap, HashMap<Integer, Editorial> editorialesMap, Scanner sc) {
        System.out.println("\n--- AGREGAR NUEVO LIBRO ---");

        System.out.println("Título (obligatorio): ");
        String titulo = BibliotecaUI.campoObligatorio(sc, "Título (obligatorio): ");

        int numPaginas = BibliotecaUI.pedirEntero(sc, "Número de páginas: ");

        System.out.println("Nombre Autor Principal (obligatorio): ");
        String nombreAutor1 = BibliotecaUI.campoObligatorio(sc, "Nombre Autor Principal (obligatorio): ");
        String autor1Norm = Utils.normalizar(nombreAutor1);
        Autor a1Obj = null;

        for (Autor a : autoresMap.values()) {
            if (Utils.normalizar(a.getNombre()).contains(autor1Norm)) {
                a1Obj = a;
                break;
            }
        }

        if (a1Obj == null) {
            System.err.println("ERROR: El autor '" + nombreAutor1 + "' no existe. Créalo primero.");
            return;
        }
        int idAutor1 = a1Obj.getId();

        String nombreAutor2 = BibliotecaUI.pedirCadena(sc, "Nombre Autor Secundario (Enter si no tiene): ");
        Autor a2Obj = null;
        Integer idAutor2Val = null;

        if (!nombreAutor2.isEmpty()) {
            String autor2Norm = Utils.normalizar(nombreAutor2);
            if (autor2Norm.equals(autor1Norm)) {
                System.err.println("ERROR: El Autor 2 no puede ser el mismo que el Autor 1.");
                return;
            }

            for (Autor a : autoresMap.values()) {
                if (Utils.normalizar(a.getNombre()).contains(autor2Norm)) {
                    a2Obj = a;
                    break;
                }
            }

            if (a2Obj == null) {
                System.err.println("ERROR: El autor secundario '" + nombreAutor2 + "' no existe.");
                return;
            }
            idAutor2Val = a2Obj.getId();
        }

        System.out.println("Nombre Editorial (obligatorio): ");
        String nombreEditorial = BibliotecaUI.campoObligatorio(sc, "Nombre Editorial (obligatorio): ");
        String editorialNorm = Utils.normalizar(nombreEditorial);
        Editorial edObj = null;

        for (Editorial e : editorialesMap.values()) {
            if (Utils.normalizar(e.getGrupoEditorial()).contains(editorialNorm)) {
                edObj = e;
                break;
            }
        }

        if (edObj == null) {
            System.err.println("ERROR: La editorial '" + nombreEditorial + "' no existe.");
            return;
        }
        int idEditorial = edObj.getId();

        String genero = BibliotecaUI.pedirCadena(sc, "Género: ");
        String categoria = BibliotecaUI.pedirCadena(sc, "Categoría: ");
        int anyoAdquisicion = BibliotecaUI.pedirEntero(sc, "Año Adquisición (ej. 2026): ");
        int anyoLectura = BibliotecaUI.pedirEntero(sc, "Año Lectura (0 si no leído): ");
        boolean leido = BibliotecaUI.pedirBoolean(sc, "¿Ya lo has leído? (s/n): ");

        if (leido && anyoLectura == 0) anyoLectura = anyoAdquisicion;
        if (!leido) anyoLectura = 0;

        String sqlInsert = "INSERT INTO libros (Titulo, NumPaginas, ID_Autor1, ID_Autor2, Genero, Categoria, ID_Grupo_Editorial, Anyo_Lectura, Anyo_Adquisicion, Leido) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        int generatedId = -1;

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, titulo.trim());
            ps.setInt(2, numPaginas);
            ps.setInt(3, idAutor1);
            if (idAutor2Val != null) ps.setInt(4, idAutor2Val);
            else ps.setNull(4, java.sql.Types.INTEGER);

            ps.setString(5, Utils.distintoNulo(genero).trim());
            ps.setString(6, Utils.distintoNulo(categoria).trim());
            ps.setInt(7, idEditorial);
            ps.setInt(8, anyoLectura);
            ps.setInt(9, anyoAdquisicion);
            ps.setBoolean(10, leido);

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                ResultSet rsKeys = ps.getGeneratedKeys();
                if (rsKeys.next()) {
                    generatedId = rsKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error SQL crítico: " + e.getMessage());
            return;
        }

        if (generatedId > 0) {
            Libro nuevoLibro = new Libro(
                    generatedId, titulo.trim(), numPaginas,
                    a1Obj, a2Obj,
                    Utils.distintoNulo(genero).trim(),
                    Utils.distintoNulo(categoria).trim(),
                    edObj, anyoLectura, anyoAdquisicion, leido);

            librosMap.put(generatedId, nuevoLibro);
            System.out.println("¡Libro '" + titulo.trim() + "' añadido correctamente!");
        }
    }

    public static void eliminarLibro(HashMap<Integer, Libro> librosMap, Scanner sc) {
        System.out.println("\n--- ELIMINAR LIBRO ---");

        String tituloABuscar = BibliotecaUI.pedirCadena(sc, "Introduce el título del libro a eliminar: ");
        String tituloNorm = Utils.normalizar(tituloABuscar);

        Libro libroEncontrado = null;
        for (Libro l : librosMap.values()) {
            if (Utils.normalizar(l.getTitulo()).equals(tituloNorm)) {
                libroEncontrado = l;
                break;
            }
        }

        if (libroEncontrado == null) {
            System.err.println("ERROR: No se ha encontrado ningún libro similar a '" + tituloABuscar + "'.");
            return;
        }

        boolean confirmar = BibliotecaUI.pedirBoolean(sc, "¿Estás seguro de que quieres borrar '" + libroEncontrado.getTitulo() + "'?");
        if (!confirmar) {
            System.out.println("Operación cancelada.");
            return;
        }

        String sql = "DELETE FROM libros WHERE ID = ?";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, libroEncontrado.getId());
            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas > 0) {
                librosMap.remove(libroEncontrado.getId());
                System.out.println("¡El libro ha sido eliminado correctamente!");
            } else {
                System.err.println("ERROR: No se pudo borrar de la base de datos.");
            }

        } catch (SQLException e) {
            System.err.println("ERROR SQL crítico al intentar eliminar: " + e.getMessage());
        }
    }
}
