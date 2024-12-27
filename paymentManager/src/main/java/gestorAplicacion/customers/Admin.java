package gestorAplicacion.customers;

import java.util.ArrayList;

import baseDatos.Repository;
import gestorAplicacion.gateways.Gateway;
import gestorAplicacion.plan.Plan;
import gestorAplicacion.plan.Subscription;

public class Admin extends Customer {

    public Admin(
        String email,
        String password,
        DocumentType documentType,
        String documentNumber
    ) {
        super(email, password, documentType, documentNumber);
    }

    public Plan createPlan(String name, String description, double price, ArrayList<Subscription> subscription) {
        Plan plan = new Plan(name, description, price, subscription);
        Repository.save(plan);
        return plan;
    }

    public void configureGateway(Gateway gateway, String publicKey, String privateKey) {
                
    }
}
