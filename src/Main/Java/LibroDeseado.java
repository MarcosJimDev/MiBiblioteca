package Main.Java;

import Main.Utils.Utils;
import java.util.Objects;

public class LibroDeseado {
    private int id;
    private String titulo;
    private int numPaginas;
    private Autor autor1;
    private Autor autor2;
    private String genero;
    private String categoria;
    private Editorial editorial;
    private String enlaceCompra;

    public LibroDeseado(int id, String titulo, int numPaginas, Autor autor1, Autor autor2, String genero, String categoria, Editorial editorial, String enlaceCompra) {
        this.id = id;
        this.titulo = titulo;
        this.numPaginas = numPaginas;
        this.autor1 = autor1;
        this.autor2 = autor2;
        this.genero = genero;
        this.categoria = categoria;
        this.editorial = editorial;
        this.enlaceCompra = enlaceCompra;
    }

    public void mostrarInfoLibroDeseado() {
        String formato = "%-3d | %-80.80s | %4d | %-27.27s | %-20.20s | %-15.15s | %-30.30s | %-30.30s%n";
        System.out.printf(formato,
                id, titulo, numPaginas,
                nombreCompleto(autor1), nombreCompleto(autor2),
                Utils.distintoNulo(genero), Utils.distintoNulo(categoria),
                editorial != null ? editorial.getGrupoEditorial() : "N/A");
    }

    public void mostrarLibroDeseado() {
        System.out.println("\n\tID: " + id);
        System.out.println("\tTítulo: " + titulo);
        System.out.println("\tNúmero de páginas: " + numPaginas);
        System.out.println("\tNombre autor principal: " + nombreCompleto(autor1));
        if (autor2 != null)
            System.out.println("\tNombre autor secundario: " + nombreCompleto(autor2));
        System.out.println("\tGénero: " + Utils.distintoNulo(genero));
        System.out.println("\tCategoría: " + Utils.distintoNulo(categoria));
        System.out.println("\tGrupo editorial: " + (editorial != null ? editorial.getGrupoEditorial() : "N/A"));
        System.out.println("\tEnlace de compra: " + (enlaceCompra == null || enlaceCompra.isEmpty() ? "N/A" : enlaceCompra));
    }

    public static String nombreCompleto(Autor a) {
        if (a == null) return "N/A";
        return a.getNombre();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        LibroDeseado that = (LibroDeseado) o;
        return Objects.equals(titulo, that.titulo);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(titulo);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public int getNumPaginas() { return numPaginas; }
    public void setNumPaginas(int numPaginas) { this.numPaginas = numPaginas; }

    public Autor getAutor1() { return autor1; }
    public void setAutor1(Autor autor1) { this.autor1 = autor1; }

    public Autor getAutor2() { return autor2; }
    public void setAutor2(Autor autor2) { this.autor2 = autor2; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public Editorial getEditorial() { return editorial; }
    public void setEditorial(Editorial editorial) { this.editorial = editorial; }

    public String getEnlaceCompra() { return enlaceCompra; }
    public void setEnlaceCompra(String enlaceCompra) { this.enlaceCompra = enlaceCompra; }

    @Override
    public String toString() {
        return "LibroDeseado{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", numPaginas=" + numPaginas +
                ", autor1=" + autor1 +
                ", autor2=" + autor2 +
                ", genero='" + genero + '\'' +
                ", categoria='" + categoria + '\'' +
                ", editorial=" + editorial +
                ", enlaceCompra='" + enlaceCompra + '\'' +
                '}';
    }
}
