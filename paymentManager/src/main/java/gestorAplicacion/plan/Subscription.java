package gestorAplicacion.plan;

import java.util.Date;

import gestorAplicacion.WithId;
import gestorAplicacion.customers.User;
import gestorAplicacion.gateways.Gateway;
import gestorAplicacion.gateways.GatewaysFactory;
import gestorAplicacion.transactions.Card;
import gestorAplicacion.transactions.Transaction;
import gestorAplicacion.transactions.TransactionStatus;

public class Subscription extends WithId {
    private static final long serialVersionUID = 2L;
    private transient User user;
    private transient Plan plan;
    private int durationDays;
    private Date nextChargeDate;
    private Date startDate;
    private SubscriptionStatus status;
    private int numberOfCollectionAttempts = 0;
    private Card card;

    public void setCard(Card card) {
        this.card = card;        
    }
    public Subscription(User user, Plan plan, int durationDays) {
        super(createId(user.getEmail(), plan.getName()));
        this.durationDays = durationDays;
        this.user = user;
        this.plan = plan;
        this.startDate = new Date();
        this.status = SubscriptionStatus.INACTIVE;
    }

    public Subscription(User user, Plan plan, int durationDays, Date startDate) {
        super(createId(user.getEmail(), plan.getName()));
        this.durationDays = durationDays;
        this.user = user;
        this.plan = plan;
        this.startDate = startDate;
        this.status = SubscriptionStatus.INACTIVE;
    }

    public Transaction processPayment(Transaction transaction, Gateway gateway) {        

        GatewaysFactory.getGateway(gateway).pay(transaction);

        return transaction;

    }

    public Gateway getGateway() {
        return this.user.getGateway();
    }

    public Transaction processPayment(Gateway gateway) {
        if (this.nextChargeDate != null && this.nextChargeDate.after(new Date())) {
            return new Transaction(
                this.plan.getName(),
                this.user, this.plan.getPrice(),
                TransactionStatus.REJECTED
            );
        }

        Transaction transaction = new Transaction(
            this.plan.getName(),
            this.user,
            this.plan.getPrice(),
            TransactionStatus.PENDING
        );
        GatewaysFactory.getGateway(gateway).pay(transaction);
        if (transaction.getStatus() == TransactionStatus.ACCEPTED) {
            this.nextChargeDate = new Date(System.currentTimeMillis() + 30 * 24 * 60 * 60 * 1000);
            this.status = SubscriptionStatus.ACTIVE;
        } else if (this.numberOfCollectionAttempts < 3) {
            // update next charge date to tomorrow
            this.nextChargeDate = new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000);
            this.status = SubscriptionStatus.PENDING;
            this.numberOfCollectionAttempts++;
        } else {
            this.status = SubscriptionStatus.CANCELLED;
        }

        return transaction;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Plan getPlan() {
        return plan;
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
    }

    public int getDurationDays() {
        return durationDays;
    }

    public Date getNextChargeDate() {
        return nextChargeDate;
    }

    public void setNextChargeDate(Date nextChargeDate) {
        this.nextChargeDate = nextChargeDate;
    }

    public SubscriptionStatus getStatus() {
        return status;
    }

    public void setStatus(SubscriptionStatus status) {
        this.status = status;
    }

    public Date getStartDate() {
        return startDate;
    }
}
