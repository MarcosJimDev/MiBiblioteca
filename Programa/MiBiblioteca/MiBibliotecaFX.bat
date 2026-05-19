@echo off
chcp 65001 > nul
java --module-path "%~dp0..\..\lib\openjfx-25.0.3_windows-x64_bin-sdk\javafx-sdk-25.0.3\lib" --add-modules javafx.controls -jar "%~dp0MiBiblioteca.jar"
pause
