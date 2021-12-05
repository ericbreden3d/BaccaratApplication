
import java.util.ArrayList;
import java.util.HashMap;

import javafx.scene.layout.*;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import java.text.DecimalFormat;
import javafx.scene.text.TextAlignment;


public class ClientGUI extends Application{
    TextField bidField, ip, port;
    Button serverChoice,clientChoice;
    HashMap<String, Scene> sceneMap;
    Scene startScene, gameScene;
    GridPane grid;
    BorderPane bPane, gamePane;
    CustomText2 betInfo, playerCards, playerDrawCard, bankDrawCard, bankCards, natWinText, displayWinner;
    Text ipT, portT;

    BorderPane startPane;
    BaccaratInfo gameInfo = new BaccaratInfo();
    Button bPlayer, bBanker, bDraw, bContinue;
    Button bReplay, bExit, bStart, bSend, bRetry;
    Text instructions;
    double totalMoney = 0;
    static Client clientConnection;
    DecimalFormat df = new DecimalFormat("0.00");
    
    ListView<BaccaratInfo> listItems = new ListView<BaccaratInfo>();

    class CustomText extends Text {
    	CustomText() {
    		this.getStyleClass().add("custom-text");
    	}
    	CustomText(String text) {
    		this.setText(text);
    		this.getStyleClass().add("custom-text");
    	}
    }
    class CustomText2 extends Text {
    	CustomText2() {
    		this.getStyleClass().add("custom-text2");
    	}
    	CustomText2(String text) {
    		this.setText(text);
    		this.getStyleClass().add("custom-text2");
    	}
    }
    
    
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
        
        
        primaryStage.setTitle("Baccarat Game Client GUI");
        
        sceneMap = new HashMap<String, Scene>();

        bContinue = new Button("Continue...");
        bContinue.setDisable(true);

        bExit = new Button("Exit");
        bExit.setOnAction(E->{
            primaryStage.close();
            Platform.exit();
            System.exit(0);
        });
        
        
        bStart = new Button("Connect to Server");
        bStart.setTranslateY(45);
        ipT = new CustomText("IP address:");
        ip = new TextField("127.0.0.1");
        portT = new CustomText("Port");
        port = new TextField("5555");


        bReplay = new Button("Play Again");
        bRetry = new Button("Retry");

        // Replay button is shown at the of the game
        bReplay.setOnAction(E->
        {
            //Resetting all values to nothing...
            gameInfo = new BaccaratInfo();
            listItems = new ListView<BaccaratInfo>();
            clientConnection.gameInfo = new BaccaratInfo();

            primaryStage.setScene(createBidScene());
            primaryStage.show();
        });

        // Retry button is shown at failure to connect
        bRetry.setOnAction(E-> {
        	gameInfo = new BaccaratInfo();
        	clientConnection.gameInfo = gameInfo;
        	bPlayer.setStyle("-fx-background-color: rgb(89, 0, 0);");
        	bBanker.setStyle("-fx-background-color: rgb(89, 0, 0);");
        	bDraw.setStyle("-fx-background-color: rgb(89, 0, 0);");
        	bidField.setText("");
            primaryStage.setScene(sceneMap.get("start"));
        });

        // Start is shown at the start screen and will attempt to make a connection with server
        bStart.setOnAction(E->
        {
            primaryStage.setScene(sceneMap.get("client"));
            primaryStage.setTitle("Client GUI");
            try {
                // make a new client and pass along gameInfo and IP/Port
                clientConnection = new Client(data ->
                {
                    gameInfo = clientConnection.gameInfo;
                }, ip.getText(), Integer.valueOf(port.getText()));
                clientConnection.start();
            } catch (Exception e) {
                // If we fail to make connection, show failure scene
                primaryStage.setScene(sceneMap.get("fail"));
            }
        });

        Pane startPane = new Pane();
        startPane.setPadding(new Insets(50));
        
        bPane = new BorderPane();
        
