package gestorAplicacion.plan;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import baseDatos.Repository;
import gestorAplicacion.WithId;


public class Plan extends WithId {
    private String name;
    private String description;
    private double price;
    private PlanStatus status;

    public Plan(String name, String description, double price) {
        super(name);
        this.name = name;
        this.description = description;
        this.price = price;
        this.status = PlanStatus.ACTIVE;
    }

    public Plan(String id, String name, String description, double price) {
        super(id);
        this.name = name;
        this.description = description;
        this.price = price;
        this.status = PlanStatus.ACTIVE;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public static List<Plan> getAll() {
        List<WithId> withIdList = Repository.loadAllObjectInDirectory("Plan");
        List<Plan> planList = new ArrayList<>();
        for (WithId withId : withIdList) {
            if (withId instanceof Plan plan && plan.getStatus() == PlanStatus.ACTIVE) {
                planList.add(plan);
            }
        }
        return planList;
    }

    public static List<Plan> getInactivePlans() {
        List<WithId> withIdList = Repository.loadAllObjectInDirectory("Plan");
        List<Plan> planList = new ArrayList<>();
        for (WithId withId : withIdList) {
            if (withId instanceof Plan plan && plan.getStatus() == PlanStatus.INACTIVE) {
                planList.add(plan);
            }
        }
        return planList;
    }

    public static List<Subscription> getSubscriptions(Plan plan) {
        List<WithId> withIdList = Repository.loadAllObjectInDirectory("Subscription" + File.separator + plan.getName());
        List<Subscription> subscriptionList = new ArrayList<>();
        for (WithId withId : withIdList) {
            if (withId instanceof Subscription subscription) {
                subscriptionList.add(subscription);
            }
        }
        return subscriptionList;
    }

    public static List<Subscription> inactivateSubscriptions(Plan plan) {
        List<WithId> withIdList = Repository.loadAllObjectInDirectory("Subscription" + File.separator + plan.getName());
        List<Subscription> subscriptionList = new ArrayList<>();
        for (WithId withId : withIdList) {
            if (withId instanceof Subscription subscription) {
                subscription.setStatus(SubscriptionStatus.INACTIVE);
                subscription.setSuspensionDate(subscription.getNextChargeDate());
                subscription.setNextChargeDate(LocalDate.MIN);
                Repository.update(subscription, "Subscription" + File.separator + plan.getName());
                subscriptionList.add(subscription);
            }
        }
        return subscriptionList;
    }

    public static Plan getPlan(String name) {
        return (Plan) Repository.load("Plan", name);
    }

    public PlanStatus getStatus() {
        return status;
    }

    public void setStatus(PlanStatus status) {
        this.status = status;
    }
}
