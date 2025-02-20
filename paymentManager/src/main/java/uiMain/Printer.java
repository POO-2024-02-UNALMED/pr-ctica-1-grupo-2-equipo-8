package uiMain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import gestorAplicacion.customers.User;
import gestorAplicacion.plan.Plan;
import gestorAplicacion.plan.Subscription;
import gestorAplicacion.transactions.Card;

public abstract class Printer {
    private Printer() {
        // Private constructor to hide the implicit public one
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
            Table.showInformation(message, subsHeaders, subsInfo);
            return null;
        }
        int selectedSubscription =  Command.askForSelectionOnTableFormat(message, subsHeaders, subsInfo);
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
            Table.showInformation(message, plansHeaders, plansInfo);
            return null;
        }

        int selectedPlanIndex = Command.askForSelectionOnTableFormat(message, plansHeaders, plansInfo);
        return plans.get(selectedPlanIndex);
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

    static void showInactiveSubscriptionNotification(User user) {
        List<Subscription> userInactiveSubscriptions = user.getInactiveSubscriptions();
        List<String[]> subscriptionsToShow = new ArrayList<>();
        String[] subsHeaders = {"ID", "Plan", "Status", "Next charge date", "Suspension date"};


        for (Subscription subscription : userInactiveSubscriptions) {
            subscriptionsToShow.add(new String[] {
                subscription.getId(),
                subscription.getPlan().getName(),
                subscription.getStatus().toString(),
                subscription.getNextChargeDate().toString(),
                subscription.getSuspensionDate().toString()
            });
        }
        if (!userInactiveSubscriptions.isEmpty()) {
            Command.logLn("The Following Subscriptions will be suspended after its next charge date ");
            Table.print(subsHeaders, subscriptionsToShow);
            Command.logLn();
        }
    }

    static void showSubscriptionsNearToChargeDate(User user) {
        List<Subscription> userInactiveSubscriptions = user.getSubscriptions();
        List<Subscription> subscriptionsNearToChargeDate = new ArrayList<>();
        for (Subscription subscription : userInactiveSubscriptions) {
            if (subscription.getNextChargeDate().isBefore(LocalDate.now().plusDays(3))) {
                subscriptionsNearToChargeDate.add(subscription);
            }
        }
        if (!subscriptionsNearToChargeDate.isEmpty()) {
            showUserSubscriptions(
                subscriptionsNearToChargeDate,
                "The following subscriptions are near to their next charge date",
                true
            );
            Command.logLn();
        }
    }
}
