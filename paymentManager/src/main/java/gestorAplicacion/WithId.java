package gestorAplicacion;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.logging.Logger;

public abstract class WithId implements Serializable {

    private String id;

    protected WithId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    protected void setId(String id) {
        this.id = id;
    }

    protected static String createId(String attribute1, String attribute2) {
        Logger logger = Logger.getLogger(WithId.class.getName());
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            messageDigest.update((attribute1 + attribute2).getBytes());
            byte[] digest = messageDigest.digest();
            Formatter formatter = new Formatter();
            for (byte b : digest) {
                formatter.format("%02x", b);
            }
            String id = formatter.toString();
            formatter.close();
            return id;
        } catch (NoSuchAlgorithmException e) {
            logger.severe("SHA-1 algorithm not found");
            return null;
        }
    }
}