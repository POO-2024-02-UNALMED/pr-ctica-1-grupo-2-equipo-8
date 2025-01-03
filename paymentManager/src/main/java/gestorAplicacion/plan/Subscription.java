package gestorAplicacion.plan;

import gestorAplicacion.WithId;
import gestorAplicacion.customers.User;

public class Subscription extends WithId {
    private String name;
    private String description;
    private double price;
    private int duration;
   

    public Subscription(String id, String name, String description, double price, int duration) {
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

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }   

    public double getPrice() {
        return price;
    }

    public int getDuration() {
        return duration;
    }

    public void setid(String id) {
        setId(id);
    }

}
