package gestorAplicacion.gateways;

public abstract class Authenticate {
    protected final String AUTHENTICATION_TOKEN;

    protected Authenticate(String token){
        this.AUTHENTICATION_TOKEN = token;
    }
    public String getAuthenticationToken(){
        return AUTHENTICATION_TOKEN;
    }
}
