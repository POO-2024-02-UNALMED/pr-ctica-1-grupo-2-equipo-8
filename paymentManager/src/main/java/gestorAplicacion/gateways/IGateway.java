package gestorAplicacion.gateways;

import gestorAplicacion.transactions.Card;
import gestorAplicacion.transactions.Transaction;

public interface IGateway {
    Transaction pay(Transaction transaction);
    Card addCreditCard(String cardNumber, String cardHolder, String expirationDate, String cvv);
    boolean authenticated();
    public boolean deleteCard(Card card);
}


