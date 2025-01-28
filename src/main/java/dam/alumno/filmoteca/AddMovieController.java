package dam.alumno.filmoteca;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class AddMovieController {
    @FXML
    public TextField titleField;
    @FXML
    public TextField yearField;
    @FXML
    public TextField idField;
    @FXML
    public TextArea descriptionFieldx;
    @FXML
    public Slider ratingSlider;
    @FXML
    public TextField posterURLField;
    @FXML
    public ImageView posterImageView;
    @FXML
    public Label ratingValueLabel;

    @FXML
    public void initialize() {
        int newId = 1;
        if (!DatosFilmoteca.getInstancia().getListaPeliculas().isEmpty()) {
            newId = DatosFilmoteca.getInstancia().getListaPeliculas().stream()
                    .mapToInt(Pelicula::getId)
                    .max()
                    .orElse(0) + 1;
        }

        idField.setText(String.valueOf(newId));
        descriptionFieldx.setWrapText(true);
        idField.setEditable(false);
        ratingSlider.setMin(0);
        ratingSlider.setMax(10);
        posterImageView.setImage(null);
        ratingSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            ratingValueLabel.setText(String.format("%.1f", newValue));
        });
    }

    public void onSaveMovie(ActionEvent actionEvent) {
        try {
            Pelicula nuevaPelicula = new Pelicula(
                    Integer.parseInt(idField.getText()),
                    titleField.getText(),
                    Integer.parseInt(yearField.getText()),
                    descriptionFieldx.getText(),
                    (float) ratingSlider.getValue(),
                    posterURLField.getText()
            );

            DatosFilmoteca.getInstancia().getListaPeliculas().add(nuevaPelicula);

            guardarDatos();
            Stage stage = (Stage) idField.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Datos invalidos");
            alert.setContentText("Por favor, asegurate de que todos los campos estan completos correctamente o ha introducido un numero en año");
            alert.showAndWait();
        }
    }

    public void onCancel(ActionEvent actionEvent) {
        Stage stage = (Stage) idField.getScene().getWindow();
        stage.close();
    }

    public void onPosterURLChanged() {
        String url = posterURLField.getText();
        try {
            Image posterImage = new Image(url);
            posterImageView.setImage(posterImage);
        } catch (Exception e) {
            posterImageView.setImage(null);
        }
    }

    private void guardarDatos() {
        ObservableList<Pelicula> listaPeliculas = DatosFilmoteca.getInstancia().getListaPeliculas();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            objectMapper.writeValue(new File("datos/peliculas.json"), listaPeliculas);
            System.out.println("Datos actualizados y guardados correctamente.");
        } catch (IOException e) {
            System.out.println("ERROR al guardar los datos en el archivo JSON.");
            e.printStackTrace();
        }
    }
}
