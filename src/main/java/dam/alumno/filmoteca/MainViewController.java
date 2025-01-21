package dam.alumno.filmoteca;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class MainViewController {
    @FXML
    public Button borrarPelicula;
    @FXML
    public Button modificarPelicula;
    @FXML
    public TextField searchField;
    @FXML
    private ListView<Pelicula> peliculasListView;
    @FXML
    private TextField idField;
    @FXML
    private TextField titleField;
    @FXML
    private TextField yearField;
    @FXML
    private TextArea descriptionField;
    @FXML
    private TextField ratingField;
    @FXML
    public ImageView posterImageView;

    @FXML
    public void initialize() {
        peliculasListView.setCellFactory(listView -> new ListCell<Pelicula>() {
            @Override
            protected void updateItem(Pelicula pelicula, boolean empty) {
                super.updateItem(pelicula, empty);
                if (pelicula != null && !empty) {
                    setText("Pelicula -> ID: " + pelicula.getId() + " - " + pelicula.getTitle());
                } else {
                    setText(null);
                }
            }
        });
        ObservableList<Pelicula> listaPeliculas = DatosFilmoteca.getInstancia().getListaPeliculas();
        peliculasListView.setItems(listaPeliculas);

        peliculasListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                borrarPelicula.setDisable(false);
                modificarPelicula.setDisable(false);
                idField.setText(String.valueOf(newValue.getId()));
                titleField.setText(newValue.getTitle());
                yearField.setText(String.valueOf(newValue.getYear()));
                descriptionField.setText(newValue.getDescription());
                ratingField.setText(String.valueOf(newValue.getRating()));
                Image posterImage = new Image(newValue.getPoster());
                posterImageView.setImage(posterImage);
            } else {
                borrarPelicula.setDisable(true);
                modificarPelicula.setDisable(true);
                limpiarCampos();
            }
        });
    }

    @FXML
    private void onAddMovie() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("PeliculaForm.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setTitle("Añadir Nueva Pelicula");
            stage.setScene(scene);
            stage.setWidth(500);
            stage.setHeight(800);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onModifyMovie() {
        Pelicula selectedMovie = peliculasListView.getSelectionModel().getSelectedItem();
        if (selectedMovie != null) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("PeliculaModify.fxml"));
                Stage stage = new Stage();
                stage.setTitle("Modificar Película");
                stage.setScene(new Scene(fxmlLoader.load(), 500, 800));
                MOdifyMovieController controller = fxmlLoader.getController();
                controller.initialize(selectedMovie);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Advertencia");
            alert.setHeaderText("No hay película seleccionada");
            alert.setContentText("Por favor, selecciona una película para modificar.");
            alert.showAndWait();
        }
    }

    @FXML
    private void onDeleteMovie() {
        Pelicula selectedMovie = peliculasListView.getSelectionModel().getSelectedItem();
        if (selectedMovie != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Borrar Película");
            alert.setHeaderText("¿Seguro que quieres borrar esta película?");
            alert.setContentText(selectedMovie.getTitle());
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    DatosFilmoteca.getInstancia().getListaPeliculas().remove(selectedMovie);
                    guardarDatos();
                }
            });
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

    @FXML
    private void onCloseApp() {
        guardarDatos();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cerrar aplicacion");
        alert.setHeaderText("Seguro que quieres salir?");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                System.exit(0);
            }
        });
    }

    public void onSearchMovie(ActionEvent actionEvent) {
        String query = searchField.getText().trim();
        if (!query.isEmpty()) {
            ObservableList<Pelicula> peliculas = peliculasListView.getItems();
            Pelicula matchedMovie = peliculas.stream()
                    .filter(p -> p.getTitle().equalsIgnoreCase(query))
                    .findFirst()
                    .orElse(null);

            if (matchedMovie != null) {
                peliculasListView.getSelectionModel().select(matchedMovie);
                peliculasListView.scrollTo(matchedMovie);
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Busqueda");
                alert.setHeaderText("No se encontro la pelicula");
                alert.setContentText("No hay peliculas con el titulo: " + query);
                alert.show();

                peliculasListView.getSelectionModel().clearSelection();
            }
        } else {
            peliculasListView.getSelectionModel().clearSelection();
        }
    }

    private void limpiarCampos() {
        idField.setText("");
        titleField.setText("");
        yearField.setText("");
        descriptionField.setText("");
        ratingField.setText("");
        posterImageView.setImage(null);
    }
}
