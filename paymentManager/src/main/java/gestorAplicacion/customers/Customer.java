package gestorAplicacion.customers;

import java.util.ArrayList;
import java.util.concurrent.Flow;

import gestorAplicacion.plan.Subscription;
import gestorAplicacion.WithId;
import gestorAplicacion.transactions.Card;
import jdk.jshell.JShell;

public class Customer extends WithId {
    protected String email;
    protected String password;
    protected DocumentType documentType;
    protected String documentNumber;
    private ArrayList<Subscription> subscriptions;

    public Customer(
        String email,
        String password,
        DocumentType documentType,
        String documentNumber
    ) {
        super(createId(email, password));
        this.email = email;
        this.password = password;
        this.documentType = documentType;
        this.documentNumber = documentNumber;
    }

    public boolean Validate(){
        String emailPattern = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        if(!email.matches(emailPattern)){
            return false;
        }
        else if(password.length() < 8){
            return false;
        }
        else if(documentNumber.length() < 6){
            return false;
        }
        return true;
    }

    public ArrayList<Subscription> getSubscriptions() {
        return subscriptions;
    }

    public void addSubscription(Subscription subscription) {
        if (subscriptions == null) {
            subscriptions = new ArrayList<Subscription>();
        }
        subscriptions.add(subscription);
    }         

    public void deleteSubscription(Subscription subscription) {
        if (subscriptions != null) {
            subscriptions.remove(subscription);
        }
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }
}
