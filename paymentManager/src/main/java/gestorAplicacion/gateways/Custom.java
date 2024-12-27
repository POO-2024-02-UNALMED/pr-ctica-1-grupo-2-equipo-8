package gestorAplicacion.gateways;

import gestorAplicacion.transactions.Card;
import gestorAplicacion.transactions.Transaction;

public class Custom implements IAdapter {
    public Transaction pay(Transaction transaction) {
        return transaction;
    }

    private static boolean validate(String cardNumber, String cardHolder, String expirationDate, String cvv) {
        return cardNumber.length() == 16
                && !cardHolder.isEmpty()
                && expirationDate.length() == 5
                && cvv.length() == 3;
    }

    private static String generateCardToken(String cardNumber, String cardHolder, String expirationDate) {
        // simulate encryption of the card number, card holder and expiration date to generate a token
        String value =  cardNumber  + cardHolder + expirationDate;
        StringBuilder tokenBuilder = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            tokenBuilder.append((int) value.charAt(i));
        }
        return tokenBuilder.toString();
    }

    public Card addCreditCard(String cardNumber, String cardHolder, String expirationDate, String cvv) {
        if (!validate(cardNumber, cardHolder, expirationDate, cvv)) {
            return null;
        }
        return new Card(
            cardNumber.substring(11, 15),
            expirationDate,
            Card.getFranchise(cardNumber),
            generateCardToken(cardNumber, cardHolder, expirationDate)
        );
    }
}
