package uiMain;
import baseDatos.Repository;
import gestorAplicacion.customers.Admin;
import gestorAplicacion.customers.DocumentType;
import gestorAplicacion.customers.User;
import gestorAplicacion.gateways.Custom;
import gestorAplicacion.gateways.IAdapter;
import gestorAplicacion.plan.Plan;
import gestorAplicacion.transactions.Card;


public class Main {

    static void logObject(Object object) {
        System.out.println(object);
    }

    public static void main(String[] args) {
        Repository.createTempDirectory();

        Admin admin = new Admin(
            "jdoe@gmail.com",
            "AVERYSECUREPASSWORD",
            DocumentType.CC,
            "1234567890"
        );
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

        Plan plan = new Plan("1","Plan1","Plan1",100,1,new Admin[]{admin});
        Repository.save(plan);

        IAdapter custom = new Custom();

        User janet = new User("janetdoe@gmail.com", "STRONGPASS", DocumentType.CC, "1234567890");
        Card card = custom.addCreditCard("1234567890", janet.getEmail(), "26/35", "123");
        janet.addCreditCard(card);

        logObject(janet.addSubscription());
        Repository.save(janet);
    }
}

