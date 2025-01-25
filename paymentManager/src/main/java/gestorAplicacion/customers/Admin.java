package gestorAplicacion.customers;

import java.io.File;

import baseDatos.Repository;
import gestorAplicacion.gateways.Credential;
import gestorAplicacion.gateways.Gateway;
import gestorAplicacion.plan.Plan;

public class Admin extends Customer {

    public Admin(
        String email,
        String password,
        DocumentType documentType,
        String documentNumber
    ) {
        super(email, password, documentType, documentNumber);
    }

    public Plan createPlan(String name, String description, double price) {
        Plan plan = new Plan(name, description, price);
        Repository.save(plan);
        return plan;
    }

    public Credential configureGateway(Gateway gateway, String publicKey, String privateKey) {
        Credential credential = new Credential(publicKey, privateKey, gateway);
        Repository.save(credential);
        return credential;
    }

    public void inactivate(Plan plan) {
        Repository.update(plan);
    }
}
