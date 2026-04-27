package Main.Java;

import Main.Utils.Utils;

import java.util.Objects;

public class Editorial {
    private int id;
    private String grupoEditorial;
    private String firmaEditorial;

    public Editorial(int id, String grupoEditorial, String firmaEditorial) {
        this.id = id;
        this.grupoEditorial = grupoEditorial;
        this.firmaEditorial = firmaEditorial;
    }

    public void mostrarInfoEditorial() {
        // %-20s para el grupo y %-20s para la firma
        System.out.printf("%-3d | %-28s | %-20s%n",
                id,
                grupoEditorial,
                Utils.distintoNulo(firmaEditorial)
        );
    }

    public void mostrarEditorial() {
        System.out.println("\n\tID: " + id);
        System.out.println("\tGrupo editorial: " + grupoEditorial);
        System.out.println("\tFirma editorial: " + firmaEditorial);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Editorial editorial = (Editorial) o;
        return Objects.equals(grupoEditorial, editorial.grupoEditorial);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(grupoEditorial);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGrupoEditorial() {
        return grupoEditorial;
    }

    public void setGrupoEditorial(String grupoEditorial) {
        this.grupoEditorial = grupoEditorial;
    }

    public String getFirmaEditorial() {
        return firmaEditorial;
    }

    public void setFirmaEditorial(String firmaEditorial) {
        this.firmaEditorial = firmaEditorial;
    }
}
