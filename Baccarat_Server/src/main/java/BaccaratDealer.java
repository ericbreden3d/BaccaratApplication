import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


public class BaccaratDealer {

    ArrayList<Card> deck;

    BaccaratDealer(){
        this.deck = new ArrayList<Card>();
    }

    public void generateDeck() {
        for (String suite: Arrays.asList("h", "d", "c", "s") ) {
            for(int i = 1; i <= 13; i++){
                Card tmp = new Card(suite, i);
                this.deck.add(tmp);
            }
        }
    }

    public void shuffleDeck() {
        Collections.shuffle(this.deck);
    }

    void clear(){
        this.deck.clear();
        generateDeck();
        shuffleDeck();
    }

    public ArrayList<Card> dealHand() {
        ArrayList<Card> hand = new ArrayList<Card>();
        // add card to hand and remove it from deck (x2)
        hand.add(drawOne());
        hand.add(drawOne());
        return hand;
    }

    public Card drawOne() {
        Card result = this.deck.get(0);
        this.deck.remove(0);
        return result;
    }

    public int deckSize() {
        return deck.size();
    }
    
    

}
