package gestorAplicacion.plan;

import java.util.ArrayList;

import gestorAplicacion.WithId;


public class Plan extends WithId{
    private String name;
    private String description;
    private double price;
    private ArrayList<Subscription> subscription;

    public Plan(String name, String description, double price, ArrayList<Subscription> subscription) {
        super(name);
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public Plan(String id, String name, String description, double price, ArrayList<Subscription> subscription) {
        super(id);
        this.name = name;
        this.description = description;
        this.price = price;
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
}
