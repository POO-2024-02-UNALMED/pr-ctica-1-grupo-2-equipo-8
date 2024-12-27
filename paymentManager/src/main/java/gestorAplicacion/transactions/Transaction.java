package gestorAplicacion.transactions;

import gestorAplicacion.WithId;
import gestorAplicacion.customers.Admin;

public class Transaction extends WithId {
    private String name;
    private String description;
    private double price;
    private int duration;
    
    public Transaction(String id, String name, String description, double price, int duration, Admin[] admins) {
        super(id);
        this.name = name;
        this.description = description;
        this.price = price;
        this.duration = duration;
    }
    
}
