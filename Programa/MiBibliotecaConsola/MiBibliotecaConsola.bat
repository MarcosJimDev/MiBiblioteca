@echo off
:: Configura la consola para que entienda tildes y eñes
chcp 65001 > nul
:: Lanza tu programa
java -jar "%~dp0MiBibliotecaConsola.jar"
pause