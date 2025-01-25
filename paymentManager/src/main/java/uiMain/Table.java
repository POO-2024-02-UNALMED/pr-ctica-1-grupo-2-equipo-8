package uiMain;

import java.util.List;

public class Table {
    private static void printRow(String[] row, int[] columnWidths) {
        for (int i = 0; i < row.length; i++) {
            System.out.printf("| %-"+columnWidths[i]+"s ", row[i]);
        }
        System.out.println("|");
    }

    private static void printSeparator(int[] columnWidths) {
        for (int width : columnWidths) {
            System.out.print("+");
            System.out.print("-".repeat(width + 2));
        }
        System.out.println("+");
    }

    public static void print(String[] headers, List<String[]> rows) {
        // Determine column widths
        int[] columnWidths = new int[headers.length];
        for (int i = 0; i < headers.length; i++) {
            columnWidths[i] = headers[i].length();
        }
        for (String[] row : rows) {
            for (int i = 0; i < row.length; i++) {
                columnWidths[i] = Math.max(columnWidths[i], row[i].length());
            }
        }

        // Print header
        printRow(headers, columnWidths);
        printSeparator(columnWidths);

        // Print rows
        for (String[] row : rows) {
            printRow(row, columnWidths);
        }
        System.out.println("");
    }

    static void showInformation(String message, String[] headers, List<String[]> rows) {
        System.out.println("");
        System.out.println(message);
        System.out.println("");
        Table.print(headers, rows);
    }
}
