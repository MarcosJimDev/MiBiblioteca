package Main.Java;

import Main.Utils.Utils;

import java.util.Objects;

public class Libro {
    private int id;
    private String titulo;
    private int numPaginas;
    private Autor autor1;
    private Autor autor2;
    private String genero;
    private String categoria;
    private Editorial editorial;
    private int anyoLectura;
    private int anyoAdquisicion;
    private boolean leido;

    public Libro(int id, String titulo, int numPaginas, Autor autor1, Autor autor2, String genero, String categoria, Editorial editorial, int anyoLectura, int anyoAdquisicion, boolean leido) {
        this.id = id;
        this.titulo = titulo;
        this.numPaginas = numPaginas;
        this.autor1 = autor1;
        this.autor2 = autor2;
        this.genero = genero;
        this.categoria = categoria;
        this.editorial = editorial;
        this.anyoLectura = anyoLectura;
        this.anyoAdquisicion = anyoAdquisicion;
        this.leido = leido;
    }

    public void mostrarInfoLibro() {
        System.out.println(id + " | " + titulo + " | " + numPaginas + " | " + nombreCompleto(autor1) + " | " + nombreCompleto(autor2) + " | " +
                genero + " | " + categoria + " | " + editorial.getGrupoEditorial() + " | " + Utils.distintoNulo(editorial.getFirmaEditorial()) + " | " + anyoLectura + " | " + anyoAdquisicion + " | " + Utils.convertirBooleanAString(leido));
    }

    public static String nombreCompleto(Autor a) {
        if (a == null) return "N/A";
        return a.getNombre();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Libro libro = (Libro) o;
        return Objects.equals(titulo, libro.titulo);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(titulo);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getNumPaginas() {
        return numPaginas;
    }

    public void setNumPaginas(int numPaginas) {
        this.numPaginas = numPaginas;
    }

    public Autor getAutor1() {
        return autor1;
    }

    public void setAutor1(Autor autor1) {
        this.autor1 = autor1;
    }

    public Autor getAutor2() {
        return autor2;
    }

    public void setAutor2(Autor autor2) {
        this.autor2 = autor2;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Editorial getEditorial() {
        return editorial;
    }

    public void setEditorial(Editorial editorial) {
        this.editorial = editorial;
    }

    public int getAnyoLectura() {
        return anyoLectura;
    }

    public void setAnyoLectura(int anyoLectura) {
        this.anyoLectura = anyoLectura;
    }

    public int getAnyoAdquisicion() {
        return anyoAdquisicion;
    }

    public void setAnyoAdquisicion(int anyoAdquisicion) {
        this.anyoAdquisicion = anyoAdquisicion;
    }

    public boolean isLeido() {
        return leido;
    }

    public void setLeido(boolean leido) {
        this.leido = leido;
    }

    @Override
    public String toString() {
        return "Libro{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", numPaginas=" + numPaginas +
                ", autor1=" + autor1 +
                ", autor2=" + autor2 +
                ", genero='" + genero + '\'' +
                ", categoria='" + categoria + '\'' +
                ", editorial=" + editorial +
                ", anyoLectura=" + anyoLectura +
                ", anyoAdquisicion=" + anyoAdquisicion +
                ", leido=" + leido +
                '}';
    }
}
