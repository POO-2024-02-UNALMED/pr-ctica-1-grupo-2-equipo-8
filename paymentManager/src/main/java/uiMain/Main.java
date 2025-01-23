package uiMain;

import java.util.ArrayList;
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
import gestorAplicacion.plan.SubscriptionStatus;
import gestorAplicacion.transactions.Card;
import gestorAplicacion.transactions.Transaction;
import gestorAplicacion.transactions.TransactionStatus;


public class Main {

    static final String[] FEATURES = {
        "Add subscription",
        "Add credit card",
        "Change subscription paying method",
        "Remove plan",
        "Charge subscription",
        "Exit"
    };

    static final String INVALID_OPTION_MESSAGE = "Invalid option, please select a valid option";

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

    static int askForSelection (String message, String[] options) {
        log(message);
        for (int i = 0; i < options.length; i++) {
            log(i + 1 + ". " + options[i]);
        }
        int selection = Integer.parseInt(System.console().readLine()) - 1;
        if (selection < 0 || selection > options.length) {
            log(INVALID_OPTION_MESSAGE);
            return askForSelection(message, options);
        }
        return selection;
    }

    static int askForSelection (String message, List<String> options) {
        System.out.println();
        log(message);
        for (int i = 0; i < options.size(); i++) {
            log(i + 1 + ". " + options.get(i));
        }
        int selection = Integer.parseInt(System.console().readLine()) - 1;
        if (selection < 0 || selection > options.size()) {
            log(INVALID_OPTION_MESSAGE);
            return askForSelection(message, options);
        }
        System.out.println();
        return selection;
    }

    static int showOptionAsTable(String message, String[] headers, List<String[]> rows) {
        System.out.println();
        log(message);
        printTable(headers, rows);
        int selection = Integer.parseInt(System.console().readLine()) - 1;
        if (selection < 0 || selection >= rows.size()) {
            log(INVALID_OPTION_MESSAGE);
            return showOptionAsTable(message, headers, rows);
        }
        System.out.println();
        return selection;
    }

    static String askForPassword (String message) {
        log(message);
        return new String(System.console().readPassword());
    }

    private static void printTable(String[] headers, List<String[]> rows) {
        // Determine column widths
        int[] columnWidths = new int[headers.length];
        for (int i = 0; i < headers.length; i++) {
            columnWidths[i] = headers[i].length();
        }
        for (String[] row : rows) {
            for (int i = 0; i < row.length; i++) {
                columnWidths[i] = Math.max(columnWidths[i], row[i].length());
            }
        }

        // Print header
        printRow(headers, columnWidths);
        printSeparator(columnWidths);

        // Print rows
        for (String[] row : rows) {
            printRow(row, columnWidths);
        }
    }

    private static void printRow(String[] row, int[] columnWidths) {
        for (int i = 0; i < row.length; i++) {
            System.out.printf("| %-"+columnWidths[i]+"s ", row[i]);
        }
        System.out.println("|");
    }

    private static void printSeparator(int[] columnWidths) {
        for (int width : columnWidths) {
            System.out.print("+");
            System.out.print("-".repeat(width + 2));
        }
        System.out.println("+");
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
            Notification.sendNotification(false, messages[0]);
        } else {
            Notification.sendNotification(true, messages[1]);
        }
    }

    static void runFeature(User user, Admin admin) {
        System.out.println();
        int feature = askForSelection("Select function", FEATURES);
        switch (feature) {
            case 0: // Add subscription
                List<Plan> plans = Plan.getAll();
                List<Plan> userPlans = user.getUserSubscribedPlans();
                List<String> userSubscribedPlansNames = new ArrayList<>();
                List<Plan> nonSubscribePlans = new ArrayList<Plan>();

                String[] headers = {"ID", "Name", "Description", "Price"};
                List<String[]> rows =  new ArrayList<>();

                for (int i = 0; i < userPlans.size(); i++) {
                    userSubscribedPlansNames.add(userPlans.get(i).getName());
                }

                int count = 0;

                for (int i = 0; i < plans.size(); i++) {
                    if (!userSubscribedPlansNames.contains(plans.get(i).getName())) {
                        count++;
                        rows.add(new String[] {
                            String.valueOf(count),
                            plans.get(i).getName(),
                            plans.get(i).getDescription(),
                            String.valueOf(plans.get(i).getPrice())
                        });
                        nonSubscribePlans.add(plans.get(i));
                    }
                }

                int selectedPlanIndex = showOptionAsTable("Select the plan you want to subscribe", headers, rows);
                Subscription addedSubscription = user.addSubscription(nonSubscribePlans.get(selectedPlanIndex));
                log(
                    new String [] {"Subscription added successfully", "Error adding subscription"},
                    addedSubscription.getStatus() == SubscriptionStatus.ACTIVE
                );

                String[] headers2 = {"Name", "Status"};
                List<String[]> rows2 = new ArrayList<>();
                rows2.add(new String[] {addedSubscription.getPlan().getName(), addedSubscription.getStatus().toString()});
                printTable(headers2, rows2);

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

                List<Subscription> subscriptio = user.getSubscriptions();
                String [] subscriptionNamesToDelete = new String[subscriptio.size()];
                for (int i = 0; i < subscriptio.size(); i++) {
                    subscriptionNamesToDelete[i] = subscriptio.get(i).getUser().getEmail()+" "+subscriptio.get(i).getPlan().getName();
                }

              
                
                String[] headers4 = {"ID", "Name", "Description", "Price"};
                List<String[]> rows4 = new ArrayList<String[]>();

                int count2 = 0;

                for (int i = 0; i < subscriptio.size(); i++) {
                    if (!java.util.Arrays.asList(subscriptionNamesToDelete).contains(subscriptio.get(i).getPlan().getName())) {
                        count2++;
                        rows4.add(new String[] {
                            String.valueOf(count2),
                            subscriptio.get(i).getPlan().getName(),
                            subscriptio.get(i).getPlan().getDescription(),
                            String.valueOf(subscriptio.get(i).getPlan().getPrice())
                        });
                        
                    }
                }
                
                int selectedPlanIndex4 = showOptionAsTable("Select the plan you want to delete", headers4, rows4);
                
                subscriptio.remove(selectedPlanIndex4);
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

            case 5:
                log("Thanks for using our service");
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

