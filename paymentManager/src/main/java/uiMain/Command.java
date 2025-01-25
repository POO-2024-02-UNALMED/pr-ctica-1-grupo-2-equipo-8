package uiMain;

import java.util.List;
import java.util.logging.Logger;

public abstract class Command {
    private Command() {
        // Private constructor to hide the implicit public one
    }
    static final String INVALID_OPTION_MESSAGE = "Invalid option, please select a valid option";
    private static final Logger LOGGER = Logger.getLogger(Command.class.getName());


    static void logLn(Object object) {
        System.out.println(object);
    }

    static void logLn() {
        System.out.println("");
    }

    static void log(Object object) {
        System.out.print(object.toString() + " ");
    }

    static void log(String[] messages, boolean success) {
        if (success) {
            if (LOGGER.isLoggable(java.util.logging.Level.INFO)) {
                LOGGER.info(String.format("SUCCESS: %s%n", messages[0]));
            }
        } else {
            if (LOGGER.isLoggable(java.util.logging.Level.WARNING)) {
                LOGGER.warning(String.format("ERROR: %s%n", messages[1]));
            }
        }
    }

    static String askString(String message) {
        log(message);
        return System.console().readLine();
    }

    static int askForSelection (String message, String[] options) {
        logLn(message);
        for (int i = 0; i < options.length; i++) {
            logLn(i + 1 + ". " + options[i]);
        }
        int selection = Integer.parseInt(System.console().readLine()) - 1;
        if (selection < 0 || selection > options.length) {
            logLn(INVALID_OPTION_MESSAGE);
            return askForSelection(message, options);
        }
        logLn();
        return selection;
    }

    static int askForSelection (String message, List<String> options) {
        logLn(message);
        for (int i = 0; i < options.size(); i++) {
            logLn(i + 1 + ". " + options.get(i));
        }
        int selection = Integer.parseInt(System.console().readLine()) - 1;
        if (selection < 0 || selection > options.size()) {
            logLn(INVALID_OPTION_MESSAGE);
            return askForSelection(message, options);
        }
        logLn();
        return selection;
    }

    static int askForSelectionOnTableFormat(String message, String[] headers, List<String[]> rows) {
        Table.showInformation(message, headers, rows);
        int selection = Integer.parseInt(System.console().readLine()) - 1;
        if (selection < 0 || selection >= rows.size()) {
            logLn(INVALID_OPTION_MESSAGE);
            return askForSelectionOnTableFormat(message, headers, rows);
        }
        logLn();
        return selection;
    }

    static String askForPassword (String message) {
        logLn(message);
        return new String(System.console().readPassword());
    }
}
