package dam.alumno.filmoteca;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class MOdifyMovieController {
    public TextField idField;
    public TextField titleField;
    public TextField yearField;
    public TextArea descriptionField;
    public Slider ratingSlider;
    public TextField posterURLField;
    public ImageView posterImageView;
    public Label ratingValueLabel;
    private Pelicula selectedMovie;

    public void initialize(Pelicula selectedMovie) {
        this.selectedMovie = selectedMovie;
        ratingSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            ratingValueLabel.setText(String.format("%.1f", newValue));
        });
        idField.setText(String.valueOf(selectedMovie.getId()));
        descriptionField.setWrapText(true);
        idField.setEditable(false);
        titleField.setText(selectedMovie.getTitle());
        yearField.setText(String.valueOf(selectedMovie.getYear()));
        descriptionField.setText(selectedMovie.getDescription());
        ratingSlider.setValue(selectedMovie.getRating());
        posterURLField.setText(selectedMovie.getPoster());
        Image posterImage = new Image(selectedMovie.getPoster());
        posterImageView.setImage(posterImage);
        ratingSlider.setMin(0);
        ratingSlider.setMax(10);
        ratingSlider.setBlockIncrement(1);

    }

    public void onSaveMovie(ActionEvent actionEvent) {
        try {
            selectedMovie.setTitle(titleField.getText());
            selectedMovie.setYear(Integer.parseInt(yearField.getText()));
            selectedMovie.setDescription(descriptionField.getText());
            selectedMovie.setRating((float) ratingSlider.getValue());
            selectedMovie.setPoster(posterURLField.getText());
            DatosFilmoteca.getInstancia().getListaPeliculas().set(
                    DatosFilmoteca.getInstancia().getListaPeliculas().indexOf(selectedMovie), selectedMovie);

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

    public void onPosterURLChanged(KeyEvent keyEvent) {
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
