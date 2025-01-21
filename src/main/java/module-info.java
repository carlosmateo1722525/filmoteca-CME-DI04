module dam.alumno.filmoteca {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;



    opens dam.alumno.filmoteca to javafx.fxml;
    exports dam.alumno.filmoteca;
}