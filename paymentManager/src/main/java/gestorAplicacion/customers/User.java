package gestorAplicacion.customers;

import java.io.File;
import java.time.LocalDate;
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

    public boolean changeSubscriptionPaymentMethod(Subscription subscription, Card card) {
        if (subscription == null) {
            return false;
        }
        subscription.setPaymentMethod(card);

        Transaction transaction = new Transaction(
            subscription.getPlan().getName(),
            subscription.getUser(),
            1,
            TransactionStatus.PENDING
        );
        subscription.processPayment(transaction, subscription.getGateway());
        return transaction.getStatus() == TransactionStatus.ACCEPTED;
    }

    private void saveOnRepositoryAndAddToSubscriptions(Subscription subscription) {
        Repository.save(subscription, "Subscription" + File.separator + subscription.getPlan().getName());
        if (this.subscriptions != null) {
            subscriptions.add(subscription);
        } else {
            this.subscriptions = new ArrayList<>();
            this.subscriptions.add(subscription);
        }
    }

    public Transaction addSubscription(Plan plan) {
        Subscription subscription = new Subscription(this, plan);
        Transaction initialChargeTransaction = null;
       if (hasCreditCard()) {
            initialChargeTransaction = subscription.processPayment(this.gateway);
        }
        saveOnRepositoryAndAddToSubscriptions(subscription);
       return initialChargeTransaction;
    }

    public Transaction addSubscription(Plan plan, Card card) {
        Subscription subscription = new Subscription(this, plan, card);
        Transaction initialChargeTransaction = subscription.processPayment(this.gateway);
        saveOnRepositoryAndAddToSubscriptions(subscription);
       return initialChargeTransaction;
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
                if(subscription.getNextChargeDate().isBefore(LocalDate.now())) {
                    subscription.setStatus(SubscriptionStatus.CANCELLED);
                    subscription.setSuspensionDate(subscription.getNextChargeDate());
                    subscription.setNextChargeDate(LocalDate.MIN);
                    Repository.update(subscription, "Subscription" + File.separator + plan.getName());
                }
                userSubscriptions.add(subscription);
            }
        }
        this.subscriptions = userSubscriptions;

        return userSubscriptions;
    }

    public List<Subscription> getInactiveSubscriptions() {
        List<Plan> inactivePlans = Plan.getInactivePlans();
        List<Subscription> inactiveSubscriptions = new ArrayList<>();
        for (Plan plan : inactivePlans) {
            String id = WithId.createId(this.email, plan.getName());
            Subscription subscription = (Subscription) Repository.load(
                "Subscription" + File.separator + plan.getName(),
                id
            );
            if (subscription != null) {
                subscription.setUser(this);
                subscription.setPlan(plan);
                inactiveSubscriptions.add(subscription);
            }
        }
        return inactiveSubscriptions;
    }

    public boolean hasCreditCard() {
        return !this.creditCards.isEmpty();
    }

    public boolean addCreditCard(Card card) {
        return this.creditCards.add(card);
    }

    public void removeCreditCard(Card card) {
        if (card != null) {
            card.delete();
            this.creditCards.remove(card);
        }
    }

    public List<Card> getCreditCards() {
        return this.creditCards;
    }

    public Gateway getGateway() {
        return this.gateway;
    }


}
