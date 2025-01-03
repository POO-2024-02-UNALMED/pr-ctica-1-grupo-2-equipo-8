package gestorAplicacion.gateways;

import java.util.EnumMap;
import java.util.Map;

public class GatewaysFactory {
    private static Map<Gateway, IGateway> gateways = new EnumMap<>(Gateway.class);

    private GatewaysFactory(Gateway gateway, String publicKey, String privateKey) {
        switch (gateway) {
            case EPAYCO, STRIPE, MERCADOPAGO:
                break;
            case CUSTOM:
                gateways.put(gateway, new Custom(publicKey, privateKey));
                break;
            default:
                break;
        }
    }

    private GatewaysFactory(Map<Gateway, Credential> gatewaysAndCredentials) {
        for (Map.Entry<Gateway, Credential> entry : gatewaysAndCredentials.entrySet()) {
            switch (entry.getKey()) {
                case EPAYCO, STRIPE, MERCADOPAGO:
                    break;
                case CUSTOM:
                    gateways.put(entry.getKey(), new Custom(entry.getValue().getPublicKey(), entry.getValue().getPrivateKey()));
                    break;
                default:
                    break;
            }
        }
    }

    public static IGateway getGateway(Gateway gateway) {
        return gateways.get(gateway);
    }

    public static void initializeGateway(Gateway gateway, String publicKey, String privateKey) {
        new GatewaysFactory(gateway, publicKey, privateKey);
    }

    public static void initializeGateways(Map<Gateway, Credential> gatewaysAndCredentials) {
        new GatewaysFactory(gatewaysAndCredentials);
    }
}
