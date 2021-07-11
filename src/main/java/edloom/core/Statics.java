package edloom.core;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Stream;

import static edloom.graphics.Statics.notifyWindow;

// a collection of static methods in a class that cannot be instantiated
public class Statics {
    private Statics() {
        throw new UnsupportedOperationException();
    }

    /**
     * Reads all .txt files in the Languages directory and creates Language type objects
     * @param arr where to save the languages
     * @throws FileNotFoundException
     * @see Language
     * @see File
     */
    public static void generateLang(ArrayList<Language> arr) throws FileNotFoundException {
        File folder = new File("src/Languages"); // directory where to look for languages
        int corrupted = 0; // count corrupted files

        for (File file : Objects.requireNonNull(folder.listFiles())) {  // scan the above mentioned directory
            if (file.isFile() && file.getName().endsWith(".txt") && lineCount(file) > 65) {
                Scanner scanner = new Scanner(file);
                Language language = new Language(file.getName().substring(0, file.getName().length() - 4));
                while (scanner.hasNextLine()) {
                    language.add(scanner.nextLine());
                }
                arr.add(language);
                scanner.close();
            } else corrupted++; // files that are not recognized as languages count as corrupted

        }
        // warning message for corrupted files
        if (corrupted > 0)
            notifyWindow(corrupted + " files in src/Languages are corrupted. \n They were skipped.", "File processing", 2);
    }



    /**
     * Creates an array of Integer that represents an interval
     * @param length length of interval
     * @param start starting index
     * @return the resulting array
     * @see Integer
     */
    public static Integer[] getInterval(int length, int start) {
        Integer[] interval = new Integer[length];
        for (int i = 0; i < length; i++) {
            interval[i] = start + i;
        }
        return interval;
    }


    /**
     * Gets the line count of a file
     * @param file the file to iterate through
     * @return line count if the file was successfully read, 0 if any IO exceptions occurred
     * @see File
     */
    public static long lineCount(File file) {
        try (Stream<String> stream = Files.lines(file.toPath(), StandardCharsets.UTF_8)) {
            return stream.count();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * generates a pseudo-random alphanumeric String
     * @return an alphanumeric String of maximum 9 characters
     * @see String
     */
    public static String generateKey() {
        Random random = new Random();

        return random.ints(48, 123)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(9)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }


    /**
     * Attempts to open a URL in browser
     * @param urlString the URL to open
     */
    public static void openWebpage(String urlString) {
        try {
            Desktop.getDesktop().browse(new URL(urlString).toURI());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
