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

        Scene scene = navigationContext.buildScene(loginScreen.getView());

        stage.setTitle("BusGo Travel");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.setMinWidth(980);
        stage.setMinHeight(720);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}