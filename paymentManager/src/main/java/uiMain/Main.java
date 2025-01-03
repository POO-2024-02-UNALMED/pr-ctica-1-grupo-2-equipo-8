package uiMain;
import java.util.ArrayList;

import baseDatos.Repository;
import gestorAplicacion.customers.Admin;
import gestorAplicacion.customers.DocumentType;
import gestorAplicacion.customers.User;
import gestorAplicacion.gateways.Gateway;
import gestorAplicacion.gateways.GatewaysFactory;
import gestorAplicacion.plan.Plan;
import gestorAplicacion.plan.Subscription;
import gestorAplicacion.transactions.Card;


public class Main {

    static void logObject(Object object) {
        System.out.println(object);
    }

    public static void main(String[] args) {
        Repository.createTempDirectory();

        Subscription sub1 = new Subscription("advanced", "sub1", "1", 100, 1);
        Subscription sub2 = new Subscription("smart", "sub2", "1", 100, 1);
        Subscription sub3 = new Subscription("basic", "sub3", "1", 100, 1);
        Subscription sub4 = new Subscription("essential", "sub4", "1", 100, 1);

        ArrayList<Subscription> advancedSubscriptions = new ArrayList<>();
        advancedSubscriptions.add(sub1);
        Plan advanced = new Plan("Advanced","Books, Music, Videos",100, advancedSubscriptions);

        ArrayList<Subscription> smartSubscriptions = new ArrayList<>();
        smartSubscriptions.add(sub2);
        Plan smart = new Plan("Smart","Books, Music",80, smartSubscriptions);

        ArrayList<Subscription> basicSubscriptions = new ArrayList<>();
        basicSubscriptions.add(sub3);
        Plan basic = new Plan("Basic","Videos",50, basicSubscriptions);

        ArrayList<Subscription> essentialSubscriptions = new ArrayList<>();
        essentialSubscriptions.add(sub4);
        Plan essential = new Plan("Essential","Music",50, essentialSubscriptions);

        // save plans
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
        User janet = new User("janetdoe@gmail.com", "STRONG_PASS", DocumentType.CC, "1234567890");
        Card card = GatewaysFactory.getGateway(Gateway.CUSTOM).addCreditCard("1234567890", janet.getEmail(), "26/35", "123");
        janet.addCreditCard(card);
        janet.addSubscription(advanced);
        janet.hasCreditCard();
        Repository.save(janet);
    }
}

