package uiMain;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import baseDatos.Loader;
import baseDatos.Repository;
import gestorAplicacion.WithId;
import gestorAplicacion.customers.Admin;
import gestorAplicacion.customers.Customer;
import gestorAplicacion.customers.User;
import gestorAplicacion.gateways.Gateway;
import gestorAplicacion.gateways.GatewaysFactory;
import gestorAplicacion.gateways.ProjectGateway;
import gestorAplicacion.plan.Plan;
import gestorAplicacion.plan.PlanStatus;
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
        "Pay subscription",
        "Exit"
    };

    static final String INVALID_OPTION_MESSAGE = "Invalid option, please select a valid option";

    static void logLn(Object object) {
        System.out.println(object);
    }
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    static void log(Object object) {
        System.out.print(object.toString() + " ");
    }

    static Card addCreditCard() {
        String cardNumber = askString("Enter the your credit card number");
        String cardHolder = askString("Enter the card holder name");
        String expirationDate = askString("Enter the due date of your credit card (MM/YY)");
        String cvv = askString("Enter the CVV of your credit card");
        if (!ProjectGateway.validate(cardNumber, cardHolder, expirationDate, cvv)) {
            logLn("Invalid credit card information");
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
        logLn(message);
        for (int i = 0; i < options.length; i++) {
            logLn(i + 1 + ". " + options[i]);
        }
        int selection = Integer.parseInt(System.console().readLine()) - 1;
        if (selection < 0 || selection > options.length) {
            logLn(INVALID_OPTION_MESSAGE);
            return askForSelection(message, options);
        }
        return selection;
    }

    static int askForSelection (String message, List<String> options) {
        logLn(message);
        for (int i = 0; i < options.size(); i++) {
            logLn(i + 1 + ". " + options.get(i));
        }
        int selection = Integer.parseInt(System.console().readLine()) - 1;
        if (selection < 0 || selection > options.size()) {
            logLn(INVALID_OPTION_MESSAGE);
            return askForSelection(message, options);
        }
        logLn("");
        return selection;
    }

    static void showInformation(String message, String[] headers, List<String[]> rows) {
        logLn("");
        logLn(message);
        printTable(headers, rows);
    }

    static int askForSelectionOnTableFormat(String message, String[] headers, List<String[]> rows) {
        showInformation(message, headers, rows);
        int selection = Integer.parseInt(System.console().readLine()) - 1;
        if (selection < 0 || selection >= rows.size()) {
            logLn(INVALID_OPTION_MESSAGE);
            return askForSelectionOnTableFormat(message, headers, rows);
        }
        logLn("");
        return selection;
    }

    static String askForPassword (String message) {
        logLn(message);
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
        Customer customer = Loader.loadCustomer(email, password, "User");
        if (customer == null) {
            logLn("Invalid credentials");
            return login();
        }
        return customer;
    }

    static void log(String[] messages, boolean success) {
        if (success) {
            if (LOGGER.isLoggable(java.util.logging.Level.INFO)) {
                LOGGER.info(String.format("SUCCESS: %s%n", messages[0]));
            }
        } else {
            if (LOGGER.isLoggable(java.util.logging.Level.WARNING)) {
                LOGGER.warning(String.format("ERROR: %s%n", messages[1]));
            }
        }
    }

    static Subscription showUserSubscriptions(List<Subscription> subscriptions, String message, boolean informative) {
        String[] subsHeaders = {"ID", "Plan", "Status", "Next charge date", "Payment method"};
        List<String[]> subsInfo = new ArrayList<>();
        for (int i = 0; i < subscriptions.size(); i++) {
            subsInfo.add(new String[] {
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
            showInformation(message, subsHeaders, subsInfo);
            return null;
        }
        int selectedSubscription =  askForSelectionOnTableFormat(message, subsHeaders, subsInfo);
        return subscriptions.get(selectedSubscription);
    }

    static Plan showPlans(List<Plan> plans, String message, boolean informative) {
        String[] plansHeaders = {"ID", "Name", "Description", "Price", "Status"};
        List<String[]> plansInfo =  new ArrayList<>();
        for (int i = 0; i < plans.size(); i++) {
            plansInfo.add(new String[] {
                String.valueOf(i + 1),
                plans.get(i).getName(),
                plans.get(i).getDescription(),
                String.valueOf(plans.get(i).getPrice()),
                plans.get(i).getStatus().toString()
            });
        }
        if (informative) {
            showInformation(message, plansHeaders, plansInfo);
            return null;
        }

        int selectedPlanIndex = askForSelectionOnTableFormat(message, plansHeaders, plansInfo);
        return plans.get(selectedPlanIndex);
    }

    static Transaction processTransaction(User user, Card card, double price, String description) {
        logLn("We will process the following transaction");
        Transaction transaction = new Transaction(
            description,
            user,
            price,
            TransactionStatus.PENDING,
            card
        );
        String[] headers = {"Description", "Price", "Status", "Payment method"};
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[] {
            transaction.getDescription(),
            String.valueOf(transaction.getPrice()),
            transaction.getStatus().toString(),
            transaction.getPaymentMethod().getLastFour() +
            "-" + transaction.getPaymentMethod().getFranchise().toString() +
            "-" + transaction.getPaymentMethod().getExpirationDate()
        });
        printTable(headers, rows);
        int accept = askForSelection("do you agree?", new String [] {"Yes", "No"});
        if (accept != 0) {
            transaction.setStatus(TransactionStatus.REJECTED);
            return transaction;
        }

        GatewaysFactory.getGateway(Gateway.PROJECT_GATEWAY).pay(transaction);
        return transaction;
    }

    static Card showCardsInfo(List<Card> cards, String message, boolean informative) {
        String[] headers = {"ID", "Last four digits", "Expiration date", "Franchise"};
        List<String[]> rows = new ArrayList<>();
        for (Card card : cards) {
            rows.add(new String[] {
                String.valueOf(cards.indexOf(card) + 1),
                card.getLastFour(),
                card.getExpirationDate(),
                card.getFranchise().toString()
            });
        }
        if (informative) {
            showInformation(message, headers, rows);
            return null;
        }
        int selectedCardIndex = askForSelectionOnTableFormat(message, headers, rows);
        return cards.get(selectedCardIndex);
    }

    static void addSubscription(User user) {
        List<Plan> plans = Plan.getAll();
        List<Plan> userPlans = user.getUserSubscribedPlans();
        List<String> userSubscribedPlansNames = new ArrayList<>();
        List<Plan> nonSubscribePlans = new ArrayList<>();

        for (Plan plan : userPlans) {
            userSubscribedPlansNames.add(plan.getName());
        }

        for (int i = 0; i < plans.size(); i++) {
            if (!userSubscribedPlansNames.contains(plans.get(i).getName())) {
                nonSubscribePlans.add(plans.get(i));
            }
        }
        if(nonSubscribePlans.isEmpty()) {
            showUserSubscriptions(user.getSubscriptions(), "You are already subscribed to all available plans", true);
            return;
        }

        Plan selectedPlan = showPlans(nonSubscribePlans, "Select the plan you want to subscribe", false);
        List<Card> creditCards = user.getCreditCards();
        if (creditCards.isEmpty()) {
            logLn("You need to add a credit card to subscribe to a plan");
            user.addCreditCard(addCreditCard());
        }

        Card selectedCard = showCardsInfo(creditCards, "Select the credit card you want to use", false);
        Transaction initialTransaction = user.addSubscription(
            selectedPlan,
            selectedCard
        );

        log(
            new String [] {"Plan subscription successfully","Error subscribing to plan"},
            initialTransaction.getStatus() == TransactionStatus.ACCEPTED
        );

        String[] headers3Fun1 = {"Plan Name", "Transaction Status", "Subscription Status"};
        List<String[]> rows3Fun1 = new ArrayList<>();
        rows3Fun1.add(new String[] {
            selectedPlan.getName(),
            initialTransaction.getStatus().toString(),
            initialTransaction.getStatus() == TransactionStatus.ACCEPTED ? "ACTIVE" : "PENDING",
        });
        printTable(headers3Fun1, rows3Fun1);
    }

    static void addUserCreditCard(User user) {
        Card cardToAdd = addCreditCard();
        Transaction verificationTransaction = processTransaction(user, cardToAdd, 1, "Credit card Validation");
        if (verificationTransaction.getStatus() != TransactionStatus.ACCEPTED) {
            logLn("Invalid credit card");
            return;
        }
        boolean creditCardAdded = user.addCreditCard(cardToAdd);
        log(new String [] {"Credit card added successfully", "Invalid credit card"}, creditCardAdded);
        showCardsInfo(user.getCreditCards(), "", true);
    }

    static void changeSubscriptionPaymentMethod(User user) {
        Subscription selectedSubscription = showUserSubscriptions(user.getSubscriptions(), "Select the subscription you want to change the payment method", false);
        Card newCard = addCreditCard();
        boolean paymentMethodChanged = user.changeSubscriptionPaymentMethod(selectedSubscription, newCard);
        log(new String [] {"Payment method changed successfully", "Error changing payment method"}, paymentMethodChanged);
    }

    static void runFeature(User user, Admin admin) {
        logLn("--------------------------------------------------------");
        List<Subscription> userInactiveSubscriptions = user.getInactiveSubscriptions();
        if (!userInactiveSubscriptions.isEmpty()) {
            showUserSubscriptions(
                userInactiveSubscriptions,
                "The Following Subscriptions will be suspended after its next charge date ",
                true
            );
            logLn("");
        }
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
                List<WithId> withIdPlansList = Repository.loadAllObjectInDirectory("Plan");
                List<Plan> systemPlans = new ArrayList<>();
                for (WithId withId : withIdPlansList) {
                    if (withId instanceof Plan plan && plan.getStatus() == PlanStatus.ACTIVE) {
                        systemPlans.add(plan);
                    }
                }
                Plan planToDelete = showPlans(systemPlans, "Select the plan you want to delete", false);
                List<Subscription> subscriptions = Plan.inactivateSubscriptions(planToDelete);
                String[] headers = {"ID", "Status"};
                List<String[]> rows = new ArrayList<>();
                for (Subscription subscription : subscriptions) {
                    rows.add(new String[] {
                        subscription.getId(),
                        subscription.getStatus().toString(),
                    });
                }
                showInformation("Subscriptions inactivated", headers, rows);
                planToDelete.setStatus(PlanStatus.INACTIVE);
                admin.deletePlan(planToDelete);
                runFeature(user, admin);
                break;

            case 4: // Pay subscription
                Subscription subsToPay = showUserSubscriptions(user.getSubscriptions(), "Select the subscription you want to pay", false);
                Transaction transaction = processTransaction(
                    user,
                    subsToPay.getPaymentMethod(),
                    subsToPay.getPlan().getPrice(),
                    subsToPay.getPlan().getName()
                );
                subsToPay.processPayment(transaction, subsToPay.getGateway());
                boolean charged = transaction.getStatus() == TransactionStatus.ACCEPTED;
                log(new String [] {"Subscription charged successfully", "Error charging subscription"}, charged);
                runFeature(user, admin);
                break;

            case 5:
                logLn("Thanks for using our service");
                break;

            default:
                logLn("Invalid selection");
                runFeature(user, admin);
        }
    }

    public static void main(String[] args) {
        Loader loader = new Loader("JDoe", "PASS");
        loader.loadData();

        // LOGIN
        Customer customer = login();
        if (customer == null) {
            logLn("Invalid credentials");
            return;
        }

        runFeature(loader.getSystemUser(), loader.getSystemAdmin());
    }
}

