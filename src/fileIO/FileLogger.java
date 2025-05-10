package fileIO;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FileLogger {
    private static final String LOG_FILE = "src/LOGS/log.txt";
    private static final String historyFile = "src/LOGS/history_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".txt";

    public static void log(String message) {
        writeToFile(LOG_FILE, "[LOG] " + timestamped(message));
    }

    public static void error(String message) {
        writeToFile(LOG_FILE, "[ERROR] " + timestamped(message));
    }

    public static void history(String message) {
        writeToFile(historyFile, timestamped(message));
    }

    private static String timestamped(String msg) {
        return "[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "] " + msg;
    }

    private static void writeToFile(String fileName, String message) {
        try {
            java.nio.file.Path path = java.nio.file.Paths.get(fileName).getParent();
            if (!java.nio.file.Files.exists(path)) {
                java.nio.file.Files.createDirectories(path);
            }

            FileOutputStream fos = new FileOutputStream(fileName, true);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            BufferedWriter writer = new BufferedWriter(osw);
            writer.write(message + "\n");
            writer.close();
        } catch(IOException e){
            System.out.println("Failed to write to " + fileName + ": " + e.getMessage());
        }
    }

}
