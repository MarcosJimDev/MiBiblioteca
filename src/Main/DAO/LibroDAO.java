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

                // Se elimina el rs.getBoolean("Leido") del constructor
                Libro libro = new Libro(rs.getInt("ID"), rs.getString("Titulo"), rs.getInt("NumPaginas"), autor1,
                        autor2, rs.getString("Genero"), rs.getString("Categoria"), editorial, rs.getInt("Anyo_Lectura"),
                        rs.getInt("Anyo_Adquisicion"));
                libros.put(libro.getId(), libro);
            }
        } catch (SQLException e) {
            System.out.println("ERROR: se ha producido un error al conectarse a la base de datos " + e);
        }
        return libros;
    }

    public static void agregarNuevoLibro(HashMap<Integer, Libro> librosMap, HashMap<Integer, Autor> autoresMap, HashMap<Integer, Editorial> editorialesMap, Scanner sc) {
        System.out.println("\n--- AGREGAR NUEVO LIBRO ---");

        String titulo = BibliotecaUI.campoObligatorio(sc, "Título (obligatorio): ").trim();
        if (Utils.comprobarSalir(titulo))
            return;
        for (Libro l : librosMap.values()) {
            if (Utils.normalizar(l.getTitulo()).equalsIgnoreCase(Utils.normalizar(titulo))) {
                System.err.println("ERROR: este libro ya existe en la base de datos.");
                return;
            }
        }

        int numPaginas = BibliotecaUI.pedirEntero(sc, "Número de páginas: ");

        String nombreAutor1 = BibliotecaUI.campoObligatorio(sc, "Nombre Autor Principal (obligatorio): ").trim();
        String autor1Norm = Utils.normalizar(nombreAutor1);
        if (Utils.comprobarSalir(autor1Norm))
            return;
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

        String nombreAutor2 = BibliotecaUI.pedirCadena(sc, "Nombre Autor Secundario (Enter si no tiene): ").trim();
        Autor a2Obj = null;
        Integer idAutor2Val = null;

        if (!nombreAutor2.isEmpty()) {
            String autor2Norm = Utils.normalizar(nombreAutor2);
            if (Utils.comprobarSalir(autor2Norm))
                return;
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

        String nombreEditorial = BibliotecaUI.campoObligatorio(sc, "Nombre Editorial (obligatorio): ").trim();
        String editorialNorm = Utils.normalizar(nombreEditorial);
        if (Utils.comprobarSalir(editorialNorm))
            return;
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

        String genero = BibliotecaUI.pedirCadena(sc, "Género: ").trim();
        if (Utils.comprobarSalir(genero))
            return;
        String categoria = BibliotecaUI.pedirCadena(sc, "Categoría: ").trim();
        if (Utils.comprobarSalir(categoria))
            return;
        int anyoAdquisicion = BibliotecaUI.pedirEntero(sc, "Año Adquisición (ej. 2026): ");
        int anyoLectura = BibliotecaUI.pedirEntero(sc, "Año Lectura (0 si no leído): ");

        // Se elimina la lógica que dependía de la variable boolean 'leido'
        // El SQL ya no incluye la columna Leido ni el décimo parámetro ?
        String sqlInsert = "INSERT INTO libros (Titulo, NumPaginas, ID_Autor1, ID_Autor2, Genero, Categoria, ID_Grupo_Editorial, Anyo_Lectura, Anyo_Adquisicion) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
            // Se actualiza el constructor de Libro quitando el parámetro boolean
            Libro nuevoLibro = new Libro(
                    generatedId, titulo.trim(), numPaginas,
                    a1Obj, a2Obj,
                    Utils.distintoNulo(genero).trim(),
                    Utils.distintoNulo(categoria).trim(),
                    edObj, anyoLectura, anyoAdquisicion);

            librosMap.put(generatedId, nuevoLibro);
            System.out.println("¡Libro '" + titulo.trim() + "' añadido correctamente!");
        }
    }

    public static void eliminarLibro(HashMap<Integer, Libro> librosMap, Scanner sc) {
        System.out.println("\n--- ELIMINAR LIBRO ---");

        String cadena = BibliotecaUI.pedirCadena(sc, "Introduce el ID del libro que deseas eliminar: ");
        if (Utils.comprobarSalir(cadena))
            return;
        int idLibro = 0;
        try {
            idLibro = Integer.parseInt(cadena);
        } catch (NumberFormatException e) {
            System.err.println("ERROR: no has introducido un número válido. " + e);
            return;
        }

        Libro libroEncontrado = null;
        for (Libro l : librosMap.values()) {
            if (l.getId() == idLibro) {
                libroEncontrado = l;
                break;
            }
        }

        if (libroEncontrado == null) {
            System.err.println("ERROR: No se ha encontrado ningún libro con ID " + idLibro + ".");
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

    public static void actualizarAnyoLectura(HashMap<Integer, Libro> librosMap, Scanner sc) {
        System.out.println("\n--- MARCAR / EDITAR AÑO DE LECTURA ---");

        // 1. Pedir el ID del libro
        int idBusqueda = BibliotecaUI.pedirEntero(sc, "Introduce el ID del libro que deseas editar: ");

        // 2. Comprobar si existe en el HashMap
        if (!librosMap.containsKey(idBusqueda)) {
            System.err.println("ERROR: No existe ningún libro con el ID " + idBusqueda + " en el sistema.");
            return;
        }

        Libro libroAEditar = librosMap.get(idBusqueda);
        System.out.println("Libro seleccionado: " + libroAEditar.getTitulo());
        System.out.println("Año de lectura actual: " + (libroAEditar.getAnyoLectura() == 0 ? "No leído" : libroAEditar.getAnyoLectura()));

        // 3. Pedir el nuevo año
        int nuevoAnyo = BibliotecaUI.pedirEntero(sc, "Introduce el nuevo año de lectura (0 si quieres marcarlo como NO leído): ");

        // 4. Actualizar en la Base de Datos
        String sql = "UPDATE libros SET Anyo_Lectura = ? WHERE ID = ?";

        try (Connection con = Conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, nuevoAnyo);
            ps.setInt(2, idBusqueda);

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas > 0) {
                // 5. Sincronizar con el HashMap (Memoria)
                libroAEditar.setAnyoLectura(nuevoAnyo);
                System.out.println("¡Base de datos y biblioteca local actualizadas correctamente!");
                if (nuevoAnyo > 0) {
                    System.out.println("Estado actual: Leído en el año " + nuevoAnyo);
                } else {
                    System.out.println("Estado actual: Marcado como NO leído.");
                }
            } else {
                System.err.println("ERROR: No se pudo actualizar el registro en la base de datos.");
            }

        } catch (SQLException e) {
            System.err.println("ERROR SQL crítico al actualizar el año: " + e.getMessage());
        }
    }
}