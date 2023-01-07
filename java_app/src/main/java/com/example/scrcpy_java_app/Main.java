package com.example.scrcpy_java_app;

import java.io.IOException;
import java.util.Objects;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.beans.binding.Bindings;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/main.fxml"));
        VBox box = loader.load();

        Scene scene = new Scene(box, 500, 300);

        Media media;
        MediaPlayer mediaPlayer;
        MediaView mediaView;
        try {
            media = new Media(Objects.requireNonNull(getClass().getResource("/device-2022-09-02-235902.mp4")).toString());
            try {
                mediaPlayer = new MediaPlayer(media);
                mediaPlayer.setAutoPlay(true);
                mediaView = new MediaView(mediaPlayer);

                Pane pane = new Pane(mediaView);
                mediaView.fitWidthProperty().bind(Bindings.selectDouble(mediaView.parentProperty(), "width"));
                mediaView.fitHeightProperty().bind(Bindings.selectDouble(mediaView.parentProperty(), "height"));
                HBox.setHgrow(pane, Priority.SOMETIMES);
                VBox.setVgrow(pane, Priority.SOMETIMES);
                screenPositioningTask(mediaView, scene);

                ((VBox) scene.getRoot()).getChildren().add(0, pane);
            } catch (Exception ignored) {}
        } catch (Exception mediaException) {// Handle exception in Media constructor.
        }

        scene.getRoot().getStylesheets().add(Objects.requireNonNull(
                getClass().getResource("/main.css")).toString());

        stage.setScene(scene);
        stage.setTitle("scrcpy: __test__");
        stage.show();
    }

    private void screenPositioningTask(MediaView mediaView, Scene scene) {
        mediaView.getParent().boundsInParentProperty().addListener((observable, oldValue, newValue) -> {
            mediaView.setTranslateX(
                    (newValue.getWidth() - mediaView.getBoundsInParent().getWidth()) / 2
            );
            if ((newValue.getHeight() - mediaView.getBoundsInParent().getHeight()) > 0) {
                mediaView.setTranslateY(
                        (newValue.getHeight() - mediaView.getBoundsInParent().getHeight()) / 2
                );
            } else {
                mediaView.setTranslateY(0);
            }
        });
        scene.addPostLayoutPulseListener(() -> mediaView.setTranslateX(
                (scene.getWidth() - mediaView.getBoundsInParent().getWidth()) / 2));
    }

    public static void main(String[] args) {
        System.out.println("scrcpy " + "[SCRCPY_VERSION]"
                + " <https://github.com/Genymobile/scrcpy>");
        /*
        DEVELOP.md:136
        app/src/main.c:21
        meson - https://mesonbuild.com/Manual.html , BUILD.md:244 ...
        */
        launch();
    }
}