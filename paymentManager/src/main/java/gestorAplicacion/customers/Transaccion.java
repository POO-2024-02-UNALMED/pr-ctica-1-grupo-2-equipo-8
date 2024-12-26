package gestorAplicacion.customers;

import gestorAplicacion.WithId;

public class Transaccion extends WithId {
    private String name;
    private String description;
    private double price;
    private int duration;
    
    public Transaccion(String id, String name, String description, double price, int duration, Admin[] admins) {
        super(id);
        this.name = name;
        this.description = description;
        this.price = price;
        this.duration = duration;

        
    }
    
}
