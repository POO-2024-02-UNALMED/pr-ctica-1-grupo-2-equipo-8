package uiMain;
import gestorAplicacion.test.Test;
import gestorAplicacion.customers.Admin;
import gestorAplicacion.customers.DocumentType;
import baseDatos.Repository;


public class Main {

    static void logObject(Object object) {
        System.out.println(object);
    }

    public static void main(String[] args) {

        Repository.createTempDirectory();
        String id = "1234567890";
        Test test = new Test(id, "John", 25);
        logObject(Repository.save(test));
        Test test2 = (Test) Repository.load("Test",id);
        logObject(test2.getName());
        logObject(test2.getAge());
        logObject(test2);

        Admin admin = new Admin(
            "John Doe",
            "jdoe@gmail.com",
            "AVERYSECUREPASSWORD",
            DocumentType.CC,
            "1234567890"
        );
        Repository.delete(admin);

        logObject(Repository.save(admin));
        Admin admin2 = (Admin) Repository.load(
            "Admin",
            gestorAplicacion.WithId.createId(
                "jdoe@gmail.com",
                "AVERYSECUREPASSWORD"
            )
        );
        logObject(admin2.getName());
        logObject(admin2.getEmail());
        logObject(admin2.getPassword());
        logObject(admin2);

        /* remove */
        Repository.delete(test);
    }
}
