package gestorAplicacion.gateways;

import gestorAplicacion.transactions.Card;
import gestorAplicacion.transactions.Transaction;

public class Adapter implements IAdapter{
    private final Gateway gateway;

    public Adapter(Gateway gateway) {
        this.gateway = gateway;
    }

    public boolean autenticate(String publicKey, String privateKey) {
        switch (gateway) {
            case Gateway.EPAYCO, Gateway.STRIPE, Gateway.MERCADOPAGO:
                return true;
            case Gateway.CUSTOM:
                return new Custom().authenticate(publicKey, privateKey);
            default:
                return false;
        }
    }

    public Card addCreditCard( String cardNumber, String expirationDate, String email, String cvv)
    {
        switch (gateway) {
            case Gateway.EPAYCO, Gateway.STRIPE, Gateway.MERCADOPAGO:
                return new Card(
                    cardNumber.substring(11, 15),
                    expirationDate,
                    Card.getFranchise(cardNumber),
                    "", gateway
                );
            case Gateway.CUSTOM:
                return new Custom().addCreditCard(cardNumber, email, expirationDate,cvv);
            default:
                return null;
        }
    }


    public boolean deleteCard(Card card) {
        switch (gateway) {
            case Gateway.EPAYCO, Gateway.STRIPE, Gateway.MERCADOPAGO:
                return true;
            case Gateway.CUSTOM:
                new Custom().deleteCard(card);
                return true;
            default:
                return false;
        }
    }

    public Transaction pay(Transaction transaction) {
        switch (gateway) {
            case Gateway.EPAYCO, Gateway.STRIPE, Gateway.MERCADOPAGO:
                return transaction;
            case Gateway.CUSTOM:
                new Custom().pay(transaction);
                return transaction;
            default:
                return null;
        }
    }

    
    
}
