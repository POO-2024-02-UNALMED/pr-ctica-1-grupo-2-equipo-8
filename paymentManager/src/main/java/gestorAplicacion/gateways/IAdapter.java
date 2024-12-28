package gestorAplicacion.gateways;

import gestorAplicacion.transactions.Card;
import gestorAplicacion.transactions.Transaction;

public interface IAdapter {
    
    Transaction pay(Transaction transaction);
    Card addCreditCard(String cardNumber, String cardHolder, String expirationDate, String cvv);
    boolean autenticate(String publicKey, String privateKey);
    public boolean deleteCard(Card card);
}


