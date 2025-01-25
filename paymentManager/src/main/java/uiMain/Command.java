package uiMain;

import java.util.List;

public abstract class Command {
    private Command() {
        // Private constructor to hide the implicit public one
    }
    static final String INVALID_OPTION_MESSAGE = "Invalid option, please select a valid option";

    static void logLn(Object object) {
        System.out.println(object);
    }

    static void log(Object object) {
        System.out.print(object.toString() + " ");
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
        logLn("");
        return selection;
    }

    static int askForSelectionOnTableFormat(String message, String[] headers, List<String[]> rows) {
        Table.showInformation(message, headers, rows);
        int selection = Integer.parseInt(System.console().readLine()) - 1;
        if (selection < 0 || selection >= rows.size()) {
            logLn(INVALID_OPTION_MESSAGE);
            return askForSelectionOnTableFormat(message, headers, rows);
        }
        logLn("");
        return selection;
    }

    static String askForPassword (String message) {
        logLn(message);
        return new String(System.console().readPassword());
    }
}
