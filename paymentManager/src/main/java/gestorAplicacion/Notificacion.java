package gestorAplicacion;

public class Notificacion{

    public static void sendNotification(boolean isError, String message, String description) {
        if (isError) {
            System.out.println("ERROR: " + message);
        }
        else {
            System.out.println("NOTIFICACION: " + message);
        }
        System.out.println("Descripcion: " + description);
    }
    public static void sendNotification(boolean isError, String message) {
        if (isError) {
            System.out.println("ERROR: " + message);
        }
        else {
            System.out.println("NOTIFICACION: " + message);
        }
    }
}
