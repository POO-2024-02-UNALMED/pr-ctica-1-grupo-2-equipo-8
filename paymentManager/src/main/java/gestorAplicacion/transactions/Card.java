package gestorAplicacion.transactions;
import gestorAplicacion.Notificacion;
import gestorAplicacion.WithId;

public class Card extends WithId {

    private  double saldo;
    private int cvs;
    private String fechaVencimiento;
    private String number;

    public Card(String id, double saldo, int cvs, String fechaVencimiento, String number) {
        super(id);
        this.saldo = saldo;
        this.cvs = cvs;
        this.fechaVencimiento = fechaVencimiento;
        this.number = number;
    }  

    
    public boolean validarSaldo(double precio) {
        if (saldo > precio) {
            return true;
            
        }
        else{
            Notificacion.sendNotification(true, "Saldo insuficiente", "No se puede realizar la transaccion");}
        return false;
    }
    
}
