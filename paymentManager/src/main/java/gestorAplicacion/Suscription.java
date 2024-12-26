package gestorAplicacion;

import gestorAplicacion.customers.User;

public class Suscription extends WithId {
    private String name;
    private String description;
    private double price;
    private int duration;
   

    public Suscription(String id, String name, String description, double price, int duration) {
        super(id);
        this.name = name;
        this.description = description;
        this.price = price;
        this.duration = duration;
    }   

    
    public boolean procesarPago(User user) {
        // TODO implement here
        return false;
    }
}
