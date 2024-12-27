package gestorAplicacion.transactions;

import java.util.List;

import gestorAplicacion.WithId;
import gestorAplicacion.customers.Admin;
import gestorAplicacion.plan.Suscription;

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
 
    public static void mostrarSuscripciones(List<Suscription> suscripciones) {
        for (Suscription suscription : suscripciones) {
            System.out.println("ID: " + suscription.getId());
            System.out.println("Nombre: " + suscription.getName());
            System.out.println("Descripción: " + suscription.getDescription());
            System.out.println("Precio: " + suscription.getPrice());
            System.out.println("Duración: " + suscription.getDuration());
            System.out.println("-------------------------");
        }
    }
}
