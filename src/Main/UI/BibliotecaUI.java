package Main.UI;

import Main.Java.Libro;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.sql.*;
import java.util.HashMap;
import java.util.Scanner;

public class BibliotecaUI {
    public static void mostrarMenu() {
        System.out.println("\nElige una opción: ");
        System.out.println("\t1. Mostrar todos los libros.");
        System.out.println("\t2. Mostrar todos los autores.");
        System.out.println("\t3. Mostrar todos las editoriales.");
        System.out.println("\t4. Agregar nuevo libro.");
        System.out.println("\t5. Agregar nuevo autor.");
        System.out.println("\t6. Agregar nueva editorial.");
        System.out.println("\t7. Eliminar libro.");
        System.out.println("\t8. Eliminar autor.");
        System.out.println("\t9. Eliminar editorial.");
        System.out.println("\t0. Salir del programa.");
    }

    public static void mostrarBienvenida(HashMap<Integer, Libro> libros) {
        System.out.println("*** BIENVENIDO A MIBIBLIOTECA ***");
        System.out.println("Actualmente hay un total de " + libros.size() + " libros.");
    }

    public static String pedirCadena(Scanner sc, String mensaje) {
        System.out.print(mensaje);
        return sc.nextLine().trim();
    }

    public static int pedirEntero(Scanner sc, String mensaje) {
        int numero = 0;
        boolean valido = false;

        while (!valido) {
            try {
                System.out.print(mensaje);
                String entrada = sc.nextLine();
                numero = Integer.parseInt(entrada);
                if (numero >= 0)
                    valido = true;
            } catch (NumberFormatException e) {
                System.err.println("ERROR: Debes introducir un número entero válido.");
            }
        }
        return numero;
    }

    public static double pedirDouble(Scanner sc, String mensaje) {
        double numero = 0;
        boolean valido = false;

        while (!valido) {
            try {
                System.out.print(mensaje);
                String entrada = sc.nextLine();
                numero = Double.parseDouble(entrada);
                valido = true;
            } catch (NumberFormatException e) {
                System.err.println("ERROR: Debes introducir un número entero válido.");
            }
        }
        return numero;
    }

    public static boolean pedirBoolean(Scanner sc, String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String entrada = sc.nextLine().trim();

            if (entrada.equalsIgnoreCase("s")) return true;
            if (entrada.equalsIgnoreCase("n")) return false;

            System.out.println("Por favor, introduce 'S' para Sí o 'N' para No.");
        }
    }

    public static Date dateValido(Scanner sc, String mensaje) {
        Date fecha = null;
        String fechaUsuario;
        boolean segundaVuelta = false;
        do {
            if (segundaVuelta) {
                System.out.println(mensaje);
            }
            fechaUsuario = sc.nextLine();
            try {
                DateTimeFormatter formateador = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate fechaLocal = LocalDate.parse(fechaUsuario, formateador);
                fecha = Date.valueOf(fechaLocal);
            } catch (DateTimeParseException e) {
                System.out.println("Formato inválido.");
            }
            segundaVuelta = true;
        } while (fechaUsuario.equalsIgnoreCase("") || fecha == null);

        return fecha;
    }

    public static String campoObligatorio(Scanner sc, String mensaje) {
        String cadena;
        do {
            cadena = sc.nextLine();
            if (cadena.equalsIgnoreCase("")) {
                System.err.println("ERROR: el campo no puede estar vacío.");
                System.out.println(mensaje);
            }
        } while (cadena.equalsIgnoreCase(""));
        return cadena;
    }
}
