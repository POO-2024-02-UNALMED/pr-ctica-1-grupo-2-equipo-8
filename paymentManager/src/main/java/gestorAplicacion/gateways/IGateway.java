package gestorAplicacion.gateways;

import gestorAplicacion.customers.User;
import gestorAplicacion.transactions.Card;
import gestorAplicacion.transactions.Transaction;

public interface IGateway {
    Transaction pay(Transaction transaction);
    Card addCreditCard(String cardNumber, String cardHolder, String expirationDate, String cvv, User user);
    boolean authenticated();
    public boolean deleteCard(Card card);
    public default boolean validate(String cardNumber, String cardHolder, String expirationDate, String cvv) {
        return cardNumber.length() > 4
                && cardHolder.length() > 3
                && expirationDate.matches("\\d{2}/\\d{2}")
                && cvv.length() > 2 && cvv.length() < 5;
    }
}
