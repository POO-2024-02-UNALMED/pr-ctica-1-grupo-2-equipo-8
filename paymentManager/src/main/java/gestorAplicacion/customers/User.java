package gestorAplicacion.customers;

import java.util.ArrayList;
import java.util.List;

import gestorAplicacion.plan.Plan;
import gestorAplicacion.plan.Subscription;
import gestorAplicacion.transactions.Card;

public class User extends Customer{

    private List<Card> creditCards;

    public User(String email, String password, DocumentType documentType, String documentId) {
        super(email, password, documentType, documentId);
        creditCards = new ArrayList<Card>();
    }

    public Subscription addSubscription(Plan plan) {;
       if (hasCreditCard()) {
           Subscription suscription = new Subscription("1", "Plan1", "Plan1", 100, 1);
              suscription.procesarPago(this);
            return suscription;
        }
       return null;
    }

    public boolean hasCreditCard() {
        return !creditCards.isEmpty();
    }

    public void addCreditCard(Card card) {
        creditCards.add(card);
    }
    
    public void removeCreditCard(Card card) {
        if (card != null) {
            card.delete();
            creditCards.remove(card);
        }
    }
}
