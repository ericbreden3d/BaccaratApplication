
import java.util.HashMap;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class GuiServer extends Application{

    HashMap<String, Scene> sceneMap;
    GridPane grid;
    VBox buttonBox;
    Scene startScene;
    BorderPane startPane;
    Server serverConnection;
    Text topText;

    ListView<String> listItems;

    Button startButton, exitButton, b1;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // close out
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent E) {
                primaryStage.close();
                Platform.exit();
                System.exit(0);
            }
        });

        primaryStage.setTitle("Baccarat Server GUI");
        
        // Exit Button
        exitButton = new Button("Exit");
        exitButton.setOnAction(e-> {
            primaryStage.close();
            Platform.exit();
            System.exit(0);
        });
        
        // Start Button initializes Server
        startButton = new Button("Start Server");
        startButton.setOnAction(e->{
            primaryStage.setTitle("This is the Server");
            primaryStage.setScene(createServerGui());
            serverConnection = new Server(data -> {
                Platform.runLater(()->{
                    listItems.getItems().add(data.toString());
                });
            });
        });
        
        startButton.setStyle("-fx-font-size: 30px;" + "-fx-font-weight: bold;");
        exitButton.setStyle("-fx-font-size: 30px;"  + "-fx-font-weight: bold;");
        
        buttonBox = new VBox(30, startButton, exitButton);
        buttonBox.setAlignment(Pos.CENTER);
        startPane = new BorderPane();
        startPane.setPadding(new Insets(50));
        startPane.setCenter(buttonBox);
        
        topText = new Text("Welcome to the server");
        topText.setStyle("-fx-font-size: 30px;" + "-fx-font-weight: bold;");
        topText.setTextAlignment(TextAlignment.CENTER);
        startPane.setTop(topText);
        BorderPane.setAlignment(topText, Pos.CENTER);
        
        startScene = new Scene(startPane, 500,500);

        listItems = new ListView<String>();

        primaryStage.setScene(startScene);
        primaryStage.show();
    }

    public Scene createServerGui() {

        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(50));
        pane.setStyle("-fx-background-color: white");

        pane.setCenter(listItems);
        pane.setBottom(exitButton);
        exitButton.setTranslateY(15);

        return new Scene(pane, 500, 500);
    }
}