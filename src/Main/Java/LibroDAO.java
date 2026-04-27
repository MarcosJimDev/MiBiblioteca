package Main.Java;

import java.sql.*;
import java.util.*;
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

                Libro libro = new Libro(rs.getInt("ID"), rs.getString("Titulo"), rs.getInt("NumPaginas"), autor1, autor2, rs.getString("Genero"), rs.getString("Categoria"), editorial, rs.getInt("Anyo_Lectura"), rs.getInt("Anyo_Adquisicion"), rs.getBoolean("Leido"));
                libros.put(libro.getId(), libro);
            }
        } catch (SQLException e) {
            System.out.println("ERROR: se ha producido un error al conectarse a la base de datos " + e);
        }
        return libros;
    }

    public static void agregarNuevoLibro(HashMap<Integer, Libro> librosMap, HashMap<Integer, Autor> autoresMap, HashMap<Integer, Editorial> editorialesMap) {
        System.out.println("\n--- AGREGAR NUEVO LIBRO ---");

        // 1. PEDIR DATOS AL USUARIO
        String titulo = BibliotecaUI.pedirCadena(Utils.declararScaner(), "Título (obligatorio): ");
        if (titulo.trim().isEmpty()) {
            System.err.println("ERROR: El título es obligatorio.");
            return;
        }

        int numPaginas = BibliotecaUI.pedirEntero(Utils.declararScaner(), "Número de páginas: ");
        if (numPaginas <= 0) {
            System.err.println("ERROR: El número de páginas debe ser positivo.");
            return;
        }

        // --- VALIDACIÓN DE AUTOR 1 ---
        String nombreAutor1 = BibliotecaUI.pedirCadena(Utils.declararScaner(), "Nombre Autor Principal (obligatorio): ");
        Autor a1Obj = null;

        // Buscamos el objeto en el mapa para tener ID y Objeto a la vez
        for (Autor a : autoresMap.values()) {
            if (a.getNombre().equalsIgnoreCase(nombreAutor1.trim())) {
                a1Obj = a;
                break;
            }
        }

        if (a1Obj == null) {
            System.err.println("ERROR: El autor '" + nombreAutor1 + "' no existe. Créalo primero.");
            return;
        }
        int idAutor1 = a1Obj.getId(); // Ya tenemos el ID para la DB

        // --- VALIDACIÓN DE AUTOR 2 (OPCIONAL) ---
        String nombreAutor2 = BibliotecaUI.pedirCadena(Utils.declararScaner(), "Nombre Autor Secundario (Enter si no tiene): ");
        Autor a2Obj = null;
        Integer idAutor2Val = null;

        if (!nombreAutor2.isEmpty()) {
            if (nombreAutor2.equalsIgnoreCase(nombreAutor1)) {
                System.err.println("ERROR: El Autor 2 no puede ser el mismo que el Autor 1.");
                return;
            }

            for (Autor a : autoresMap.values()) {
                if (a.getNombre().equalsIgnoreCase(nombreAutor2.trim())) {
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

        // --- VALIDACIÓN DE EDITORIAL ---
        String nombreEditorial = BibliotecaUI.pedirCadena(Utils.declararScaner(), "Nombre Editorial (obligatorio): ");
        Editorial edObj = null;

        for (Editorial e : editorialesMap.values()) {
            if (e.getGrupoEditorial().equalsIgnoreCase(nombreEditorial.trim())) {
                edObj = e;
                break;
            }
        }

        if (edObj == null) {
            System.err.println("ERROR: La editorial '" + nombreEditorial + "' no existe.");
            return;
        }
        int idEditorial = edObj.getId();

        // RESTO DE DATOS
        String genero = BibliotecaUI.pedirCadena(Utils.declararScaner(), "Género: ");
        String categoria = BibliotecaUI.pedirCadena(Utils.declararScaner(), "Categoría: ");
        int anyoAdquisicion = BibliotecaUI.pedirEntero(Utils.declararScaner(), "Año Adquisición (ej. 2026): ");
        int anyoLectura = BibliotecaUI.pedirEntero(Utils.declararScaner(), "Año Lectura (0 si no leído): ");
        boolean leido = BibliotecaUI.pedirBoolean(Utils.declararScaner(), "¿Ya lo has leído? (s/n): ");

        if (leido && anyoLectura == 0) anyoLectura = anyoAdquisicion;
        if (!leido) anyoLectura = 0;

        // 2. AGREGAR A LA DB
        String sqlInsert = "INSERT INTO libros (Titulo, NumPaginas, ID_Autor1, ID_Autor2, Genero, Categoria, ID_Grupo_Editorial, Anyo_Lectura, Anyo_Adquisicion, Leido) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        int generatedId = -1;

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, titulo.trim());
            ps.setInt(2, numPaginas);
            ps.setInt(3, idAutor1);
            if (idAutor2Val != null) ps.setInt(4, idAutor2Val); else ps.setNull(4, Types.INTEGER);
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

        // 3. AGREGAR AL HASHMAP (Sincronización)
        if (generatedId > 0) {
            Libro nuevoLibro = new Libro(
                    generatedId, titulo.trim(), numPaginas,
                    a1Obj, a2Obj, // Usamos los objetos que encontramos al principio
                    Utils.distintoNulo(genero).trim(),
                    Utils.distintoNulo(categoria).trim(),
                    edObj, anyoLectura, anyoAdquisicion, leido
            );

            librosMap.put(generatedId, nuevoLibro);
            System.out.println("¡Libro '" + titulo.trim() + "' añadido correctamente!");
        }
    }

    public static void eliminarLibro(HashMap<Integer, Libro> librosMap) {
        // Usamos el scanner de utilidades para mantener la consistencia
        Scanner sc = Utils.declararScaner();
        System.out.println("\n--- ELIMINAR LIBRO ---");

        // 1. Pedir el título al usuario
        String tituloABuscar = BibliotecaUI.pedirCadena(sc, "Introduce el título exacto del libro a eliminar: ");

        // 2. Buscar el libro en el HashMap para obtener su ID
        Libro libroEncontrado = null;
        for (Libro l : librosMap.values()) {
            if (l.getTitulo().equalsIgnoreCase(tituloABuscar.trim())) {
                libroEncontrado = l;
                break;
            }
        }

        // 3. Validar si existe
        if (libroEncontrado == null) {
            System.err.println("ERROR: No se ha encontrado ningún libro con el título '" + tituloABuscar + "'.");
            return;
        }

        // 4. Confirmación de seguridad
        boolean confirmar = BibliotecaUI.pedirBoolean(sc, "¿Estás seguro de que quieres borrar '" + libroEncontrado.getTitulo() + "'?");
        if (!confirmar) {
            System.out.println("Operación cancelada.");
            return;
        }

        // 5. Borrado en la Base de Datos
        String sql = "DELETE FROM libros WHERE ID = ?";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, libroEncontrado.getId());

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas > 0) {
                // 6. Si se borró en SQL, lo borramos del HashMap (Sincronización)
                librosMap.remove(libroEncontrado.getId());
                System.out.println("¡El libro ha sido eliminado correctamente de la biblioteca!");
            } else {
                System.err.println("ERROR: El libro existe en el programa pero no se pudo borrar de la base de datos.");
            }

        } catch (SQLException e) {
            System.err.println("ERROR SQL crítico al intentar eliminar: " + e.getMessage());
        }
    }

    // --- MÉTODOS PRIVADOS DE COMPROBACIÓN EN LA DB ---

    private static boolean existeAutorEnDB(String nombreAutor) {
        // Cambiamos el WHERE para buscar por la columna Nombre
        String sql = "SELECT COUNT(*) FROM autores WHERE Nombre = ?";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // Usamos setString y aplicamos trim() para limpiar espacios accidentales
            ps.setString(1, nombreAutor.trim());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar existencia del autor '" + nombreAutor + "': " + e.getMessage());
        }
        return false;
    }

    private static boolean existeEditorialEnDB(String nombreEditorial) {
        // Cambiamos el WHERE para buscar por el nombre del grupo editorial
        String sql = "SELECT COUNT(*) FROM editoriales WHERE Grupo_Editorial = ?";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombreEditorial.trim());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar existencia de la editorial '" + nombreEditorial + "': " + e.getMessage());
        }
        return false;
    }

    private static int obtenerIdAutorPorNombre(String nombre) {
        String sql = "SELECT ID FROM autores WHERE Nombre = ?";
        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombre.trim());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("ID");
            }
        } catch (SQLException e) {
            System.err.println("Error al recuperar el ID del autor: " + e.getMessage());
        }
        return -1;
    }
}
