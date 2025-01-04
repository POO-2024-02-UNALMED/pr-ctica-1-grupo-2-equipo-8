package uiMain;

import baseDatos.Repository;
import gestorAplicacion.customers.Admin;
import gestorAplicacion.customers.DocumentType;
import gestorAplicacion.customers.User;
import gestorAplicacion.gateways.Gateway;
import gestorAplicacion.gateways.GatewaysFactory;
import gestorAplicacion.plan.Plan;
import gestorAplicacion.transactions.Card;


public class Main {

    static void logObject(Object object) {
        System.out.println(object);
    }

    public static void main(String[] args) {
        Repository.createTempDirectory();

        Plan advanced = new Plan("Advanced","Books, Music, Videos",100);
        Plan smart = new Plan("Smart","Books, Music",80);
        Plan basic = new Plan("Basic","Videos",50);
        Plan essential = new Plan("Essential","Music",50);
        Repository.save(advanced);
        Repository.save(smart);
        Repository.save(basic);
        Repository.save(essential);

        Admin admin = new Admin(
            "jdoe@gmail.com",
            "A_VERY_SECURE_PASSWORD",
            DocumentType.CC,
            "1234567890"
        );
        Repository.save(admin);

        // configure credentials
        admin.configureGateway(Gateway.CUSTOM, "publicKey", "privateKey");

        // Initialize gateways
        GatewaysFactory.initializeGateway(Gateway.CUSTOM, null, null);

        // Create user
        User janet = new User("janetdoe@gmail.com", "STRONG_PASS", DocumentType.CC, "1234567890", Gateway.CUSTOM);
        Card card = GatewaysFactory
            .getGateway(Gateway.CUSTOM)
            .addCreditCard(
                "1234567890",
                janet.getEmail(),
                "26/35",
                "123"
            );
        janet.addCreditCard(card);

        // Create subscription
        janet.addSubscription(advanced);
        janet.addSubscription(smart);
        janet.addSubscription(basic);
        janet.addSubscription(essential);

        Repository.save(janet);
    }
}

