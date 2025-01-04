package gestorAplicacion.plan;

import gestorAplicacion.WithId;
import gestorAplicacion.customers.User;
import gestorAplicacion.gateways.Gateway;
import gestorAplicacion.gateways.GatewaysFactory;
import gestorAplicacion.transactions.Transaction;

public class Subscription extends WithId {
    private User user;
    private Plan plan;
    private int duration;

    public Subscription(User user, Plan plan, int duration) {
        super(createId(user.getEmail(), plan.getName()));
        this.duration = duration;
        this.user = user;
        this.plan = plan;
    }

    public Transaction processPayment(Gateway gateway) {
        Transaction transaction = new Transaction(this.plan.getName(), this.user, this.plan.getPrice());
        GatewaysFactory.getGateway(gateway).pay(transaction);
        return transaction;
    }

    public User getUser() {
        return user;
    }

    public Plan getPlan() {
        return plan;
    }


    public int getDuration() {
        return duration;
    }
}
