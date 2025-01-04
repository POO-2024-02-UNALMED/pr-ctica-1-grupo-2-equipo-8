package gestorAplicacion.customers;

import java.util.ArrayList;
import java.util.List;

import gestorAplicacion.gateways.Gateway;
import gestorAplicacion.plan.Plan;
import gestorAplicacion.plan.Subscription;
import gestorAplicacion.transactions.Card;

public class User extends Customer {
    private List<Card> creditCards;
    private Gateway gateway;

    public User(String email, String password, DocumentType documentType, String documentId, Gateway gateway) {
        super(email, password, documentType, documentId);
        creditCards = new ArrayList<>();
        this.gateway = gateway;
    }

    public Subscription addSubscription(Plan plan) {
       if (hasCreditCard()) {
           Subscription subscription = new Subscription(this, plan, 1);
              subscription.processPayment(this.gateway);
            return subscription;
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

    public Gateway getGateway() {
        return gateway;
    }
}
