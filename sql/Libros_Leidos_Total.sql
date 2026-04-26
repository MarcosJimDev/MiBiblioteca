use MiBiblioteca;
SELECT 
    l.ID, 
    l.Titulo, 
    l.NumPaginas, 
    CONCAT(a1.Nombre, ' ', a1.Apellidos) AS Autor_1, 
    CONCAT(a2.Nombre, ' ', a2.Apellidos) AS Autor_2, 
    l.Genero, 
    l.Categoria, 
    e.Grupo_Editorial, 
    e.Firma_Editorial, 
    l.Anyo_Lectura, 
    l.Anyo_Adquisicion, 
    l.Leido, 
    l.Lista_Deseados
FROM libros l
LEFT JOIN autores a1 ON l.ID_Autor1 = a1.ID
LEFT JOIN autores a2 ON l.ID_Autor2 = a2.ID
LEFT JOIN editoriales e ON l.ID_Grupo_Editorial = e.ID
WHERE l.Leido = TRUE;