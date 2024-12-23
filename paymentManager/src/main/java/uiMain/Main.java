package uiMain;
import gestorAplicacion.test.Test;
import baseDatos.Repository;


public class Main {
    public static void main(String[] args) {
        Repository.createTempDirectory();
        String id = "1234567890";
        Test test = new Test(id, "John", 25);
        System.out.println(Repository.save(test));
        Test test2 = (Test) Repository.load("Test",id);
        System.out.println(test2.getName());
        System.out.println(test2.getAge());
        System.out.println(test2);
    }
}
