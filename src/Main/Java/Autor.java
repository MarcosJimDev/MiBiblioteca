package Main.Java;

import Main.Utils.Utils;

import java.sql.*;

public class Autor {
    private int id;
    private String nombre;
    private String nacionalidad;
    private Date fechaNacimiento;

    public Autor(int id, String nombre, String nacionalidad, Date fechaNacimiento) {
        this.id = id;
        this.nombre = nombre;
        this.nacionalidad = nacionalidad;
        this.fechaNacimiento = fechaNacimiento;
    }

    public void mostrarInfoAutor() {
        // %-3d: ID (3 espacios, alineado a la izquierda)
        // %-25s: Nombre (25 espacios, alineado a la izquierda)
        // %-15s: Nacionalidad (15 espacios)
        System.out.printf("%-3d | %-30s | %-15s | %-12s%n",
                id,
                nombre,
                Utils.distintoNulo(nacionalidad),
                Utils.distintoNulo(fechaNacimiento)
        );
    }

    public void mostrarAutor() {
        System.out.println("\n\tID: " + id);
        System.out.println("\tNombre: " + nombre);
        System.out.println("\tNacionalidad: " + nacionalidad);
        System.out.println("\tFecha de nacimiento: " + fechaNacimiento);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }

    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }
}
