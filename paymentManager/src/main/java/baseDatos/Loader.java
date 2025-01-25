package baseDatos;

import java.time.LocalDate;
import java.util.List;

import gestorAplicacion.WithId;
import gestorAplicacion.customers.Admin;
import gestorAplicacion.customers.Customer;
import gestorAplicacion.customers.DocumentType;
import gestorAplicacion.customers.User;
import gestorAplicacion.gateways.Gateway;
import gestorAplicacion.gateways.GatewaysFactory;
import gestorAplicacion.gateways.IGateway;
import gestorAplicacion.plan.Plan;
import gestorAplicacion.plan.Subscription;
import gestorAplicacion.transactions.Card;

public class Loader {
    private String email;
    private String password;
    private User systemUser;
    private Admin systemAdmin;
    private boolean debugMode = false;
    private List<Plan> plans;

    public Loader(String email, String password, boolean debugMode) {
        this.email = email;
        this.password = password;
        this.debugMode = debugMode;
    }

    private void createRandomUsers() {
        for (int i = 0; i < 10; i++) {
            User user = new User(
                "user" + i + "@gmail.com",
                "password" + i,
                DocumentType.CC,
                "1234567890" + i,
                Gateway.PROJECT_GATEWAY
            );
            // add credit card
            Card card = GatewaysFactory.getGateway(Gateway.PROJECT_GATEWAY).addCreditCard(
                "5434567890111213",
                user.getEmail(),
                "02/35",
                "123",
                user
            );
            user.addCreditCard(card);
            // add subscription
            user.addSubscription(plans.get(i % 4));
            Repository.save(user);
        }
    }

    public void loadData() {
        Repository.setDebugMode(debugMode);
        Repository.createTempDirectory();
        Plan advanced = new Plan("Advanced","Books, Music, Videos",100);
        Plan smart = new Plan("Smart","Books, Music",80);
        Plan basic = new Plan("Basic","Videos",50);
        Plan essential = new Plan("Essential","Music",50);
        this.plans = List.of(advanced, smart, basic, essential);
        Repository.save(advanced);
        Repository.save(smart);
        Repository.save(basic);
        Repository.save(essential);

        Admin admin = new Admin(
            this.email,
            this.password,
            DocumentType.CC,
            "1234567890"
        );
        User user = new User(
            admin.getEmail(),
            this.password,
            DocumentType.CC,
            admin.getDocumentNumber(),
            Gateway.PROJECT_GATEWAY
        );

        Repository.save(admin);
        admin.configureGateway(Gateway.PROJECT_GATEWAY, "publicKey", "privateKey");
        GatewaysFactory.initializeGateway(Gateway.PROJECT_GATEWAY);
        IGateway projectGateway = GatewaysFactory.getGateway(Gateway.PROJECT_GATEWAY);

        Card card = projectGateway.addCreditCard(
            "5434567890111213",
            user.getEmail(),
            "02/35",
            "123",
            user
        );
        Card card2 = projectGateway.addCreditCard(
            "454567890114312",
            user.getEmail(),
            "10/30",
            "132",
            user
        );
        user.addCreditCard(card);
        user.addCreditCard(card2);

        user.addSubscription(essential, card2);
        Subscription futureSubscription = new Subscription(user, basic, LocalDate.now().plusDays(1));

        Repository.save(futureSubscription, "Subscription" + java.io.File.separator + basic.getName());
        Repository.save(user);
        this.systemUser = user;
        this.systemAdmin = admin;
        createRandomUsers();
    }

    public static Customer loadCustomer(String email, String password, String type) {
        String id = WithId.createId(email, password);
        return (Customer) Repository.load(type, id);
    }

    public User getSystemUser() {
        return this.systemUser;
    }

    public Admin getSystemAdmin() {
        return this.systemAdmin;
    }
}
