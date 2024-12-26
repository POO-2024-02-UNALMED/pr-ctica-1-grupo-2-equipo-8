package gestorAplicacion.customers;

import gestorAplicacion.WithId;

public class Customer extends WithId {
    protected String name;
    protected String email;
    protected String password;
    protected DocumentType documentType;
    protected String documentNumber;

    public Customer(
        String name,
        String email,
        String password,
        DocumentType documentType,
        String documentNumber
    ) {
        super(createId(email, password));
        this.name = name;
        this.email = email;
        this.password = password;
        this.documentType = documentType;
        this.documentNumber = documentNumber;
    }

    public String getName() {
        return name;
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
