package gestorAplicacion.customers;

import gestorAplicacion.WithId;

public class Customer extends WithId {
    protected String email;
    protected String password;
    protected DocumentType documentType;
    protected String documentNumber;

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
