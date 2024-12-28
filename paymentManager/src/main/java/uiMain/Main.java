package uiMain;
import java.util.ArrayList;

import baseDatos.Repository;
import gestorAplicacion.customers.Admin;
import gestorAplicacion.customers.DocumentType;
import gestorAplicacion.customers.User;
import gestorAplicacion.gateways.Custom;
import gestorAplicacion.gateways.Gateway;
import gestorAplicacion.gateways.IAdapter;
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

        Plan advanced = new Plan("Advanced","Books, Music, Videos",100, new ArrayList<Subscription>(){{
            add(sub1);
        }});
        Plan smart = new Plan("Smart","Books, Music",80, new ArrayList<Subscription>(){{
            add(sub2);
        }});
        Plan basic = new Plan("Basic","Videos",50, new ArrayList<Subscription>(){{
            add(sub3);
        }});
        Plan essential = new Plan("Essential","Music",50, new ArrayList<Subscription>(){{
            add(sub4);
        }});
        Plan [] list = {advanced,smart,basic,essential};
        for (int i = 0; i < list.length; i++) {
            logObject(Repository.save(list[i]));
        }


        Admin admin = new Admin(
            "jdoe@gmail.com",
            "AVERYSECUREPASSWORD",
            DocumentType.CC,
            "1234567890"
        );

        admin.configureGateway(Gateway.CUSTOM, "publicKey", "privateKey");

        logObject(Repository.save(admin));
        Admin admin2 = (Admin) Repository.load(
            "Admin",
            gestorAplicacion.WithId.createId(
                "jdoe@gmail.com",
                "AVERYSECUREPASSWORD"
            )
        );
        logObject(admin2.getEmail());
        logObject(admin2.getPassword());
        logObject(admin2);

        IAdapter custom = new Custom();

        User janet = new User("janetdoe@gmail.com", "STRONGPASS", DocumentType.CC, "1234567890");
        Card card = custom.addCreditCard("1234567890", janet.getEmail(), "26/35", "123");
        janet.addCreditCard(card);

        logObject(janet.addSubscription(advanced));
        logObject(janet.hasCreditCard());
        Repository.save(janet);
    }
}

