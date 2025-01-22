package gestorAplicacion.customers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import baseDatos.Repository;
import gestorAplicacion.WithId;
import gestorAplicacion.gateways.Gateway;
import gestorAplicacion.plan.Plan;
import gestorAplicacion.plan.Subscription;
import gestorAplicacion.plan.SubscriptionStatus;
import gestorAplicacion.transactions.Card;
import gestorAplicacion.transactions.Transaction;
import gestorAplicacion.transactions.TransactionStatus;

public class User extends Customer {
    private List<Card> creditCards;
    private Gateway gateway;
    private static final long serialVersionUID = 1L;
    // use transient to avoid serialization of this field
    // subscriptions are loaded from disk when needed
    private transient List<Subscription> subscriptions;

    public User(String email, String password, DocumentType documentType, String documentId, Gateway gateway) {
        super(email, password, documentType, documentId);
        creditCards = new ArrayList<>();
        this.gateway = gateway;
    }

    public boolean changeSubcritionPaymentMethod(Subscription subscription, Card card) {
        if (subscription == null) {
            return false;
        }
        subscription.setCard(card);

        Transaction transaction = new Transaction(
            subscription.getPlan().getName(),
            subscription.getUser(),
            1,
            TransactionStatus.PENDING
        );
        subscription.processPayment(transaction, subscription.getGateway());
        return transaction.getStatus() == TransactionStatus.ACCEPTED;
    }

    public Subscription addSubscription(Plan plan) {
        Subscription subscription = new Subscription(this, plan, 1);
       if (hasCreditCard()) {
            subscription.processPayment(this.gateway);
        }

        Repository.save(subscription, "Subscription" + File.separator + subscription.getPlan().getName());

        if (this.subscriptions != null) {
            subscriptions.add(subscription);
        } else {
            this.subscriptions = new ArrayList<>();
            this.subscriptions.add(subscription);
        }

       return subscription;
    }

    public List<Plan> getUserSubscribedPlans() {
        ArrayList<Subscription> useSubscriptions = (ArrayList<Subscription>) getSubscriptions();
        ArrayList<Plan> plans = new ArrayList<>();
        for (Subscription subscription : useSubscriptions) {
            plans.add(subscription.getPlan());
        }
        return plans;
    }

    public List<Subscription> getSubscriptions() {
        if (subscriptions != null) {
            return subscriptions;
        }

        List<Plan> plans = Plan.getAll();
        ArrayList<Subscription> userSubscriptions = new ArrayList<>();
        for (Plan plan : plans) {
            String id = WithId.createId(this.email, plan.getName());
            Subscription subscription = (Subscription) Repository.load(
                "Subscription" + File.separator + plan.getName(),
                id
            );
            if (subscription != null) {
                subscription.setUser(this);
                subscription.setPlan(plan);
                userSubscriptions.add(subscription);
            }
        }
        this.subscriptions = userSubscriptions;

        return userSubscriptions;
    }

    public boolean hasCreditCard() {
        return !creditCards.isEmpty();
    }

    public boolean addCreditCard(Card card) {
        return creditCards.add(card);
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
