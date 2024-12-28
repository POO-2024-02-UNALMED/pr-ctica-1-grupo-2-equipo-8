package gestorAplicacion.gateways;

public abstract class Authenticate {
    protected static String AUTHENTICATIONTOKEN = null;

    public void setAuthenticationToken(String token){
        AUTHENTICATIONTOKEN = token;
    }
    public String getAuthenticationToken(){
        return AUTHENTICATIONTOKEN;
    }

}
