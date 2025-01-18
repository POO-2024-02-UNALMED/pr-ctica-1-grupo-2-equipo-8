package gestorAplicacion.gateways;

import gestorAplicacion.WithId;

public class Credential extends WithId {
    private final String PUBLICKEY;
    private final String PRIVATEKEY;
    private final Gateway GATEWAY;

    public Credential(String publicKey, String privateKey, Gateway gateway) {
        super(gateway.toString());
        this.PUBLICKEY = publicKey;
        this.PRIVATEKEY = privateKey;
        this.GATEWAY = gateway;
    }

    public String getPublicKey() {
        return PUBLICKEY;
    }

    public String getPrivateKey() {
        return PRIVATEKEY;
    }

    public Gateway getGateway() {
        return GATEWAY;
    }
}
