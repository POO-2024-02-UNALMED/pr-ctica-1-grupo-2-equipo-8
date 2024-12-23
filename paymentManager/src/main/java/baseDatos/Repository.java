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
    private static File directoryFile = new File(
        workingDirectory + "/paymentManager/src/main/java/baseDatos/temp"
    );

    public static void createDirectory() {
        if (!directoryFile.exists()) {
            directoryFile.mkdir();
        }
    }

    public static boolean save(WithId object) {
        String objectClass = object.getClass().getSimpleName();
        String objectId = object.getId();
        String fileName = objectClass + objectId;

        File file = new File(directoryFile.getAbsolutePath() + "/" + fileName);
        if (!file.exists()) {
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                    new FileOutputStream(directoryFile.getAbsolutePath() + "/" + fileName))) {
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

    public static WithId load(String path) {
        WithId object = null;
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream
                (directoryFile.getAbsolutePath() + "/" + path))) {
            object = (WithId) objectInputStream.readObject();
        } catch (FileNotFoundException e) {
            logger.warning("File not found");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return object;
    }

    public static boolean delete(String path) {
        File file = new File(directoryFile.getAbsolutePath() + path);
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
        String objectClass = object.getClass().getSimpleName();
        String objectId = object.getId();
        String fileName = objectClass + objectId;
        File file = new File(directoryFile.getAbsolutePath() + "/" + fileName);
        if (file.exists()) {
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                    new FileOutputStream(directoryFile.getAbsolutePath() + "/" + fileName))) {
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
