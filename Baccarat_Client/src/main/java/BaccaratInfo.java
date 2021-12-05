import java.util.ArrayList;
import java.io.Serializable;

class BaccaratInfo implements Serializable {
    int client;
    double bid;
    String betPlacement;
    String winner;
    // the prize money won/lost. Value comes from evaluateWinning()
    double evalWin;
    double wallet;
    ArrayList<Card> playerHand;
    ArrayList<Card> bankerHand;
    Boolean natural;
    Boolean naturalDraw;

    BaccaratInfo()
    {
    	client = 0;
        bid = 0.0;
        betPlacement = "";
        winner = "";
        evalWin = 0.0;
        wallet = 0.0;
        playerHand = new ArrayList<>();
        bankerHand = new ArrayList<>();
        natural = false;
        naturalDraw = false;
    }
}
