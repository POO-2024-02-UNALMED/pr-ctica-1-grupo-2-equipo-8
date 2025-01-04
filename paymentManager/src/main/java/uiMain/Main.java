package uiMain;

import baseDatos.Repository;
import gestorAplicacion.WithId;
import gestorAplicacion.customers.Admin;
import gestorAplicacion.customers.Customer;
import gestorAplicacion.customers.DocumentType;
import gestorAplicacion.customers.User;
import gestorAplicacion.gateways.Gateway;
import gestorAplicacion.gateways.GatewaysFactory;
import gestorAplicacion.gateways.IGateway;
import gestorAplicacion.plan.Plan;
import gestorAplicacion.transactions.Card;


public class Main {

    static void log(Object object) {
        System.out.println(object);
    }

    static String askForSelection (String message, String [] options) {
        log(message);
        for (int i = 0; i < options.length; i++) {
            log(i + 1 + ". " + options[i]);
        }
        String selection = System.console().readLine();
        if (Integer.parseInt(selection) < 0 || Integer.parseInt(selection) >= options.length) {
            log("Invalid selection, please select a valid option");
            return askForSelection(message, options);
        }
        return selection;
    }

    static String askForStringInput (String message) {
        log(message);
        return System.console().readLine();
    }

    static String askForPassword (String message) {
        log(message);
        return new String(System.console().readPassword());
    }

    static Customer login () {
        // ask for role on CLI User or Admin
        String role = askForSelection("Select your role: ", new String [] {"User", "Admin"});
        // ask for email
        String email = askForStringInput("Enter your email: ");
        // ask for password
        String password = askForPassword("Enter your password: ");
        String id = WithId.createId(email, password);
        Customer customer = (Customer) Repository.load(role.equals("1") ? "User" : "Admin", id);
        if (customer == null) {
            log("Invalid credentials");
            return login();
        }

        return customer;
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

        Admin defaultAdmin = new Admin(
            "jdoe@gmail.com",
            "A_VERY_SECURE_PASSWORD",
            DocumentType.CC,
            "1234567890"
        );
        Repository.save(defaultAdmin);

        // configure credentials
        defaultAdmin.configureGateway(Gateway.PROJECT_GATEWAY, "publicKey", "privateKey");

        // Initialize gateways
        GatewaysFactory.initializeGateway(Gateway.PROJECT_GATEWAY);
        IGateway projectGateway = GatewaysFactory.getGateway(Gateway.PROJECT_GATEWAY);

        // Create user
        User janet = new User(
            "janetdoe@gmail.com",
            "STRONG_PASS", DocumentType.CC,
            "1234567890",
            Gateway.PROJECT_GATEWAY
        );
        Card card = projectGateway.addCreditCard(
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

        // LOGIN
        Customer customer = login();
        if (customer instanceof User user) {
            user.getSubscriptions().forEach(Main::log);
        } else {
            Admin admin = (Admin) customer;
            log(admin);
        }
    }
}