        ImageView title = new ImageView(new Image("Images/title.png"));
        title.setFitHeight(300);
        title.setFitWidth(300);
        title.setTranslateY(-50);
        bPane.setTop(title);
        BorderPane.setAlignment(title, Pos.CENTER);

        bSend = new Button("Place Bid");
        
        // Send button is shown at bid scene. Will send information and embeds the game sequence
        bSend.setOnAction(e->
        {
        	// need bid and bid placement
        	if (bidField.getText().length() == 0 || gameInfo.betPlacement.length() == 0) {
        		return;
        	}
        	
            Boolean flag = true;
            bExit.setDisable(true);
            bContinue.setDisable(true);
            playerCards = new CustomText2("");
            bankCards = new CustomText2("");
            natWinText = new CustomText2("");
            // third card, if any
            playerDrawCard = new CustomText2("");
            // third card, if any
            bankDrawCard = new CustomText2("");
            displayWinner = new CustomText2("");
            // Send the user's balance inside gameinfo
            gameInfo.wallet = totalMoney;
            // From the textfield, update gameinfo's bid for server to process
            gameInfo.bid = Double.parseDouble(bidField.getText());
            
            try {
                clientConnection.send(gameInfo);
            } catch (Exception E) {
                // We've somehow failed to send gameinfo. Possible that the server has shut down
                bPlayer.setDisable(false);
                bBanker.setDisable(false);
                bDraw.setDisable(false);
                // set flag so that later events do not take place
                flag = false;
                bExit.setDisable(false);
                primaryStage.setScene(sceneMap.get("fail"));
                return;
            }
                        
            betInfo = new CustomText2("Player bet $" + df.format(gameInfo.bid) + " on " + gameInfo.betPlacement);

            // Setup sequence. All setOnFinished must call the processing play() function to be sequencial
            Double pauseDir = 1.0;
            PauseTransition showPlayer = new PauseTransition(Duration.seconds(pauseDir));
            PauseTransition showBanker = new PauseTransition(Duration.seconds(pauseDir));
            PauseTransition naturalPause = new PauseTransition(Duration.seconds(pauseDir));
            PauseTransition showPlayerAux = new PauseTransition(Duration.seconds(pauseDir));
            PauseTransition showBankerAux = new PauseTransition(Duration.seconds(pauseDir));
            PauseTransition winnerPause = new PauseTransition(Duration.seconds(pauseDir));
            
            
            HBox pCardBox = new HBox(35);
            HBox bCardBox = new HBox(35);
          
            showPlayer.setOnFinished(E ->
            {
                playerCards.setText("Player hand: " + gameInfo.playerHand.get(0).logCard() + ", "
                        + gameInfo.playerHand.get(1).logCard() );
                createCardBox(pCardBox, gameInfo.playerHand, 2);
                showBanker.play();
            });

            showBanker.setOnFinished(E ->
            {
                bankCards.setText("Banker hand: " + gameInfo.bankerHand.get(0).logCard() + ", "
                        + gameInfo.bankerHand.get(1).logCard());
                createCardBox(bCardBox, gameInfo.bankerHand, 2);
                naturalPause.play();
            });

            naturalPause.setOnFinished(E ->
            {
                if(gameInfo.natural == true) {
                	if (gameInfo.naturalDraw == true) {
                		natWinText.setText("Player and Banker both have natural wins: Draw");
                	} else {
                		natWinText.setText("There is a natural win!");
                	}
                } else {
                    natWinText.setText("No natural win!");
                }
                showPlayerAux.play();
            });

            showPlayerAux.setOnFinished(E ->
            {
                if(gameInfo.playerHand.size() == 3) {
                    playerDrawCard.setText("Player extra card: " + gameInfo.playerHand.get(2).logCard());
                    createCardBox(pCardBox, gameInfo.playerHand, 1);
                } else {
                    playerDrawCard.setText("Player did not draw extra card");
                }
                showBankerAux.play();
            });

            showBankerAux.setOnFinished(E ->
            {
                if(gameInfo.bankerHand.size() == 3) {
                    bankDrawCard.setText("Banker extra card: " + gameInfo.bankerHand.get(2).logCard());
                    createCardBox(bCardBox, gameInfo.bankerHand, 1);
                } else {
                    bankDrawCard.setText("Banker did not draw extra card");
                    
                }
                winnerPause.play();
            });

            winnerPause.setOnFinished(E ->
            {
                displayWinner.setText("Winner: " + gameInfo.winner);
                bContinue.setDisable(false);
                bExit.setDisable(false);
            });

            // Do not pass go if the server is unreachable
            if(flag) {
                showPlayer.play();
            }
            gamePane = new BorderPane();
            gamePane.setPadding(new Insets(20));
            gamePane.getStyleClass().add("border");            
            
            VBox v = new VBox(15, betInfo, playerCards, pCardBox, bankCards, bCardBox, natWinText, playerDrawCard, bankDrawCard, displayWinner);
            VBox menuBox = new VBox(15, bContinue);
            bContinue.setTranslateY(-50);
            gamePane.setCenter(v);
            gamePane.setBottom(menuBox);
            Scene gameScene = new Scene(gamePane, 500, 700);
            gameScene.getStylesheets().add("CSS/baccarat.css");
            if(flag) {
                primaryStage.setScene(gameScene);
            }
            primaryStage.show();
        });

