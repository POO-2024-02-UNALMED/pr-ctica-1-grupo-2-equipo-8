package uiMain;

import java.util.List;
import java.util.Scanner;

import baseDatos.Repository;
import gestorAplicacion.Notificacion;
import gestorAplicacion.WithId;
import gestorAplicacion.customers.Admin;
import gestorAplicacion.customers.Customer;
import gestorAplicacion.customers.DocumentType;
import gestorAplicacion.customers.User;
import gestorAplicacion.gateways.Gateway;
import gestorAplicacion.gateways.GatewaysFactory;
import gestorAplicacion.gateways.IGateway;
import gestorAplicacion.gateways.ProjectGateway;
import gestorAplicacion.plan.Plan;
import gestorAplicacion.plan.Subscription;
import gestorAplicacion.transactions.Card;
import gestorAplicacion.transactions.Transaction;
import gestorAplicacion.transactions.TransactionStatus;


public class Main {

    static void log(Object object) {
        System.out.println(object);
    }
    static int  Askmenu(String [] userOptions){
        System.out.println("Deseas hacer otra operacion? 1. Si 2. No");
        int option = new Scanner(System.in).nextInt();
        if (option == 1) {
            int selection = askForSelection("Select function", userOptions);
            return selection;
        } else {
            System.out.println("Gracias por usar nuestro servicio");
            return -1;
        }
        
    }
    static Card addCreditCard() {
        String cardNumber = askString("Enter the your credit card number");
        String cardHolder = askString("Enter the card holder name");
        String expirationDate = askString("Enter the due date of your credit card (MM/YY)");
        String cvv = askString("Enter the CVV of your credit card");
        ProjectGateway projectGateway = new ProjectGateway();
        return projectGateway.addCreditCard(cardNumber, cardHolder, expirationDate, cvv);
        

    }

    static String askString(String message) {
        log(message);
        return System.console().readLine();
    }

    static int askForSelection (String message, String [] options) {
        log(message);
        for (int i = 0; i < options.length; i++) {
            log(i + 1 + ". " + options[i]);
        }
        int selection = new Scanner(System.in).nextInt()-1;
        if (selection < 0 || selection > options.length) {
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
        int role = askForSelection("Select your role: ", new String [] {"User", "Admin"});
        // ask for email
        String email = askForStringInput("Enter your email: ");
        // ask for password
        String password = askForPassword("Enter your password: ");
        String id = WithId.createId(email, password);
        Customer customer = (Customer) Repository.load(role==0 ? "User" : "Admin", id);
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
            "jdoe",
            "PASS",
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
            "jane",
            "STRO", DocumentType.CC,
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

        Repository.save(janet);

        String [] userOptions = {"Add subscription", "Add credit card", "Change subscription paying metod","Remove plan","Charge subscription"};

        // LOGIN
        Customer customer = login();
        Notificacion notificacion = new Notificacion();
        
        int selection = askForSelection("Select function", userOptions);
        switch (selection) {
            
            case 0: // Add subscription ###############
            List<Plan> plans = Plan.getAll();
            String [] planNames = new String[plans.size()];
            for (int i = 0; i < plans.size(); i++) {
                planNames[i] = plans.get(i).getName();
            }
            int selectedPlanIndex = askForSelection("Select a plan", planNames);
            if (janet.addSubscription(plans.get(selectedPlanIndex))){
                notificacion.sendNotification(false, "Subscription added successfully");
            } else {
                notificacion.sendNotification(true, "Error adding subscription");               
            }
            Askmenu(userOptions);
                break;

            case 1://Add credit card ############
                if (janet.addCreditCard(addCreditCard())) {
                    notificacion.sendNotification(false, "Credit card added successfully");
                } else {
                    notificacion.sendNotification(true, "Invalid credit car");
                }
            Askmenu(userOptions);
                break;
            
            case 2://Change subscription paying metod ############
                List<Subscription> subscriptions = janet.getSubscriptions();
                String [] subscriptionNames = new String[subscriptions.size()];
                for (int i = 0; i < subscriptions.size(); i++) {
                    subscriptionNames[i] = subscriptions.get(i).getPlan().getName();
                }
                int selectedSubscriptionIndex = askForSelection("Select a subscription", subscriptionNames);
                Subscription selectedSubscription = subscriptions.get(selectedSubscriptionIndex);
                Card newCard = addCreditCard();
                if (janet.changeSubcritionPaymentMethod(selectedSubscription, newCard)) {
                    notificacion.sendNotification(false, "Payment method changed successfully");
                } else {
                    notificacion.sendNotification(true, "Error changing payment method");
                }
            Askmenu(userOptions);
                break;

            case 3: // Delete plan ############
            Askmenu(userOptions);
                break;

            case 4: // Charge subscription ############
                List<Subscription> subscription = janet.getSubscriptions();
                String [] subscriptionName = new String[subscription.size()];
                for (int i = 0; i < subscription.size(); i++) {
                    subscriptionName[i] = subscription.get(i).getPlan().getName();
                }
                int selectedSubsIndex = askForSelection("Select a subscription to charge", subscriptionName);
                Subscription selectedSubs = subscription.get(selectedSubsIndex);
                Transaction transaction = new Transaction(
                    selectedSubs.getPlan().getName(),
                    janet,
                    selectedSubs.getPlan().getPrice(),
                    TransactionStatus.PENDING
                );
                selectedSubs.processPayment(transaction, selectedSubs.getGateway());
                if (transaction.getStatus() == TransactionStatus.ACCEPTED) {
                    notificacion.sendNotification(false, "Subscription charged successfully");
                } else {
                    notificacion.sendNotification(true, "Error charging subscription");
                }
            Askmenu(userOptions);
                break;
            default:
                log("Invalid selection");
        }

          
    }
}

