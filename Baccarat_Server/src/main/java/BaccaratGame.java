import java.util.ArrayList;
import java.text.DecimalFormat;

public class BaccaratGame {

    ArrayList<Card> playerHand;
    ArrayList<Card> bankerHand;
    BaccaratDealer theDealer;
    double currentBet;
    double totalWinnings;
    String betPlacement;
    boolean naturalDraw;

    BaccaratGame() { // default constructor...
        this.playerHand = new ArrayList<Card>();
        this.bankerHand = new ArrayList<Card>();
        this.currentBet = 0;
        this.totalWinnings = 0;
        this.betPlacement = "";
        naturalDraw = false;
    }

    // Tells us how much money the user has won or lost based on the state of the game
    public double evaluateWinnings(){
        String result = naturalDraw ? "Draw" : BaccaratGameLogic.whoWon(playerHand, bankerHand);  // no need to eval if natural draw
        if(!betPlacement.equals(result) ) {
            // player lost
            totalWinnings = -currentBet;
            return totalWinnings;
        }

        // If bet on player, user gets twice their bet
        if(result.equals("Player")) {
            totalWinnings = currentBet * 2;
            return currentBet;
        }
        // If bet on banker, user gets twice their bet, minus 5% for betting on banker
        else if(result.equals("Banker")) {
        	DecimalFormat df = new DecimalFormat("0.00");
            totalWinnings = currentBet * 0.95; // 5% commision
            return Double.parseDouble(df.format(totalWinnings));  // format to two decimal places
        }
        // Draw. Very rare, so big payout
        else {
            totalWinnings = currentBet * 8;
            return totalWinnings;
        }
    }

    // TODO Unused?
    public void closeResources(){
        this.playerHand = new ArrayList<Card>();
        this.bankerHand = new ArrayList<Card>();
        this.currentBet = 0;
        this.totalWinnings = 0;
    }
}
