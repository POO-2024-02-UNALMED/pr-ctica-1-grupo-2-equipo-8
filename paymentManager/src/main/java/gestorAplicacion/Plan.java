package gestorAplicacion;

import java.util.List;

import gestorAplicacion.customers.Admin;


public class Plan extends WithId{
    
    private String name;
    private String description;
    private double price;
    private int duration;
    private Admin[] admins;
    
    public Plan(String id, String name, String description, double price, int duration, Admin[] admins) {
        super(id);
        this.name = name;
        this.description = description;
        this.price = price;
        this.duration = duration;
        this.admins = admins;
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
