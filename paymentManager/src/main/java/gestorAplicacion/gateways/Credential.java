package gestorAplicacion.gateways;

import gestorAplicacion.WithId;

public class Credential extends WithId {
    private final String PUBLIC_KEY;
    private final String PRIVATEKEY;
    private final Gateway GATEWAY;

    public Credential(String publicKey, String privateKey, Gateway gateway) {
        super(gateway.toString());
        this.PUBLIC_KEY = publicKey;
        this.PRIVATEKEY = privateKey;
        this.GATEWAY = gateway;
    }

    public String getPublicKey() {
        return PUBLIC_KEY;
    }

    public String getPrivateKey() {
        return PRIVATEKEY;
    }

    public Gateway getGateway() {
        return GATEWAY;
    }
}
