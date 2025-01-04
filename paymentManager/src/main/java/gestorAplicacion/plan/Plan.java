package gestorAplicacion.plan;

import java.util.ArrayList;
import java.util.List;

import gestorAplicacion.WithId;


public class Plan extends WithId{
    private String name;
    private String description;
    private double price;

    public Plan(String name, String description, double price) {
        super(name);
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public Plan(String id, String name, String description, double price) {
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

    public static List<Plan> getAll() {
        return new ArrayList<>(); // TODO: Implement this method
    }
}
