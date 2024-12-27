package uiMain;
import baseDatos.Repository;
import gestorAplicacion.customers.Admin;
import gestorAplicacion.customers.DocumentType;
import gestorAplicacion.customers.User;
import gestorAplicacion.plan.Plan;
import gestorAplicacion.transactions.Card;


public class Main {

    static void logObject(Object object) {
        System.out.println(object);
    }

    public static void main(String[] args) {

        Repository.createTempDirectory();

        Admin admin = new Admin(
            "John Doe",
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
        logObject(admin2.getName());
        logObject(admin2.getEmail());
        logObject(admin2.getPassword());
        logObject(admin2);

        Plan plan = new Plan("1","Plan1","Plan1",100,1,new Admin[]{admin});
        Repository.save(plan);

        User juanito = new User("Juanito", "iasbdc", "123", DocumentType.CC, "1234567890");
        Repository.save(juanito);
        Card card = new Card("1234567890", 400, 123, "26/35", "1234567890");
        juanito.addCreditCard(card);

        logObject(juanito.addSubscription());
        Repository.delete(juanito);
    }
}

