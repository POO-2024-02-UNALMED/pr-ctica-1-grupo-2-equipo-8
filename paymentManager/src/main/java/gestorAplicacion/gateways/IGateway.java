package gestorAplicacion.gateways;

import gestorAplicacion.customers.User;
import gestorAplicacion.transactions.Card;
import gestorAplicacion.transactions.Transaction;

public interface IGateway {
    Transaction pay(Transaction transaction);
    Card addCreditCard(String cardNumber, String cardHolder, String expirationDate, String cvv, User user);
    boolean authenticated();
    public boolean deleteCard(Card card);
}
