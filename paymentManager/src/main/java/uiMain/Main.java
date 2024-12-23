package uiMain;
import gestorAplicacion.test.Test;
import baseDatos.Repository;


public class Main {
    public static void main(String[] args) {
        Repository.createDirectory();
        Test test = new Test("1", "John", 25);
        Repository.save(test);
        Test test2 = (Test) Repository.load("Test1");
        System.out.println(test2.getName());
        System.out.println(test2.getAge());
        System.out.println(test2);
    }
}
