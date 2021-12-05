import java.io.Serializable;

// Card Object used player hands and core gameplay
// Card Object in client program should be identical
public class Card implements Serializable {
    // private static final long serialVersionUID = 1L;
    // h = heart
    // d = diamond
    // c = club
    // s = spade
    String suit;
    // -1 = unconstructed
    // 1 = ace
    // 2-10 = 2-10
    // 11, 12, 13 = jack queen king
    int value;

    Card() {
        this.suit = "";
        this.value = -1;
    }

    // Construct new card
    Card(String theSuit, int theValue) {
        this.suit = theSuit;
        this.value = theValue;
    }

    String getCardSuite(){
        return suit;
    }

    int getCardValue() {
        return value;
    }
    
    // formatted string based on card value and suit
    String logCard() {
        String tmpsuit = "";
        String tmpVal;
        if(suit == "h") {
            tmpsuit = "Hearts";
        } else if (suit == "d") {
            tmpsuit = "Diamonds";
        } else if(suit == "c") {
            tmpsuit = "Clubs";
        } else if (suit == "s"){
            tmpsuit = "Spades";
        }
        if(value == 1) {
            tmpVal = "Ace";
        } else if(value == 11) {
            tmpVal = "Jack";
        } else if(value == 12) {
            tmpVal = "Queen";
        } else if (value == 13) {
            tmpVal = "King";
        } else {
            tmpVal = String.valueOf(value);
        }
        return tmpVal + " of " + tmpsuit;
    }
}