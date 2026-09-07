package hr.tvz.projekt;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;


public class HelloApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/hr/tvz/projekt/MainView.fxml")
        );

        Scene scene = new Scene(loader.load(), 1000, 600);

        primaryStage.setTitle("Sustav za evidenciju volontera");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch();
    }
}
