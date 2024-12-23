package gestorAplicacion.customers;

import gestorAplicacion.WithId;

public class Customer extends WithId {
    private String name;
    private String email;
    private String password;

    public Customer(String name, String email, String password) {
        super(createId(email, password));
        this.name = name;
        this.email = email;
        this.password = password;
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
}
