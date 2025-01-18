package gestorAplicacion.gateways;

import baseDatos.Repository;

public abstract class Authenticate {
    protected final String AUTHENTICATION_TOKEN;

    protected Authenticate(Gateway gateway) {
        Credential credential = (Credential) Repository.load("Credential", gateway.toString());
        // simulate request to the gateway to authenticate
        this.AUTHENTICATION_TOKEN = credential.getPublicKey() + credential.getPrivateKey();
    }
    public String getAuthenticationToken(){
        return AUTHENTICATION_TOKEN;
    }
}
