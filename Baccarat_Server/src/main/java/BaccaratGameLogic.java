import java.util.ArrayList;

public class BaccaratGameLogic {
    // Return string of which hand won the game. Usage: hand1 is player, hand2 is banker
    public static String whoWon(ArrayList<Card> hand1, ArrayList<Card> hand2) {
        int tot1 = handTotal(hand1);
        int tot2 = handTotal(hand2);
        // Automatic draw if both totals are equiv. Does not count for 8/9
        Boolean draw = (tot1 == tot2 && (tot1 <= 7 && tot2 <= 7));
        // The player wins if they have 8 or 9. Or, they just have more than the banker. tot1 = 8 vs tot2 = 9, tot2 wins
        Boolean player = (tot1 == 8 || tot1 == 9 || tot1 > tot2) && (tot1 != tot2);
        if(draw) {
            return "Draw";
        } else if(player) {
            return "Player";
        } else {
            // In any other case, the banker wins
            return "Banker";
        }
    }
    // Return the "value" of a given hand. The range of this value is from 0 to 9
    public static int handTotal(ArrayList<Card> hand) {
        int result = 0;
        // for all cards in a hand, accumulate them
        for(Card i : hand) {
            if(i.getCardValue() >= 10){
                // 10's and face cards are of value 0
                continue;
            }
            result += i.getCardValue();
        }
        // Return the value as a mod 10 so that the range is from 0 to 9
        return result % 10;
    }

    // Returns a decision of "if the banker should draw a third card"
    public static boolean evaluateBankerDraw(ArrayList<Card> bankerHand, Card playerCard) {
    	int bTot = handTotal(bankerHand);
    	int pCard = playerCard.value;
    	
    	if (bTot <= 2) 
    		return true;
    	if (bTot == 3 && pCard != 8)
    		return true;
    	if (bTot == 4 && (pCard == -1 || (2 <= pCard && pCard <= 7)))
    		return true;
    	if (bTot == 5 && (pCard == -1 || (4 <= pCard && pCard <= 7)))
    		return true;
    	if (bTot == 6 && (pCard == 6 || pCard == 7))
    		return true;
    	
    	return false;
    }

    // Returns a decision of "if the player should draw a third card"
    public static boolean evaluatePlayerDraw(ArrayList<Card> hand) {
        // Player can only draw a third card if they have a value of 5 or lower
            if(handTotal(hand) <= 5) {
                return true;
            }
            return false;
    }
}
