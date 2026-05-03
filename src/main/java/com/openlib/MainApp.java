package com.openlib;

import com.openlib.util.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        SceneManager.getInstance().init(primaryStage);
        SceneManager.getInstance().showLogin();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
