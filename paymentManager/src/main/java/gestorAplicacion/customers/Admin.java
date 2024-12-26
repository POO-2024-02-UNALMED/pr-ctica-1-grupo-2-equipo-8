package gestorAplicacion.customers;

public class Admin extends Customer {

    public Admin(
        String name,
        String email,
        String password,
        DocumentType documentType,
        String documentNumber
    ) {
        super(name, email, password, documentType, documentNumber);
    }
}
