package repl.banking.util;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

// utility class - all methods are static!
    // no constructor - cannot create an object
public class BankLogger {
    // initialize logger for this class
    // use built-in logging method in java
    private static final Logger logger = Logger.getLogger("BankingApp");

    // runs once when class is initializaed
    static {
        try {
            // create a FileHandler that writes to "app.log"
            // the 'true' param appends data to the file instead of overwriting it
            FileHandler fileHandler = new FileHandler("banking.log", true);

            // set a text formatter (otherwise it defaults to XML)
            fileHandler.setFormatter(new SimpleFormatter());

            // attach the file handler to your logger
            logger.addHandler(fileHandler);
            logger.setUseParentHandlers(false);

        } catch (IOException e) {
            System.err.println("Could not create log file.");
        }
    }

    public static void info(String message) {
        logger.info(message);
    }

    public static void error(String message) {
        logger.severe(message);
    }

}