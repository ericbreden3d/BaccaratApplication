import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.scene.control.ListView;


public class Server{

    int count = 1;
    ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
    TheServer server;
    private Consumer<Serializable> callback;

    
    double cash;

    Server(Consumer<Serializable> call){

        callback = call;
        server = new TheServer();
        server.start();
    }


    public class TheServer extends Thread{

        public void run() {

            try(ServerSocket mysocket = new ServerSocket(5555);){
                System.out.println("Server is waiting for a client!");

                while(true) {
                    ClientThread c = new ClientThread(mysocket.accept(), count);
                    callback.accept("client has connected to server: " + "client #" + count);
                    clients.add(c);
                    c.start();

                    count++;

                }
            }//end of try
            catch(Exception e) {
                callback.accept("Server socket did not launch");
            }
        }//end of while
    }

    class ClientThread extends Thread{

        Socket connection;
        int count;
        ObjectInputStream in;
        ObjectOutputStream out;
        private BaccaratDealer theDealer;
        private BaccaratGame game;
        private ArrayList<Card> playerHand;
        private ArrayList<Card> bankerHand;

        BaccaratInfo gameInfo;

        ClientThread(Socket s, int count){
            this.connection = s;
            this.count = count;
        }

        public void updateClients(String message) {
            for(int i = 0; i < clients.size(); i++) {
                ClientThread t = clients.get(i);
                try {
                    t.out.writeObject(message);
                }
                catch(Exception e) {}
            }
        }

        public void run(){

            try {
                in = new ObjectInputStream(connection.getInputStream());
                out = new ObjectOutputStream(connection.getOutputStream());
                connection.setTcpNoDelay(true);
            }
            catch(Exception e) {
                System.out.println("Streams not open");
            }

            updateClients("new client on server: client #"+count);

            while(true) {
                try {
                    gameInfo = (BaccaratInfo) in.readObject();
                    callback.accept("client: " + count + " sent: " + gameInfo.client);
                    internals(gameInfo);
                    out.writeObject(gameInfo);
                    updateClients("client #"+count+" said: "+ gameInfo.client);

                }
                catch(Exception e) {
                    callback.accept("Something went wrong. Client: " + count + " connection closed.");
                    updateClients("Client #"+count+" has left the server!");
                    clients.remove(this);
                    e.printStackTrace();
                    break;
                }
            }
        }//end of run

        public void internals(BaccaratInfo pgameInfo) {
            theDealer = new BaccaratDealer();
            game = new BaccaratGame();

            theDealer.generateDeck();
            theDealer.shuffleDeck();

            playerHand = theDealer.dealHand();
            bankerHand = theDealer.dealHand();

            String playerLog = "Player's Hand: " + playerHand.get(0).logCard() + ", " + playerHand.get(1).logCard();
            String bankerLog = "Banker's Hand: " + bankerHand.get(0).logCard() + ", " + bankerHand.get(1).logCard();

            callback.accept("client: " + count + "; player's cards: " + playerLog);
            callback.accept("client: " + count + "; banker's cards: " + bankerLog);

            int pTot = BaccaratGameLogic.handTotal(playerHand);
            int bTot = BaccaratGameLogic.handTotal(bankerHand);
            
            game.playerHand = playerHand;
            game.bankerHand = bankerHand;
            game.currentBet = pgameInfo.bid;

            game.betPlacement = pgameInfo.betPlacement;

            // Natural draw
            if((pTot == 8 || pTot == 9) && (bTot == 8 || bTot == 9)) {
                pgameInfo.winner = "Draw";
                pgameInfo.natural = true;
                pgameInfo.naturalDraw = true;
                game.naturalDraw = true;
                pgameInfo.evalWin = game.evaluateWinnings();
                pgameInfo.wallet += pgameInfo.evalWin;
                callback.accept("client: " + count + "; Natural win: " + pgameInfo.winner + "; $" + pgameInfo.evalWin);
            } // Natural win for player
            else if (pTot == 8 || pTot == 9) {
            	pgameInfo.winner = "Player";
                pgameInfo.natural = true;
                pgameInfo.evalWin = game.evaluateWinnings();
                pgameInfo.wallet += pgameInfo.evalWin;
                callback.accept("client: " + count + "; Natural win: " + pgameInfo.winner + "; $" + pgameInfo.evalWin);
            } // Natural win for banker
            else if (bTot == 8 || bTot == 9) {
                pgameInfo.winner = "Banker";
                pgameInfo.natural = true;
                pgameInfo.evalWin = game.evaluateWinnings();
                pgameInfo.wallet += pgameInfo.evalWin;
                callback.accept("client: " + count + "; Natural win: " + pgameInfo.winner + "; $" + pgameInfo.evalWin);
            } else {
                pgameInfo.natural = false;
                Card extraCard = new Card();
                // Does the player need to draw a third card?
                if(BaccaratGameLogic.evaluatePlayerDraw(playerHand)){
                        playerHand.add(theDealer.drawOne());
                        extraCard = playerHand.get(2);
                        pgameInfo.playerHand = playerHand;
                        game.playerHand = playerHand;
                }
                // Does the banker need to draw a third card?
                if(BaccaratGameLogic.evaluateBankerDraw(bankerHand, extraCard)){
                    bankerHand.add(theDealer.drawOne());
                    pgameInfo.bankerHand = bankerHand;
                    game.bankerHand = bankerHand;
                }
                pgameInfo.winner = BaccaratGameLogic.whoWon(playerHand, bankerHand);
                game.playerHand = playerHand;
                game.bankerHand = bankerHand;
                game.totalWinnings = pgameInfo.bid;
                game.currentBet = pgameInfo.bid;
                pgameInfo.evalWin = game.evaluateWinnings();
                pgameInfo.wallet += pgameInfo.evalWin;
                callback.accept("client: " + count + "; No Natural win: " + pgameInfo.winner + "; $" + pgameInfo.evalWin);
            }
     
            // Update the class object, gameInfo, with player/banker hands etc
            pgameInfo.playerHand = playerHand;
            pgameInfo.bankerHand = bankerHand;
            gameInfo = pgameInfo;
        }

    }//end of client thread
}