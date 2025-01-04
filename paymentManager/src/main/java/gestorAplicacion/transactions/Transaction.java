package gestorAplicacion.transactions;

import java.util.Calendar;

import gestorAplicacion.WithId;
import gestorAplicacion.customers.User;
import gestorAplicacion.gateways.Gateway;

public class Transaction extends WithId {
    private Card paymentMethod;
    private  TransactionStatus status;
    private String description;
    private double price;
    private String userEmail;
    private Gateway gateway;

    private static String getMontAndYear() {
        return Calendar.getInstance().get(Calendar.MONTH) + "-" + Calendar.getInstance().get(Calendar.YEAR);
    }

    public Transaction(String description, User user, double price) {
        super(createId(getMontAndYear(), user.getEmail()));
        this.description = description;
        this.price = price;
        this.userEmail = user.getEmail();
        this.gateway = user.getGateway();
    }

    public Card getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(Card paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public Gateway getGateway() {
        return gateway;
    }
}
