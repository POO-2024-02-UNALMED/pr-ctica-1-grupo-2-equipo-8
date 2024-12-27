package gestorAplicacion.customers;

public class Admin extends Customer {

    public Admin(
        String email,
        String password,
        DocumentType documentType,
        String documentNumber
    ) {
        super(email, password, documentType, documentNumber);
    }
}
