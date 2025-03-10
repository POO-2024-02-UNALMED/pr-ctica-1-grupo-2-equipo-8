package uiMain;

import java.util.ArrayList;
import java.util.List;

import baseDatos.Loader;
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
        "Inactivate plan",
        "Pay subscription",
        "Exit"
    };

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

    static Card addCreditCard(User user) {
        String cardNumber = Command.askString("Enter the your credit card number");
        String cardHolder = Command.askString("Enter the card holder name");
        String expirationDate = Command.askString("Enter the due date of your credit card (MM/YY)");
        String cvv = Command.askString("Enter the CVV of your credit card");
        ProjectGateway projectGateway = (ProjectGateway) GatewaysFactory.getGateway(Gateway.PROJECT_GATEWAY);
        Command.logLn();
        if (!projectGateway.validate(cardNumber, cardHolder, expirationDate, cvv)) {
            Command.logLn("Invalid credit card information");
            return addCreditCard(user);
        }
        return projectGateway.addCreditCard(cardNumber, cardHolder, expirationDate, cvv, user);
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
            Printer.showUserSubscriptions(
                user.getSubscriptions(), "You are already subscribed to all available plans", true);
            return;
        }

        Plan selectedPlan = Printer.showPlans(nonSubscribePlans, "Select the plan you want to subscribe", false);
        List<Card> creditCards = user.getCreditCards();
        if (creditCards.isEmpty()) {
            Command.logLn("You need to add a credit card to subscribe to a plan");
            user.addCreditCard(addCreditCard(user));
        }

        Card selectedCard = Printer.showCardsInfo(creditCards, "Select the credit card you want to use", false);
        Transaction initialTransaction = user.addSubscription(
            selectedPlan,
            selectedCard
        );

        Command.log(
            new String [] {"Subscription added successfully","Error subscribing to plan"},
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
        Card cardToAdd = addCreditCard(user);
        Transaction verificationTransaction = processTransaction(user, cardToAdd, 1, "Credit card Validation");
        if (verificationTransaction.getStatus() != TransactionStatus.ACCEPTED) {
            Command.logLn("Invalid credit card");
            return;
        }
        boolean creditCardAdded = user.addCreditCard(cardToAdd);
        Command.log(new String [] {"Credit card added successfully", "Invalid credit card"}, creditCardAdded);
        Printer.showCardsInfo(user.getCreditCards(), "", true);
    }

    static void changeSubscriptionPaymentMethod(User user) {
        Subscription selectedSubscription = Printer.showUserSubscriptions(
            user.getSubscriptions(),
            "Select the subscription you want to change the payment method",
            false
        );
        Card selectedCard = Printer.showCardsInfo(
            user.getCreditCards(),
            "Select the credit card you want to use",
            false
        );
        boolean paymentMethodChanged = selectedSubscription.upsertPaymentMethod(selectedCard);
        Command.log(new String [] {"Payment method changed successfully", "Error changing payment method"}, paymentMethodChanged);
    }

    static void inactivatePlan(Admin admin) {
        List<Plan> systemPlans = Plan.getAll();
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
        admin.inactivate(planToDelete);
    }

    static void paySubscription(User user) {
        Subscription subsToPay = Printer.showUserSubscriptions(
            user.getSubscriptions(),
            "Select the subscription you want to pay",
            false
        );
        Transaction transaction = processTransaction(
            user,
            subsToPay.getPaymentMethod(),
            subsToPay.getPlan().getPrice(),
            subsToPay.getPlan().getName()
        );
        if (transaction.getStatus() == TransactionStatus.REJECTED) {
            Command.logLn("Error charging subscription, you have declined the transaction");
            return;
        }
        subsToPay.processPayment(transaction);
        boolean charged = transaction.getStatus() == TransactionStatus.ACCEPTED;
        Command.log(new String [] {"Subscription charged successfully", "Error charging subscription"}, charged);
    }

    static void run(User user, Admin admin) {
        Command.logLn("#".repeat(100));
        Printer.showInactiveSubscriptionNotification(user);
        Printer.showSubscriptionsNearToChargeDate(user);
        int feature = Command.askForSelection("What will you do?", FEATURES);
        switch (feature) {
            case 0:
                addSubscription(user);
                run(user, admin);
                break;

            case 1:
                addUserCreditCard(user);
                run(user, admin);
                break;

            case 2:
                changeSubscriptionPaymentMethod(user);
                run(user, admin);
                break;

            case 3:
                inactivatePlan(admin);
                run(user, admin);
                break;

            case 4:
                paySubscription(user);
                run(user, admin);
                break;

            case 5:
                Command.logLn("Thanks for using our service");
                break;

            default:
                Command.logLn("Invalid selection");
                run(user, admin);
        }
    }

    public static void main(String[] args) {
        Loader loader = new Loader("JDoe", "PASS", false);
        loader.loadData();

        // LOGIN
        Customer customer = login();
        if (customer == null) {
            Command.logLn("Invalid credentials");
            return;
        }

        run(loader.getSystemUser(), loader.getSystemAdmin());
    }
}

