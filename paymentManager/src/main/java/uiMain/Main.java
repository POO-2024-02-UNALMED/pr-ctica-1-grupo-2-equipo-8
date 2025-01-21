package uiMain;

import java.util.List;

import baseDatos.Repository;
import gestorAplicacion.Notification;
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

    static final String[] FEATURES = {
        "Add subscription",
        "Add credit card",
        "Change subscription paying method",
        "Remove plan",
        "Charge subscription"
    };

    static void log(Object object) {
        System.out.println(object);
    }

    static int  askMenu(String [] userOptions){
        int option = askForSelection("Would you like to explore more features", new String [] {"Yes", "No"});
        if (option == 0) {
            return askForSelection("Select function", userOptions);
        } else {
            log("Thanks for using our service");
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
        int selection = Integer.parseInt(System.console().readLine()) - 1;
        if (selection < 0 || selection > options.length) {
            log("Invalid selection, please select a valid option");
            return askForSelection(message, options);
        }
        return selection;
    }


    static String askForPassword (String message) {
        log(message);
        return new String(System.console().readPassword());
    }

    static Customer login () {
        String email = askString("Enter your email: ");
        String password = askForPassword("Enter your password: ");
        String id = WithId.createId(email, password);
        Customer customer = (Customer) Repository.load("User", id);
        if (customer == null) {
            log("Invalid credentials");
            return login();
        }
        return customer;
    }

    static void log(String[] messages, boolean success) {
        if (success) {
            Notification.sendNotification(false, messages[0] );
        } else {
            Notification.sendNotification(true, messages[1]);
        }
    }

    static void runFeature(User user, Admin admin) {
        int feature = askForSelection("Select function", FEATURES);
        switch (feature) {
            case 0: // Add subscription
                List<Plan> plans = Plan.getAll();
                String[] planNames = new String[plans.size()];

                for (int i = 0; i < plans.size(); i++) {
                    planNames[i] = plans.get(i).getName();
                }

                int selectedPlanIndex = askForSelection("Select a plan", planNames);
                boolean subscriptionAdded = user.addSubscription(plans.get(selectedPlanIndex));
                log(new String [] {"Subscription added successfully", "Error adding subscription"}, subscriptionAdded);

                runFeature(user, admin);
                break;

            case 1: //Add credit card
                boolean creditCardAdded = user.addCreditCard(addCreditCard());
                log(new String [] {"Credit card added successfully", "Invalid credit card"}, creditCardAdded);

                runFeature(user, admin);
                break;

            case 2: //Change subscription paying method
                List<Subscription> subscriptions = user.getSubscriptions();
                String [] subscriptionNames = new String[subscriptions.size()];
                for (int i = 0; i < subscriptions.size(); i++) {
                    subscriptionNames[i] = subscriptions.get(i).getPlan().getName();
                }

                int selectedSubscriptionIndex = askForSelection("Select a subscription", subscriptionNames);
                Subscription selectedSubscription = subscriptions.get(selectedSubscriptionIndex);
                Card newCard = addCreditCard();
                boolean paymentMethodChanged = user.changeSubcritionPaymentMethod(selectedSubscription, newCard);
                log(new String [] {"Payment method changed successfully", "Error changing payment method"}, paymentMethodChanged);
                runFeature(user, admin);
                break;

            case 3: // Delete plan
                runFeature(user, admin);
                break;

            case 4: // Charge subscription
                List<Subscription> subscription = user.getSubscriptions();
                String [] subscriptionName = new String[subscription.size()];
                for (int i = 0; i < subscription.size(); i++) {
                    subscriptionName[i] = subscription.get(i).getUser().getEmail()+" "+subscription.get(i).getPlan().getName();
                }
                int selectedSubsIndex = askForSelection("Select a subscription to charge", subscriptionName);
                Subscription selectedSubs = subscription.get(selectedSubsIndex);
                Transaction transaction = new Transaction(
                    selectedSubs.getPlan().getName(),
                    user,
                    selectedSubs.getPlan().getPrice(),
                    TransactionStatus.PENDING
                );
                selectedSubs.processPayment(transaction, selectedSubs.getGateway());

                boolean charged = transaction.getStatus() == TransactionStatus.ACCEPTED;
                log(new String [] {"Subscription charged successfully", "Error charging subscription"}, charged);
                runFeature(user, admin);
                break;

            default:
                log("Invalid selection");
        }
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
            "jdoe",
            "PASS",
            DocumentType.CC,
            "1234567890"
        );

        User user = new User(
            admin.getEmail(),
            admin.getPassword(),
            DocumentType.CC,
            admin.getDocumentNumber(),
            Gateway.PROJECT_GATEWAY
        );

        Repository.save(admin);

        admin.configureGateway(Gateway.PROJECT_GATEWAY, "publicKey", "privateKey");
        GatewaysFactory.initializeGateway(Gateway.PROJECT_GATEWAY);
        IGateway projectGateway = GatewaysFactory.getGateway(Gateway.PROJECT_GATEWAY);

        Card card = projectGateway.addCreditCard(
            "1234567890",
            user.getEmail(),
            "26/35",
            "123"
        );
        user.addCreditCard(card);
        user.addSubscription(advanced);
        user.addSubscription(smart);
        user.addSubscription(basic);
        Repository.save(user);

        // LOGIN
        Customer customer = login();
        if (customer == null) {
            log("Invalid credentials");
            return;
        }

        runFeature((User) customer, admin);
    }
}

