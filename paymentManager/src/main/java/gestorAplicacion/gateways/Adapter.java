package gestorAplicacion.gateways;

import gestorAplicacion.transactions.Card;

public class Adapter {
    private final Gateway gateway;

    public Adapter(Gateway gateway) {
        this.gateway = gateway;
    }

    public void deleteCard(Card card) {
        switch (gateway) {
            case Gateway.EPAYCO, Gateway.STRIPE, Gateway.MERCADOPAGO:
                break;
            case Gateway.CUSTOM:
                new Custom().deleteCard(card);
                break;
            default:
                throw new AssertionError();
        }
    }
    
}