        // Continue button is shown after the game sequence
        bContinue.setOnAction(E ->{
            primaryStage.setScene(createEndScene());
            primaryStage.show();
        });

        
        VBox v = new VBox(10);
        v.getChildren().addAll(ipT, ip, portT, port, bStart);
        startPane.getChildren().add(v);
        v.setAlignment(Pos.CENTER);
        v.setTranslateY(-100);
        
        bPane.setPadding(new Insets(50));
        bPane.setCenter(v);
        bPane.getStyleClass().add("border");
        startScene = new Scene(bPane, 500, 700);
        startScene.getStylesheets().add("CSS/baccarat.css");

        sceneMap.put("client",  createBidScene());
        sceneMap.put("fail",  createFailScene());
        sceneMap.put("start", startScene);

        primaryStage.setScene(startScene);
        primaryStage.show();
    }

    // Bid scene is where the user bets on Player/Banker and enters bid amount
    public Scene createBidScene() {

        BorderPane bp = new BorderPane();
        bp.getStyleClass().add("border");
        
        bp.setPadding(new Insets(50));
        
        VBox v = new VBox(15);
        
        bidField = new TextField();
        bidField.getStyleClass().add("bid-field");
        bidField.setMinSize(200, 100);
        bidField.setPromptText("Bid");
        
        bPlayer = new Button("Player");
        bPlayer.setDisable(false);

        bBanker = new Button("Banker");
        bBanker.setDisable(false);

        bDraw = new Button("Draw");
        bDraw.setDisable(false);

        //Event handler for Player....
        bPlayer.setOnAction(e ->
        {
        	bPlayer.setStyle("-fx-background-color: black;");
            gameInfo.betPlacement = "Player";
            bBanker.setStyle("-fx-background-color: rgb(89, 0, 0);");
            bDraw.setStyle("-fx-background-color: rgb(89, 0, 0);");
        });

        bBanker.setOnAction(e ->
        {
        	bBanker.setStyle("-fx-background-color: black;");
            gameInfo.betPlacement = "Banker";
            bPlayer.setStyle("-fx-background-color: rgb(89, 0, 0);");
            bDraw.setStyle("-fx-background-color: rgb(89, 0, 0);");
        });

        bDraw.setOnAction(e ->
        {
        	bDraw.setStyle("-fx-background-color: black;");
            gameInfo.betPlacement = "Draw";
            bPlayer.setStyle("-fx-background-color: rgb(89, 0, 0);");
            bBanker.setStyle("-fx-background-color: rgb(89, 0, 0);");
        });
        
        CustomText wallet = new CustomText("Wallet: $" + df.format(totalMoney));
        wallet.setTranslateY(-50);
        v.getChildren().addAll(wallet, bPlayer, bBanker, bDraw);
        v.setTranslateY(-20);
        wallet.setTranslateY(-27.5);
        v.setAlignment(Pos.CENTER);
        
        Text dollar = new Text("$");
        dollar.setStyle("-fx-font-size: 100px;");
        dollar.setTranslateY(-27);
        HBox bidBox = new HBox(30, dollar, bidField);
        bidBox.setTranslateY(30);
        bidBox.setTranslateX(19);
        
        instructions = new Text("Select bid amount and bid placement, then place bid!");
        instructions.setTranslateY(10);
        instructions.getStyleClass().add("instructions");
        VBox placeBidBox = new VBox(bSend, instructions);
        placeBidBox.setAlignment(Pos.CENTER);
                
        bp.setCenter(v);
        bp.setTop(bidBox);
        bp.setBottom(placeBidBox);
        BorderPane.setAlignment(bidBox, Pos.CENTER);
        BorderPane.setAlignment(placeBidBox, Pos.CENTER);
        bSend.getStyleClass().add("bid-button");
        bSend.setTranslateY(-20);
        
        Scene bidScene = new Scene(bp, 500, 700);
        bidScene.getStylesheets().add("CSS/baccarat.css");
        return bidScene;
    }
    
    public void createCardBox(HBox cardBox, ArrayList<Card> hand, int amount) {
    	int low, cap;
    	if (amount == 2) {
    		low = 0;
    		cap = 2;
    	} else {
    		low = 2;
    		cap = 3;
    	}
    	for (int i = low; i < cap; i++) {
    		ImageView im = new ImageView(new Image("Images/" + hand.get(i).suit + "/" + Integer.valueOf(hand.get(i).value) + ".png"));
    		im.setFitHeight(100);
    		im.setFitWidth(80);
    		cardBox.getChildren().add(im);
    	}
    }

    // End scene shows how much the user won/lost and asks if they want to play again
    public Scene createEndScene()
    {
        BorderPane endPane = new BorderPane();
        endPane.getStyleClass().add("border");
        
        CustomText moneyWin = new CustomText();
        moneyWin.setTextAlignment(TextAlignment.CENTER);
        moneyWin.setStyle("-fx-font-size: 25;" + "-fx-font-weight: bold;");
        
        VBox v = new VBox(20);
        v.setAlignment(Pos.CENTER);
        v.getChildren().add(moneyWin);
        
        BorderPane.setAlignment(v, Pos.CENTER);

        totalMoney = gameInfo.wallet;

        if (gameInfo.betPlacement.equals(gameInfo.winner)) {
            moneyWin.setText(
                    "You won! Bet on " + gameInfo.betPlacement
                            + " paid $" + df.format(Math.abs(gameInfo.evalWin)) + ",\n"
                            + "Wallet: $" + df.format(totalMoney));
        } else {
            moneyWin.setText("You bet on " + gameInfo.betPlacement
                    + " and lost $" + df.format(Math.abs(gameInfo.evalWin)) + ",\n"
                    + "Wallet:  $ " + df.format(totalMoney));
        }
        
        bReplay.setTranslateY(20);
        bExit.setTranslateY(20);
        
        v.getChildren().add(bReplay);
        v.getChildren().add(bExit);
        v.setAlignment(Pos.CENTER);
        endPane.setCenter(v);
        
        Scene endScene = new Scene(endPane, 500, 700);
        endScene.getStylesheets().add("CSS/baccarat.css");
        
        return endScene;
    }

    // Fail scene for when app is unable to connect with server
    public Scene createFailScene(){
        BorderPane failPane = new BorderPane();
        failPane.getStyleClass().add("border");
        VBox v = new VBox(20);
        v.setAlignment(Pos.CENTER);
        CustomText fail = new CustomText("Failed to connect to server");
        v.getChildren().addAll(fail, bRetry, bExit);
        failPane.setCenter(v);   
        Scene failScene = new Scene(failPane, 500, 700);
        failScene.getStylesheets().add("CSS/baccarat.css");
        return failScene;
    }

}
