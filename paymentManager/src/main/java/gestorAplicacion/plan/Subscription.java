package gestorAplicacion.plan;

import java.time.LocalDate;

import baseDatos.Repository;
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
    private LocalDate nextChargeDate;
    private LocalDate startDate;
    private SubscriptionStatus status;
    private int numberOfCollectionAttempts = 0;
    private Card card;
    private LocalDate suspensionDate;

    public Subscription(User user, Plan plan) {
        super(createId(user.getEmail(), plan.getName()));
        this.user = user;
        this.plan = plan;
        this.startDate = LocalDate.now();
        this.status = SubscriptionStatus.INACTIVE;
        this.nextChargeDate = LocalDate.now();
        this.suspensionDate = LocalDate.MAX;
    }

    public Subscription(User user, Plan plan, LocalDate startDate) {
        this(user, plan);
        if (startDate.isAfter(LocalDate.now())) {
            this.startDate = startDate;
            this.nextChargeDate = startDate;
        }
    }

    public Subscription(User user, Plan plan, Card card) {
        this(user, plan);
        this.card = card;
    }

    public Subscription(User user, Plan plan, LocalDate startDate, Card card) {
        this(user, plan, startDate);
        this.card = card;
    }

    public Transaction processPayment(Transaction transaction) {
        GatewaysFactory.getGateway(this.user.getGateway()).pay(transaction);
        if(transaction.getStatus() == TransactionStatus.ACCEPTED && this.nextChargeDate.isAfter(LocalDate.now().plusDays(1))) {
            long remainingDays = this.nextChargeDate.toEpochDay() - LocalDate.now().toEpochDay();
            this.nextChargeDate = LocalDate.now().plusMonths(1).plusDays(remainingDays);
        }
        else if (transaction.getStatus() == TransactionStatus.ACCEPTED) {
            this.nextChargeDate = LocalDate.now().plusMonths(1);
            this.status = SubscriptionStatus.ACTIVE;
        } else if (this.numberOfCollectionAttempts < 3) {
            // update next charge date to tomorrow
            this.nextChargeDate = LocalDate.now().plusDays(1);
            this.status = SubscriptionStatus.PENDING;
            this.numberOfCollectionAttempts++;
        } else {
            this.status = SubscriptionStatus.CANCELLED;
        }
        Repository.update(this, "Subscription" + java.io.File.separator + this.plan.getName());
        return transaction;
    }

    public Gateway getGateway() {
        return this.user.getGateway();
    }

    public Transaction processPayment() {
        Transaction transaction = new Transaction(
            this.plan.getName(),
            this.user,
            this.plan.getPrice(),
            TransactionStatus.PENDING,
            this.getPaymentMethod()
        );

        return processPayment(transaction);
    }

    public boolean upsertPaymentMethod(Card card) {
        this.setPaymentMethod(card);
        Repository.update(this);

        Transaction transaction = new Transaction(
            this.plan.getName(),
            this.user,
            1,
            TransactionStatus.PENDING
        );
        processPayment(transaction);
        return transaction.getStatus() == TransactionStatus.ACCEPTED;
    }

    public User getUser() {
        return user;
    }

    public Card getPaymentMethod () {
        if(this.card == null) {
            return this.user.getCreditCards().get(0);
        }

        return this.card;
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

    public void setPaymentMethod(Card card) {
        this.card = card;
    }

    public LocalDate getNextChargeDate() {
        return nextChargeDate;
    }

    public SubscriptionStatus getStatus() {
        return status;
    }

    public void setStatus(SubscriptionStatus status) {
        this.status = status;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setNextChargeDate(LocalDate nextChargeDate) {
        this.nextChargeDate = nextChargeDate;
    }

    public void setSuspensionDate(LocalDate date) {
        this.suspensionDate = date;
    }

    public LocalDate getSuspensionDate() {
        return this.suspensionDate;
    }
}
