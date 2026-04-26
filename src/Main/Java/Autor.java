package Main.Java;

import Main.Utils.Utils;

import java.sql.*;

public class Autor {
    private int id;
    private String nombre;
    private String apellido;
    private String nacionalidad;
    private Date fechaNacimiento;

    public Autor(int id, String nombre, String apellido, String nacionalidad, Date fechaNacimiento) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.nacionalidad = nacionalidad;
        this.fechaNacimiento = fechaNacimiento;
    }

    public void mostrarInfoAutor() {
        System.out.println(id + " | " + nombre + " " + Utils.distintoNulo(apellido) + " | " + Utils.distintoNulo(nacionalidad) + " | " + Utils.distintoNulo(fechaNacimiento));
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

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
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
