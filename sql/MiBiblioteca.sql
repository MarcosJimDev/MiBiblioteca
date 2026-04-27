-- 1. Crear la base de datos
drop database if exists MiBiblioteca;
CREATE DATABASE IF NOT EXISTS MiBiblioteca;
USE MiBiblioteca;

-- 2. Crear la tabla de autores (según la imagen proporcionada)
CREATE TABLE autores (
    ID INT NOT NULL AUTO_INCREMENT,
    Nombre VARCHAR(100) NOT NULL,
    Nacionalidad VARCHAR(50),
    Fecha_Nacimiento DATE,
    PRIMARY KEY (ID)
) ENGINE=InnoDB;

-- 3. Crear la tabla de editoriales
CREATE TABLE editoriales (
    ID INT NOT NULL AUTO_INCREMENT,
    Grupo_Editorial VARCHAR(100) NOT NULL,
    Firma_Editorial VARCHAR(100),
    PRIMARY KEY (ID)
) ENGINE=InnoDB;

-- 4. Crear la tabla de libros (vinculando autores y editoriales)
CREATE TABLE libros (
    ID INT NOT NULL AUTO_INCREMENT,
    Titulo VARCHAR(255) NOT NULL,
    NumPaginas INT,
    ID_Autor1 INT NOT NULL,
    ID_Autor2 INT,
    Genero VARCHAR(100),
    Categoria VARCHAR(100),
    ID_Grupo_Editorial INT,
    Anyo_Lectura INT,
    Anyo_Adquisicion INT,
    Leido BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (ID),
    -- Definición de claves foráneas
    CONSTRAINT fk_autor1 FOREIGN KEY (ID_Autor1) REFERENCES autores(ID),
    CONSTRAINT fk_autor2 FOREIGN KEY (ID_Autor2) REFERENCES autores(ID),
    CONSTRAINT fk_editorial FOREIGN KEY (ID_Grupo_Editorial) REFERENCES editoriales(ID)
) ENGINE=InnoDB;

-- 5. Insertar los datos de la tabla autores (transcritos de tu imagen)
-- Nota: MySQL usa formato YYYY-MM-DD para fechas.
INSERT INTO autores (Nombre, Apellidos, Nacionalidad, Fecha_Nacimiento) VALUES
('Desconocido', NULL, NULL, NULL),
('Dani Fontecha', 'Española', NULL),
('Miguel de Cervantes', 'Española', '1547-09-29'),
('César Mallorquí', 'Española', '1953-06-10'),
('Fernando Lalana', 'Española', '1958-02-24'),
('Adrián Redondo de la Casa', 'Española', '2003-12-25'),
('Ricardo Gómez', 'Española', NULL),
('Roberto Santiago', 'Española', '1968-07-05'),
('VEGETTA777', 'Española', '1989-04-12'),
('Willyrex', 'Española', '1993-12-30'),
('Mario Pasqualotto', 'Italiano', NULL),
('Christian Gálvez', 'Española', '1980-05-19'),
('Mino Milani', 'Italiana', NULL),
('Robert Louis Stevenson', 'Escocesa', '1850-11-13'),
('Geronimo Stilton', 'Ratoniano', NULL),
('Marcello Simoni', 'Italiano', '1975-06-27'),
('Víctor Manuel Álvarez', 'Española', NULL),
('José María Zavala', 'Española', NULL),
('Gustavo Adolfo Bécquer', 'Española', '1870-02-17'),
('Ildefonso Falcones', 'Española', '1959-02-02'),
('Arturo Pérez-Reverte', 'Española', '1951-11-25'),
('Robert Kiyosaki', 'Estadounidense', NULL),
('James Ellroy', 'Estadounidense', NULL),
('Michael Connelly', 'Estadounidense', NULL),
('Peter Lehr', '', NULL),
('Verónica Martínez Ama', 'Española', NULL),
('Víctor del Árbol', 'Española', NULL),
('Carlos Ruíz Zafón', 'Española', NULL),
('David Lagercrantz', '', NULL),
('Don Winslow', 'Estadounidense', NULL),
('Eva García Sáenz d', 'Española', NULL),
('George Orwell', 'Británica', '1903-06-25'),
('Raymond Chandler', 'Estadounidense', NULL),
('Ken Follett', 'Británica', '1949-06-05'),
('Jorge Molist', 'Española', NULL),
('Richard Castle', 'Estadounidense', NULL);

INSERT INTO editoriales (Grupo_Editorial, Firma_Editorial) VALUES
('Legisfor', NULL),
('BOE', NULL),
('DEUSTO', null),
('Espasa Calpe S.A', null),
('Edebé',null),
('SM',null),
('Círculo rojo',null),
('Entre líneas editores',null),
('Planeta', 'Temas de hoy'),
('SAU', 'laGalera'),
('Unidad (biblioteca EL MUNDO',null),
('Penguin Random House','Alfaguara'),
('Vicens vives',null),
('Planeta','Destino'),
('Salvat',null),
('Newton Compton Editores',null),
('TriNaranjus',null),
('Penguin Random House','B de bolsillo'),
('Anaya','Cátedra'),
('Penguin Random House','DeBols!llo'),
('Aguilar',null),
('Penguin Random House','Random House'),
('AdN',null),
('Planeta','Crítica'),
('Planeta','Booket'),
('RBA','Serie Negra'),
('SUMA',null);

insert into libros (Titulo, NumPaginas, ID_Autor1, ID_Autor2, 
Genero, Categoria, ID_Grupo_Editorial, Anyo_Lectura, Anyo_Adquisicion, 
Leido) values
('Constitución Española', 99, 1, null, 'No ficción', 'Leyes', 1, 2021, 2021, true),
('Código Penal español', 185, 1, null, 'No ficción', 'Leyes', 1, null, 2021, false),
('Código Civil español', 239, 1, null, 'No ficción', 'Leyes', 1, null, 2021, false),
('Constitución Española', 99, 1, null, 'No ficción', 'Leyes', 2, null, 2021, false),
('Constitución Europea', 174, 1, null, 'No ficción', 'Leyes', 2, null, 2021, false),
('La Constitución explicada superfácil ¡hasta que la entienda hasta tu cuñado!', 
176, 2, null, 'No ficción', 'Sátira', 3, null, 2021, false),
('El ingenioso hidalgo don Quijote de la Mancha', 920, 3, null, 'Narrativa',
'Fantasía / metáfora', 4, null, 2021, false),
('El último trabajo del Señor Luna', 248, 4, null, 'Narrativa', 'Juvenil',
5, null, 2019, false),
('Los diamantes de Oberón', 224, 5, null, 'Narrativa', 'Juvenil', 6, null, 2018, false);