package gestorAplicacion.transactions;

import java.util.List;

import gestorAplicacion.WithId;
import gestorAplicacion.customers.Admin;
import gestorAplicacion.plan.Subscription;

public class Transaction extends WithId {
    private String name;
    private String description;
    private double price;
    private int duration;
    private Card paymentMethod;
    private  TransactionStatus status;

    public Transaction(String id, String name, String description, double price, int duration, Admin[] admins) {
        super(id);
        this.name = name;
        this.description = description;
        this.price = price;
        this.duration = duration;
    }
 
    public Card getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(Card paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public static void mostrarSuscripciones(List<Subscription> suscripciones) {
            for (Subscription suscription : suscripciones) {
            System.out.println("ID: " + suscription.getId());
            System.out.println("Nombre: " + suscription.getName());
            System.out.println("Descripción: " + suscription.getDescription());
            System.out.println("Precio: " + suscription.getPrice());
            System.out.println("Duración: " + suscription.getDuration());
            System.out.println("-------------------------");
        }
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }
    
}
