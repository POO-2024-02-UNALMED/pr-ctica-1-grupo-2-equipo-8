package gestorAplicacion.transactions;

import gestorAplicacion.WithId;

public class Card extends WithId {
    private String dueDate;
    private String lastFour;
    private Franchise franchise;
    private final String TOKEN;

    public Card( String lastFour, String dueDate, Franchise franchise, String token) {
        super(WithId.createId(dueDate, lastFour));
        this.dueDate = dueDate;
        this.lastFour = lastFour;
        this.franchise = franchise;
        this.TOKEN = token;
    }

    public String getDueDate() {
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