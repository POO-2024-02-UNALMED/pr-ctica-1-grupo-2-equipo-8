package gestorAplicacion.gateways;

import java.io.File;

import baseDatos.Repository;
import gestorAplicacion.customers.User;
import gestorAplicacion.transactions.Card;
import gestorAplicacion.transactions.Transaction;
import gestorAplicacion.transactions.TransactionStatus;

public class ProjectGateway extends Authenticate implements IGateway {

    public ProjectGateway() {
        super(Gateway.PROJECT_GATEWAY);
    }

    public Transaction pay(Transaction transaction) {
        transaction.setStatus(TransactionStatus.ACCEPTED);
        return transaction;
    }

    public boolean authenticated() {
        return this.AUTHENTICATION_TOKEN != null;
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

    public Card addCreditCard(String cardNumber, String cardHolder, String expirationDate, String cvv, User user) {
        if (!validate(cardNumber, cardHolder, expirationDate, cvv)) {
            return null;
        }
        Card card = new Card(
            cardNumber.substring(cardNumber.length() - 4, cardNumber.length()),
            expirationDate,
            Card.getFranchise(cardNumber),
            generateCardToken(cardNumber, cardHolder, expirationDate), Gateway.PROJECT_GATEWAY,
            user
        );

        Repository.save(card, "Card" + File.separator + user.getId());

        return card;
    }

    public boolean deleteCard(Card card) {
        // delete card from the database
        return true;
    }
}
