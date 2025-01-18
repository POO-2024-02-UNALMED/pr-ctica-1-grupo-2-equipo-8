package gestorAplicacion.gateways;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class GatewaysFactory {
    private static Map<Gateway, IGateway> gateways = new EnumMap<>(Gateway.class);

    private GatewaysFactory(Gateway gateway) {
        switch (gateway) {
            case OTHER:
                break;
            case PROJECT_GATEWAY:
                gateways.put(gateway, new ProjectGateway());
                break;
            default:
                break;
        }
    }

    private GatewaysFactory(List<Gateway> gatewaysToAdd) {
        iterateAndAdd(gatewaysToAdd);
    }

    private static void iterateAndAdd(List<Gateway> gatewaysToAdd) {
        for (Gateway gateway : gatewaysToAdd) {
            gateways.put(gateway, new ProjectGateway());
        }
    }

    public static IGateway getGateway(Gateway gateway) {
        return gateways.get(gateway);
    }

    public static void initializeGateway(Gateway gateway) {
        if (gateways.isEmpty()) {
            new GatewaysFactory(gateway);
        } else {
            GatewaysFactory.gateways.put(gateway, new ProjectGateway());
        }
    }

    public static void initializeGateways(List<Gateway> gatewaysAndCredentials) {
        if (gateways.isEmpty()) {
            new GatewaysFactory(gatewaysAndCredentials);
        } else {
            iterateAndAdd(gatewaysAndCredentials);
        }
    }
}
