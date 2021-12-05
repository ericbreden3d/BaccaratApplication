import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class BaccaratTest {

	@Test
	void cardDefConstructor() {
		Card c = new Card();
		assertEquals("", c.suit);
		assertEquals(-1, c.value);
	}
	
	@Test
	void cardParamConstructor() {
		Card c = new Card("s", 12);
		assertEquals("s", c.suit);
		assertEquals(12, c.value);
	}
	
	@Test
	void dealerConstructor() {
		BaccaratDealer d = new BaccaratDealer();
		assertEquals(0, d.deck.size());
	}
	
	@Test
	void generateDeck() {
		BaccaratDealer bd = new BaccaratDealer();
		
		ArrayList<String> deck = new ArrayList<String>();
		for (String suite: Arrays.asList("h", "d", "c", "s") ) {
            for(int i = 1; i <= 13; i++){
                Card tmp = new Card(suite, i);
                deck.add(tmp.suit);
            }
        }
		
		bd.generateDeck();
		
		ArrayList<String> testDeck = new ArrayList<String>();
		for (Card c : bd.deck) {
			testDeck.add(c.suit);
		}
		
		assertArrayEquals(deck.toArray(), testDeck.toArray());
	}
	
	@Test
	void dealerGenerateDeck() {
		BaccaratDealer bd = new BaccaratDealer();
		bd.generateDeck();
		
		ArrayList<Integer> deck = new ArrayList<Integer>();
		for (String suite: Arrays.asList("h", "d", "c", "s") ) {
            for(int i = 1; i <= 13; i++){
                Card tmp = new Card(suite, i);
                deck.add(tmp.value);
            }
        }
		
		ArrayList<Integer> testDeck = new ArrayList<Integer>();
		for (Card c : bd.deck) {
			testDeck.add(c.value);
		}
		
		bd.generateDeck();
		assertArrayEquals(deck.toArray(), testDeck.toArray());
	}
	
	@Test
	void dealerGenerateDeck2() {
		BaccaratDealer bd = new BaccaratDealer();
		
		ArrayList<Card> deck = new ArrayList<Card>();
		for (String suite: Arrays.asList("h", "d", "c", "s") ) {
            for(int i = 1; i <= 13; i++){
                Card tmp = new Card(suite, i);
                deck.add(tmp);
            }
        }
		
		bd.generateDeck();
		assertEquals(deck.size(), bd.deck.size());
	}
	
	@Test
	void dealerDealHand() {
		BaccaratDealer bd = new BaccaratDealer();
		bd.generateDeck();
		
		ArrayList<Card> test = new ArrayList<Card>();
		test.add(new Card("h", 1));
		test.add(new Card("h", 2));
		
		assertEquals(test.get(0).value, bd.dealHand().get(0).value);
		assertEquals(test.get(1).suit, bd.dealHand().get(1).suit);
	}
	
	@Test
	void dealerDealHand2() {
		BaccaratDealer bd = new BaccaratDealer();
		bd.generateDeck();
		bd.shuffleDeck();
		
		ArrayList<Card> test = new ArrayList<Card>();
		test.add(bd.deck.get(0));
		test.add(bd.deck.get(1));
		
		assertArrayEquals(test.toArray(), bd.dealHand().toArray());
	}
	
	@Test
	void dealerDrawOne() {
		BaccaratDealer bd = new BaccaratDealer();
		bd.generateDeck();
		bd.shuffleDeck();
		
		Card c = bd.deck.get(0);
		
		assertEquals(c, bd.drawOne());
	}

	@Test
	void dealerDrawOne2() {
		BaccaratDealer bd = new BaccaratDealer();
		bd.generateDeck();
		bd.shuffleDeck();
		bd.drawOne();
	
		assertEquals(51, bd.deck.size());
	}
	
	@Test
	void dealerDeckSize() {
		BaccaratDealer bd = new BaccaratDealer();	
		assertEquals(0, bd.deckSize());
	}
	
	@Test
	void dealerDeckSize2() {
		BaccaratDealer bd = new BaccaratDealer();
		bd.generateDeck();
		bd.shuffleDeck();
		bd.drawOne();
		bd.drawOne();
		bd.drawOne();
		bd.drawOne();
	
		assertEquals(48, bd.deckSize());
	}
	
	@Test
	void dealerShuffleDeck() {
		BaccaratDealer bd = new BaccaratDealer();
		bd.generateDeck();
		bd.shuffleDeck();
		
		Card c = new Card("h", 1);
		
		assertNotEquals(c, bd.deck.get(0));
	}
	
	@Test
	void dealerShuffleDeck2() {
		BaccaratDealer bd = new BaccaratDealer();
		bd.generateDeck();
		bd.shuffleDeck();
		
		// save card from last shuffle
		Card c = bd.deck.get(0);
		bd.shuffleDeck();
		assertNotEquals(c, bd.deck.get(0));
	}
	
	@Test
	void logicWhoWon() {
		ArrayList<Card> arr1 = new ArrayList<Card>();
		arr1.add(new Card("", 2));
		arr1.add(new Card("", 3));
		
		ArrayList<Card> arr2 = new ArrayList<Card>();
		arr2.add(new Card("", 2));
		arr2.add(new Card("", 3));
		
		assertEquals("Draw", BaccaratGameLogic.whoWon(arr1, arr2));
	}
	
	@Test
	void logicWhoWon2() {
		ArrayList<Card> arr1 = new ArrayList<Card>();
		arr1.add(new Card("", 2));
		arr1.add(new Card("", 4));
		
		ArrayList<Card> arr2 = new ArrayList<Card>();
		arr2.add(new Card("", 2));
		arr2.add(new Card("", 3));
		
		assertEquals("Player", BaccaratGameLogic.whoWon(arr1, arr2));
	}
	
	@Test
	void logicHandTotal() {
		ArrayList<Card> hand = new ArrayList<Card>();
		hand.add(new Card("", 5));
		hand.add(new Card("", 2));
		
		assertEquals(7, BaccaratGameLogic.handTotal(hand));
	}
	
	@Test
	void logicHandTotal2() {
		ArrayList<Card> hand = new ArrayList<Card>();
		hand.add(new Card("", 7));
		hand.add(new Card("", 9));
		
		assertEquals(6, BaccaratGameLogic.handTotal(hand));
	}
	
	@Test
	void logicEvalPlayerDraw() {
		ArrayList<Card> hand = new ArrayList<Card>();
		hand.add(new Card("", 7));
		hand.add(new Card("", 9));
		assertEquals(false, BaccaratGameLogic.evaluatePlayerDraw(hand));
	}
	
	@Test
	void logicEvalPlayerDraw2() {
		ArrayList<Card> hand = new ArrayList<Card>();
		hand.add(new Card("", 5));
		hand.add(new Card("", 9));	
		assertEquals(true, BaccaratGameLogic.evaluatePlayerDraw(hand));
	}
	
	@Test
	void logicEvalBankerDraw() {
		ArrayList<Card> hand = new ArrayList<Card>();
		hand.add(new Card("", 5));
		hand.add(new Card("", 9));
		// banker total 4, no extra player card, should be true
		assertEquals(true, BaccaratGameLogic.evaluateBankerDraw(hand, new Card()));
	}
	
	@Test
	void logicEvalBankerDraw2() {
		ArrayList<Card> hand = new ArrayList<Card>();
		hand.add(new Card("", 4));
		hand.add(new Card("", 9));
		
		//banker total 3, player extra card is 8, should return false
		assertEquals(false, BaccaratGameLogic.evaluateBankerDraw(hand, new Card("", 8)));
	}
	
	@Test
	void gameDefConstructor() {
		BaccaratGame game = new BaccaratGame();
		assertEquals(0, game.playerHand.size());
		assertEquals(0, game.bankerHand.size());
		assertEquals(0, game.currentBet);
		assertEquals(0, game.totalWinnings);
		assertEquals("", game.betPlacement);
		assertEquals(false,game.naturalDraw);
	}
	
	@Test
	void evaluateWinnings() {
		BaccaratGame game = new BaccaratGame();
		
		ArrayList<Card> arr1 = new ArrayList<Card>();
		arr1.add(new Card("", 2));
		arr1.add(new Card("", 4));
		
		ArrayList<Card> arr2 = new ArrayList<Card>();
		arr2.add(new Card("", 2));
		arr2.add(new Card("", 3));
		
		game.playerHand = arr1;
		game.bankerHand = arr2;
		game.currentBet = 5;
		game.betPlacement = "Player";
		
		assertEquals(5, game.evaluateWinnings());
	}
	
	@Test
	void evaluateWinnings2() {
		BaccaratGame game = new BaccaratGame();
		
		ArrayList<Card> arr1 = new ArrayList<Card>();
		arr1.add(new Card("", 2));
		arr1.add(new Card("", 6));
		
		ArrayList<Card> arr2 = new ArrayList<Card>();
		arr2.add(new Card("", 13));
		arr2.add(new Card("", 9));
		
		game.currentBet = 5;
		game.betPlacement = "Draw";
		game.naturalDraw = true;
		
		assertEquals(40, game.evaluateWinnings());
	}
}
