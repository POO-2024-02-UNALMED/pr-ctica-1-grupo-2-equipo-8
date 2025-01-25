package gestorAplicacion.transactions;

import gestorAplicacion.WithId;
import gestorAplicacion.customers.User;
import gestorAplicacion.gateways.Gateway;
import gestorAplicacion.gateways.GatewaysFactory;

public class Card extends WithId {
    private String dueDate;
    private String lastFour;
    private Franchise franchise;
    private final String TOKEN;
    private Gateway gateway;
    private transient User cardOwner;

    public Card(
        String lastFour,
        String dueDate,
        Franchise franchise,
        String token,
        Gateway gateway,
        User cardOwner
    ) {
        super(createId(dueDate, lastFour));
        this.dueDate = dueDate;
        this.lastFour = lastFour;
        this.franchise = franchise;
        this.TOKEN = token;
        this.gateway = gateway;
        this.cardOwner = cardOwner;
    }

    public String getExpirationDate() {
        return dueDate;
    }

    public String getLastFour() {
        return lastFour;
    }

    public Franchise getFranchise() {
        return franchise;
    }

    public String getTOKEN() {
        return TOKEN;
    }

    public void delete() {
        GatewaysFactory.getGateway(this.gateway).deleteCard(this);
    }

    public User getCardOwner() {
        return cardOwner;
    }

    public static Franchise getFranchise(String number) {
        if (number.startsWith("4")) {
            return Franchise.VISA;
        } else if (number.startsWith("5")) {
            return Franchise.MASTERCARD;
        } else if (number.startsWith("6")) {
            return Franchise.DISCOVER;
        } else {
            return Franchise.UNKNOWN;
        }
    }
}