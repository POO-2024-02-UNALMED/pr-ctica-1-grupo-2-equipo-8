package baseDatos;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.util.logging.Logger;

import gestorAplicacion.WithId;

public class Repository {
    private Repository() {
        // Private constructor to hide the implicit public one
    }

    private static final Logger logger = Logger.getLogger(Repository.class.getName());
    private static String rootDirectory = System.getProperty("user.dir");
    private static String operativeSystem = System.getProperty("os.name");
    private static String tempDirectoryRelativePath = operativeSystem.contains("Mac")
        ? "/PaymentManager/src/main/java/baseDatos/temp/"
        : "/src/main/java/baseDatos/temp/";
    private static String tempDirectoryPath = rootDirectory + (new File(tempDirectoryRelativePath)).getPath();

    public static void createDirectory(File directory) {
        if (!directory.exists()) {
            directory.mkdir();
        }
    }

    public static void createTempDirectory() {
        createDirectory(new File(tempDirectoryPath));
    }

    private static String getObjectFilePath(WithId object) {
        String objectClass = object.getClass().getSimpleName();
        String objectId = object.getId();
        String classPath = new File(tempDirectoryPath + File.separator + objectClass).getPath();
        createDirectory(new File(classPath));

        return new File(classPath + File.separator + objectId).getPath();
    }

    public static boolean save(WithId object) {
        String objectPath = getObjectFilePath(object);
        File file = new File(objectPath);
        if (!file.exists()) {
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                    new FileOutputStream(objectPath))) {
                objectOutputStream.writeObject(object);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        logger.warning("File already exists, use update method instead");
        return false;
    }

    public static WithId load(String objectClass, String id) {
        WithId object = null;
        String directory = new File(tempDirectoryPath + File.separator + objectClass + File.separator + id).getPath();
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream
                (directory))) {

            object = (WithId) objectInputStream.readObject();
        } catch (FileNotFoundException e) {
            logger.warning("File not found");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return object;
    }

    public static boolean delete(WithId object) {
        File file = new File(getObjectFilePath(object));
        if (file.exists()) {
            try {
                Files.delete(file.toPath());
                return true;
            } catch (IOException | SecurityException e) {
                logger.warning("Failed to delete file: " + e.getMessage());
                return false;
            }
        }

        return false;
    }

    public static boolean update (WithId object) {
        String fileName = getObjectFilePath(object);
        File file = new File(tempDirectoryPath + fileName);
        if (file.exists()) {
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                    new FileOutputStream(tempDirectoryPath + fileName))) {
                objectOutputStream.writeObject(object);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }
}
