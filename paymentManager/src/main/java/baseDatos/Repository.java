package baseDatos;
import java.io.File;
import java.nio.file.Files;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Logger;

import gestorAplicacion.WithId;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;

public class Repository {
    private Repository() {
        // Private constructor to hide the implicit public one
    }
    private static final Logger logger = Logger.getLogger(Repository.class.getName());
    private static String workingDirectory = (new File("")).getAbsolutePath();
    private static String tempDirectoryPath =  workingDirectory + "/paymentManager/src/main/java/baseDatos/temp/";

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
        String classPath = tempDirectoryPath + objectClass;
        createDirectory(new File(classPath));
        return classPath + "/" + objectId;
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
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream
                (tempDirectoryPath + "/" + objectClass + "/" + id))) {
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
