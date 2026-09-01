package com.busreservation;

import com.busreservation.ui.LoginScreen;
import com.busreservation.ui.NavigationContext;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {

        NavigationContext navigationContext = new NavigationContext(stage);
        LoginScreen loginScreen = new LoginScreen(stage, navigationContext);

        Scene scene = new Scene(
                loginScreen.getView(),
                1100,
                750
        );

        stage.setTitle("Bus Reservation System");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}