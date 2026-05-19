drop database if exists MiBiblioteca;
CREATE DATABASE IF NOT EXISTS MiBiblioteca;
USE MiBiblioteca;

CREATE TABLE autores (
    ID INT NOT NULL AUTO_INCREMENT,
    Nombre VARCHAR(100) NOT NULL,
    Nacionalidad VARCHAR(50),
    Fecha_Nacimiento DATE,
    PRIMARY KEY (ID)
) ENGINE=InnoDB;

CREATE TABLE editoriales (
    ID INT NOT NULL AUTO_INCREMENT,
    Grupo_Editorial VARCHAR(100) NOT NULL,
    Firma_Editorial VARCHAR(100),
    PRIMARY KEY (ID)
) ENGINE=InnoDB;

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
    PRIMARY KEY (ID),
    -- Definición de claves foráneas
    CONSTRAINT fk_autor1 FOREIGN KEY (ID_Autor1) REFERENCES autores(ID),
    CONSTRAINT fk_autor2 FOREIGN KEY (ID_Autor2) REFERENCES autores(ID),
    CONSTRAINT fk_editorial FOREIGN KEY (ID_Grupo_Editorial) REFERENCES editoriales(ID)
) ENGINE=InnoDB;

CREATE TABLE librosDeseados (
    ID INT NOT NULL AUTO_INCREMENT,
    Titulo VARCHAR(255) NOT NULL,
    NumPaginas INT,
    ID_Autor1 INT NOT NULL,
    ID_Autor2 INT,
    Genero VARCHAR(100),
    Categoria VARCHAR(100),
    ID_Grupo_Editorial INT,
    Enlace_Compra varchar(2048),
    PRIMARY KEY (ID),
    -- Definición de claves foráneas
    CONSTRAINT fk_autorDeseado1 FOREIGN KEY (ID_Autor1) REFERENCES autores(ID),
    CONSTRAINT fk_autorDeseado2 FOREIGN KEY (ID_Autor2) REFERENCES autores(ID),
    CONSTRAINT fk_editorialDeseada FOREIGN KEY (ID_Grupo_Editorial) REFERENCES editoriales(ID)
) ENGINE=InnoDB;