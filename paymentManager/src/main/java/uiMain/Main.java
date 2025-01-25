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
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    static Card addCreditCard() {
        String cardNumber = Command.askString("Enter the your credit card number");
        String cardHolder = Command.askString("Enter the card holder name");
        String expirationDate = Command.askString("Enter the due date of your credit card (MM/YY)");
        String cvv = Command.askString("Enter the CVV of your credit card");
        if (!ProjectGateway.validate(cardNumber, cardHolder, expirationDate, cvv)) {
            Command.logLn("Invalid credit card information");
            return addCreditCard();
        }
        ProjectGateway projectGateway = new ProjectGateway();
        return projectGateway.addCreditCard(cardNumber, cardHolder, expirationDate, cvv);
    }


    static Customer login () {
        String email = Command.askString("Enter your email: ");
        String password = Command.askForPassword("Enter your password: ");
        Customer customer = Loader.loadCustomer(email, password, "User");
        if (customer == null) {
            Command.logLn("Invalid credentials");
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

    static Transaction processTransaction(User user, Card card, double price, String description) {
        Command.logLn("We will process the following transaction");
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
        Table.print(headers, rows);
        int accept = Command.askForSelection("do you agree?", new String [] {"Yes", "No"});
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
            Table.showInformation(message, headers, rows);
            return null;
        }
        int selectedCardIndex = Command.askForSelectionOnTableFormat(message, headers, rows);
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
            Printer.showUserSubscriptions(user.getSubscriptions(), "You are already subscribed to all available plans", true);
            return;
        }

        Plan selectedPlan = Printer.showPlans(nonSubscribePlans, "Select the plan you want to subscribe", false);
        List<Card> creditCards = user.getCreditCards();
        if (creditCards.isEmpty()) {
            Command.logLn("You need to add a credit card to subscribe to a plan");
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
        Table.print(headers3Fun1, rows3Fun1);
    }

    static void addUserCreditCard(User user) {
        Card cardToAdd = addCreditCard();
        Transaction verificationTransaction = processTransaction(user, cardToAdd, 1, "Credit card Validation");
        if (verificationTransaction.getStatus() != TransactionStatus.ACCEPTED) {
            Command.logLn("Invalid credit card");
            return;
        }
        boolean creditCardAdded = user.addCreditCard(cardToAdd);
        log(new String [] {"Credit card added successfully", "Invalid credit card"}, creditCardAdded);
        showCardsInfo(user.getCreditCards(), "", true);
    }

    static void changeSubscriptionPaymentMethod(User user) {
        Subscription selectedSubscription = Printer.showUserSubscriptions(user.getSubscriptions(), "Select the subscription you want to change the payment method", false);
        Card newCard = addCreditCard();
        boolean paymentMethodChanged = user.changeSubscriptionPaymentMethod(selectedSubscription, newCard);
        log(new String [] {"Payment method changed successfully", "Error changing payment method"}, paymentMethodChanged);
    }

    static void runFeature(User user, Admin admin) {
        Command.logLn("--------------------------------------------------------");
        List<Subscription> userInactiveSubscriptions = user.getInactiveSubscriptions();
        if (!userInactiveSubscriptions.isEmpty()) {
            Printer.showUserSubscriptions(
                userInactiveSubscriptions,
                "The Following Subscriptions will be suspended after its next charge date ",
                true
            );
            Command.logLn("");
        }
        int feature = Command.askForSelection("Select function", FEATURES);
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
                Plan planToDelete = Printer.showPlans(systemPlans, "Select the plan you want to delete", false);
                List<Subscription> subscriptions = Plan.inactivateSubscriptions(planToDelete);
                String[] headers = {"ID", "Status"};
                List<String[]> rows = new ArrayList<>();
                for (Subscription subscription : subscriptions) {
                    rows.add(new String[] {
                        subscription.getId(),
                        subscription.getStatus().toString(),
                    });
                }
                Table.showInformation("Subscriptions inactivated", headers, rows);
                planToDelete.setStatus(PlanStatus.INACTIVE);
                admin.deletePlan(planToDelete);
                runFeature(user, admin);
                break;

            case 4: // Pay subscription
                Subscription subsToPay = Printer.showUserSubscriptions(user.getSubscriptions(), "Select the subscription you want to pay", false);
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
                Command.logLn("Thanks for using our service");
                break;

            default:
                Command.logLn("Invalid selection");
                runFeature(user, admin);
        }
    }

    public static void main(String[] args) {
        Loader loader = new Loader("JDoe", "PASS");
        loader.loadData();

        // LOGIN
        Customer customer = login();
        if (customer == null) {
            Command.logLn("Invalid credentials");
            return;
        }

        runFeature(loader.getSystemUser(), loader.getSystemAdmin());
    }
}

