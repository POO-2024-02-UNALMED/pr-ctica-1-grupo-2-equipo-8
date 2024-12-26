package gestorAplicacion.customers;

import java.util.ArrayList;
import java.util.List;

import gestorAplicacion.Plan;
import gestorAplicacion.Suscription;

public class User extends Customer{

    List<Card> credicards = new ArrayList<>();

    public User(String name, String email, String password, DocumentType documentType, String documentId) {
        super(name, email, password, documentType, documentId);

    }

    public Suscription addSubscription() {
        Plan plan = new Plan("1", "Plan1", "Plan1", 100, 1, new Admin[]{new Admin("John Doe", "dafwa", "", DocumentType.CC, "1234567890")});
       if (hasCreditCard()) {
           Suscription suscription = new Suscription("1", "Plan1", "Plan1", 100, 1);
              suscription.procesarPago(this);
            return suscription;
        }
       return null;
    }

    private boolean hasCreditCard() {
        // TODO implement here
        return true;
    }

    public void addCreditCard(Card card) {
        credicards.add(card);
    }

     
    
}
