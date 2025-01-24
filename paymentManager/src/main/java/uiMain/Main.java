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

    static Card addCreditCard() {
        String cardNumber = askString("Enter the your credit card number");
        String cardHolder = askString("Enter the card holder name");
        String expirationDate = askString("Enter the due date of your credit card (MM/YY)");
        String cvv = askString("Enter the CVV of your credit card");
        if (!ProjectGateway.validate(cardNumber, cardHolder, expirationDate, cvv)) {
            log("Invalid credit card information");
            return addCreditCard();
        }
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

    static void showInformation(String message, String[] headers, List<String[]> rows) {
        System.out.println();
        log(message);
        printTable(headers, rows);
    }

    static int askForSelectionOnTableFormat(String message, String[] headers, List<String[]> rows) {
        showInformation(message, headers, rows);
        int selection = Integer.parseInt(System.console().readLine()) - 1;
        if (selection < 0 || selection >= rows.size()) {
            log(INVALID_OPTION_MESSAGE);
            return askForSelectionOnTableFormat(message, headers, rows);
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

    static Subscription showUserSubscriptions(User user, String message, boolean informative) {
        List<Subscription> subscriptions = user.getSubscriptions();
        String[] headers = {"ID", "Plan", "Status", "Next charge date", "Payment method"};
        List<String[]> rows = new ArrayList<>();
        for (int i = 0; i < subscriptions.size(); i++) {
            rows.add(new String[] {
                String.valueOf(i + 1),
                subscriptions.get(i).getPlan().getName(),
                subscriptions.get(i).getStatus().toString(),
                subscriptions.get(i).getNextChargeDate().toString(),
                subscriptions.get(i).getPaymentMethod().getLastFour() +
                "-" + subscriptions.get(i).getPaymentMethod().getFranchise().toString() +
                "-" + subscriptions.get(i).getPaymentMethod().getExpirationDate()
            });
        }
        if (informative) {
            showInformation(message, headers, rows);
            return null;
        }
        int selectedSubscription =  askForSelectionOnTableFormat(message, headers, rows);
        return subscriptions.get(selectedSubscription);
    }

    static void addSubscription(User user) {
        List<Plan> plans = Plan.getAll();
        List<Plan> userPlans = user.getUserSubscribedPlans();
        List<String> userSubscribedPlansNames = new ArrayList<>();
        List<Plan> nonSubscribePlans = new ArrayList<>();

        if(nonSubscribePlans.isEmpty()) {
            showUserSubscriptions(user, "You are already subscribed to all available plans", true);
            return;
        }

        String[] header1Fun1 = {"ID", "Name", "Description", "Price"};
        List<String[]> rows1Fun1 =  new ArrayList<>();

        for (int i = 0; i < userPlans.size(); i++) {
            userSubscribedPlansNames.add(userPlans.get(i).getName());
        }

        int count = 0;
        for (int i = 0; i < plans.size(); i++) {
            if (!userSubscribedPlansNames.contains(plans.get(i).getName())) {
                count++;
                rows1Fun1.add(new String[] {
                    String.valueOf(count),
                    plans.get(i).getName(),
                    plans.get(i).getDescription(),
                    String.valueOf(plans.get(i).getPrice())
                });
                nonSubscribePlans.add(plans.get(i));
            }
        }

        int selectedPlanIndex = askForSelectionOnTableFormat("Select the plan you want to subscribe", header1Fun1, rows1Fun1);

        List<Card> creditCards = user.getCreditCards();
        if (creditCards.isEmpty()) {
            log("You need to add a credit card to subscribe to a plan");
            user.addCreditCard(addCreditCard());
        }

        String[] headers2Fun1 = {"ID", "Last four digits", "Expiration date", "Franchise"};
        List<String[]> rows2Fun1 = new ArrayList<>();
        for (int i = 0; i < creditCards.size(); i++) {
            rows2Fun1.add(new String[] {
                String.valueOf(i + 1),
                creditCards.get(i).getLastFour(),
                creditCards.get(i).getExpirationDate(),
                creditCards.get(i).getFranchise().toString()
            });
        }

        int selectedCardIndex = askForSelectionOnTableFormat("Select the credit card you want to use", headers2Fun1, rows2Fun1);
        Transaction initialTransaction = user.addSubscription(
            nonSubscribePlans.get(selectedPlanIndex),
            creditCards.get(selectedCardIndex)
        );

        log(
            new String [] {"Payment method changed successfully","Error changing payment method"},
            initialTransaction.getStatus() == TransactionStatus.ACCEPTED
        );

        String[] headers3Fun1 = {"Plan Name", "Transaction Status", "Subscription Status"};
        List<String[]> rows3Fun1 = new ArrayList<>();
        rows3Fun1.add(new String[] {
            nonSubscribePlans.get(selectedPlanIndex).getName(),
            initialTransaction.getStatus().toString(),
            initialTransaction.getStatus() == TransactionStatus.ACCEPTED ? "ACTIVE" : "PENDING",
        });
        printTable(headers3Fun1, rows3Fun1);
    }

    static void addUserCreditCard(User user) {
        Card cardToAdd = addCreditCard();
        int accept = askForSelection("We will charge your card to validate it, dou you agree?", new String [] {"Yes", "No"});
        if (accept != 0) {
            return;
        }

        Transaction verificationTransaction = new Transaction(
            "Credit card Validation",
            user,
            100,
            TransactionStatus.PENDING,
            cardToAdd
        );
        GatewaysFactory.getGateway(Gateway.PROJECT_GATEWAY).pay(verificationTransaction);
        boolean creditCardAdded = user.addCreditCard(cardToAdd);
        log(new String [] {"Credit card added successfully", "Invalid credit card"}, creditCardAdded);
        String[] headers1Fun2 = {"Last Four Digits", "Status", "Gateway", "Franchise"};
        List<String[]> rows1Fun2 = new ArrayList<>();
        for (Card card : user.getCreditCards()) {
            rows1Fun2.add(new String[] {
                card.getLastFour(),
                "VALID",
                user.getGateway().toString(),
                card.getFranchise().toString()
            });
        }
        printTable(headers1Fun2, rows1Fun2);
    }

    static void changeSubscriptionPaymentMethod(User user) {
        Subscription selectedSubscription = showUserSubscriptions(user, "Select the subscription you want to change the payment method", false);
        Card newCard = addCreditCard();
        boolean paymentMethodChanged = user.changeSubscriptionPaymentMethod(selectedSubscription, newCard);
        log(new String [] {"Payment method changed successfully", "Error changing payment method"}, paymentMethodChanged);
    }

    static void runFeature(User user, Admin admin) {
        System.out.println();
        int feature = askForSelection("Select function", FEATURES);
        switch (feature) {
            case 0:
                addSubscription(user);
                runFeature(user, admin);
                break;

            case 1:
                addUserCreditCard(user);
                runFeature(user, admin);
                break;

            case 2:
                changeSubscriptionPaymentMethod(user);
                runFeature(user, admin);
                break;

            case 3: // Delete plan

                List<Subscription> subscriptions = user.getSubscriptions();
                String [] subscriptionNamesToDelete = new String[subscriptions.size()];
                for (int i = 0; i < subscriptions.size(); i++) {
                    subscriptionNamesToDelete[i] = subscriptions.get(i).getUser().getEmail() +
                        "-" +
                        subscriptions.get(i).getPlan().getName();
                }

                String[] headers1Fun4 = {"ID", "Name", "Description", "Price"};
                List<String[]> rows1Fun4 = new ArrayList<>();

                int count2 = 0;
                for (int i = 0; i < subscriptions.size(); i++) {
                    if (!java.util.Arrays.asList(subscriptionNamesToDelete).contains(subscriptions.get(i).getPlan().getName())) {
                        count2++;
                        rows1Fun4.add(new String[] {
                            String.valueOf(count2),
                            subscriptions.get(i).getPlan().getName(),
                            subscriptions.get(i).getPlan().getDescription(),
                            String.valueOf(subscriptions.get(i).getPlan().getPrice())
                        });
                    }
                }
                int selectedPlanIndex4 = askForSelectionOnTableFormat("Select the plan you want to delete", headers1Fun4, rows1Fun4);

                subscriptions.remove(selectedPlanIndex4);
                runFeature(user, admin);
                break;

            case 4: // Charge subscription

                List<Subscription> subscription = user.getSubscriptions();
                String [] subscriptionName = new String[subscription.size()];
                for (int i = 0; i < subscription.size(); i++) {
                    subscriptionName[i] = subscription.get(i).getUser().getEmail()+" "+subscription.get(i).getPlan().getName();
                }

                String[] headers5 = {"ID", "Name", "Subscription", "Price"};
                List<String[]> rows5 = new ArrayList<>();

                int count5 = 0;

                for (int i = 0; i < subscription.size(); i++) {
                    if (!java.util.Arrays.asList(subscriptionName).contains(subscription.get(i).getPlan().getName())) {
                        count5++;
                        rows5.add(new String[] {
                            String.valueOf(count5),
                            user.getUserSubscribedPlans().get(i).getName(),
                            user.getUserSubscribedPlans().get(i).getDescription(),
                            String.valueOf(subscription.get(i).getPlan().getPrice())
                        });
                    }
                }

                int selectedSubsIndex= askForSelectionOnTableFormat("Select the subscription yoy want to charge", headers5, rows5);

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
                runFeature(user, admin);
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
            "5434567890111213",
            user.getEmail(),
            "02/35",
            "123"
        );
        Card card2 = projectGateway.addCreditCard(
            "454567890114312",
            user.getEmail(),
            "10/30",
            "132"
        );
        user.addCreditCard(card);
        user.addCreditCard(card2);
        user.addSubscription(basic);
        user.addSubscription(essential);
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

