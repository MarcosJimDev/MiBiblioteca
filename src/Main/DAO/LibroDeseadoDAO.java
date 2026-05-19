package Main.DAO;

import java.sql.*;
import java.util.*;

import Main.Java.*;
import Main.UI.BibliotecaUI;
import Main.Utils.Utils;

public class LibroDeseadoDAO {

    public static HashMap<Integer, LibroDeseado> cargarLibrosDeseados(HashMap<Integer, Autor> autores, HashMap<Integer, Editorial> editoriales) {
        HashMap<Integer, LibroDeseado> librosDeseados = new HashMap<>();

        String sql = "select * from librosDeseados";

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

                LibroDeseado ld = new LibroDeseado(rs.getInt("ID"), rs.getString("Titulo"), rs.getInt("NumPaginas"),
                        autor1, autor2, rs.getString("Genero"), rs.getString("Categoria"), editorial,
                        rs.getString("Enlace_Compra"));
                librosDeseados.put(ld.getId(), ld);
            }
        } catch (SQLException e) {
            System.out.println("ERROR: se ha producido un error al conectarse a la base de datos " + e);
        }
        return librosDeseados;
    }

    public static void agregarNuevoLibroDeseado(HashMap<Integer, LibroDeseado> librosDeseadosMap, HashMap<Integer, Autor> autoresMap, HashMap<Integer, Editorial> editorialesMap, Scanner sc) {
        System.out.println("\n--- AGREGAR NUEVO LIBRO DESEADO ---");

        String titulo = BibliotecaUI.campoObligatorio(sc, "Título (obligatorio): ").trim();
        if (Utils.comprobarSalir(titulo))
            return;
        for (LibroDeseado ld : librosDeseadosMap.values()) {
            if (Utils.normalizar(ld.getTitulo()).equalsIgnoreCase(Utils.normalizar(titulo))) {
                System.err.println("ERROR: este libro ya existe en la lista de deseados.");
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
        String enlaceCompra = BibliotecaUI.pedirCadena(sc, "Enlace de compra (opcional): ").trim();

        String sqlInsert = "INSERT INTO librosDeseados (Titulo, NumPaginas, ID_Autor1, ID_Autor2, Genero, Categoria, ID_Grupo_Editorial, Enlace_Compra) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
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
            ps.setString(8, enlaceCompra.isEmpty() ? null : enlaceCompra);

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
            LibroDeseado nuevoLibroDeseado = new LibroDeseado(
                    generatedId, titulo.trim(), numPaginas,
                    a1Obj, a2Obj,
                    Utils.distintoNulo(genero).trim(),
                    Utils.distintoNulo(categoria).trim(),
                    edObj, enlaceCompra);

            librosDeseadosMap.put(generatedId, nuevoLibroDeseado);
            System.out.println("¡Libro deseado '" + titulo.trim() + "' añadido correctamente!");
        }
    }

    public static void eliminarLibroDeseado(HashMap<Integer, LibroDeseado> librosDeseadosMap, HashMap<Integer, Libro> librosMap, HashMap<Integer, Autor> autoresMap, HashMap<Integer, Editorial> editorialesMap, Scanner sc) {
        System.out.println("\n--- ELIMINAR LIBRO DESEADO ---");

        String cadena = BibliotecaUI.pedirCadena(sc, "Introduce el ID del libro deseado que deseas eliminar: ");
        if (Utils.comprobarSalir(cadena))
            return;
        int idLibroDeseado = 0;
        try {
            idLibroDeseado = Integer.parseInt(cadena);
        } catch (NumberFormatException e) {
            System.err.println("ERROR: no has introducido un número válido. " + e);
            return;
        }

        LibroDeseado libroEncontrado = null;
        for (LibroDeseado ld : librosDeseadosMap.values()) {
            if (ld.getId() == idLibroDeseado) {
                libroEncontrado = ld;
                break;
            }
        }

        if (libroEncontrado == null) {
            System.err.println("ERROR: No se ha encontrado ningún libro deseado con ID " + idLibroDeseado + ".");
            return;
        }

        System.out.println("\nLibro deseado seleccionado: " + libroEncontrado.getTitulo());
        System.out.println("\t1. Eliminar permanentemente.");
        System.out.println("\t2. Mover a la lista de libros adquiridos.");
        int opcion = BibliotecaUI.pedirEntero(sc, "Elige una opción: ");

        if (opcion == 1) {
            boolean confirmar = BibliotecaUI.pedirBoolean(sc, "¿Estás seguro de que quieres eliminar permanentemente '" + libroEncontrado.getTitulo() + "'?");
            if (!confirmar) {
                System.out.println("Operación cancelada.");
                return;
            }

            String sql = "DELETE FROM librosDeseados WHERE ID = ?";
            try (Connection con = Conexion.conectar();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, libroEncontrado.getId());
                int filasAfectadas = ps.executeUpdate();
                if (filasAfectadas > 0) {
                    librosDeseadosMap.remove(libroEncontrado.getId());
                    System.out.println("¡El libro deseado ha sido eliminado correctamente!");
                } else {
                    System.err.println("ERROR: No se pudo borrar de la base de datos.");
                }
            } catch (SQLException e) {
                System.err.println("ERROR SQL crítico al intentar eliminar: " + e.getMessage());
            }
        } else if (opcion == 2) {
            for (Libro l : librosMap.values()) {
                if (Utils.normalizar(l.getTitulo()).equalsIgnoreCase(Utils.normalizar(libroEncontrado.getTitulo()))) {
                    System.err.println("ERROR: este libro ya existe en la lista de adquiridos.");
                    return;
                }
            }

            boolean confirmar = BibliotecaUI.pedirBoolean(sc, "¿Estás seguro de que quieres mover '" + libroEncontrado.getTitulo() + "' a la lista de adquiridos?");
            if (!confirmar) {
                System.out.println("Operación cancelada.");
                return;
            }

            int anyoAdquisicion = BibliotecaUI.pedirEntero(sc, "Año de adquisición (ej. 2026): ");
            int anyoLectura = BibliotecaUI.pedirEntero(sc, "Año de lectura (0 si no leído): ");

            String sqlInsert = "INSERT INTO libros (Titulo, NumPaginas, ID_Autor1, ID_Autor2, Genero, Categoria, ID_Grupo_Editorial, Anyo_Lectura, Anyo_Adquisicion) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            int generatedId = -1;

            try (Connection con = Conexion.conectar();
                 PreparedStatement ps = con.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {

                ps.setString(1, libroEncontrado.getTitulo());
                ps.setInt(2, libroEncontrado.getNumPaginas());
                ps.setInt(3, libroEncontrado.getAutor1().getId());
                if (libroEncontrado.getAutor2() != null) ps.setInt(4, libroEncontrado.getAutor2().getId());
                else ps.setNull(4, java.sql.Types.INTEGER);
                ps.setString(5, Utils.distintoNulo(libroEncontrado.getGenero()).trim());
                ps.setString(6, Utils.distintoNulo(libroEncontrado.getCategoria()).trim());
                ps.setInt(7, libroEncontrado.getEditorial().getId());
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
                System.err.println("Error SQL crítico al mover a adquiridos: " + e.getMessage());
                return;
            }

            if (generatedId > 0) {
                String sqlDelete = "DELETE FROM librosDeseados WHERE ID = ?";
                try (Connection con = Conexion.conectar();
                     PreparedStatement ps = con.prepareStatement(sqlDelete)) {
                    ps.setInt(1, libroEncontrado.getId());
                    ps.executeUpdate();
                } catch (SQLException e) {
                    System.err.println("Error SQL al eliminar de deseados: " + e.getMessage());
                }

                Libro nuevoLibro = new Libro(
                        generatedId, libroEncontrado.getTitulo(), libroEncontrado.getNumPaginas(),
                        libroEncontrado.getAutor1(), libroEncontrado.getAutor2(),
                        libroEncontrado.getGenero(), libroEncontrado.getCategoria(),
                        libroEncontrado.getEditorial(), anyoLectura, anyoAdquisicion);

                librosMap.put(generatedId, nuevoLibro);
                librosDeseadosMap.remove(libroEncontrado.getId());
                System.out.println("¡Libro '" + libroEncontrado.getTitulo() + "' movido a la lista de adquiridos correctamente!");
            }
        } else {
            System.out.println("ERROR: opción no válida.");
        }
    }
}
