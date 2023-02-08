import java.time.LocalDate;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.io.IOException;
import java.time.format.DateTimeParseException;

public class Days {
    public static void main(String... args) {
        LocalDate today = LocalDate.now();
        String birthdateString = System.getenv("BIRTHDATE");
        if (birthdateString != null && !birthdateString.isBlank()) {
            LocalDate birthdate = LocalDate.parse(birthdateString);
            if (today.getMonth() == birthdate.getMonth() && 
                today.getDayOfMonth() == birthdate.getDayOfMonth()) {
                System.out.println("Happy birthday!");
            }
        }
        
        String userHomeDirectory = System.getProperty("user.home");
        if (userHomeDirectory.isBlank()) {
            System.out.println("Unable to determine user home directory");
            System.exit(-1);
        }

        Path daysPath = Paths.get(userHomeDirectory, ".days");
        if (Files.notExists(daysPath)) {
            System.out.println(daysPath + " directory does not exist, please create it");
            System.exit(-1);
        }
        Path eventsPath = daysPath.resolve("events.csv");
        if (Files.notExists(eventsPath)) {
            System.out.println(eventsPath + " file not found");
            System.exit(-1);
        }

        // Create a new event list, initially empty.
        List<Event> events = new ArrayList<Event>();

        // Read all the lines from the events file.
        // Split them by commas and construct new Event instances.
        // Note that we skip the first line (column headers).
        try {
            List<String> lines = Files.readAllLines(eventsPath);

            boolean isHeaderLine = true;
            for (String line : lines) {
                if (isHeaderLine) {
                    isHeaderLine = false;
                    continue;
                }

                // Discard empty lines
                if (line.isBlank()) {
                    continue;
                }

                // Split line using static helper method
                List<String> columns = Days.getColumns(line);
                if (columns.size() != 3) {
                    System.err.println("line should have 3 columns: " + line);
                    continue;
                }

                // Try to parse the date column.
                // Discard the line if parsing fails.
                LocalDate date = null;
                String dateString = columns.get(0);
                try {
                     date = LocalDate.parse(dateString);

                    // Make a new event instance by parsing the
                    // date in column #0, and collecting
                    // category and description strings from
                    // columns #1 and #2.
                    Event event = new Event(
                        date,
                        columns.get(1),
                        columns.get(2)
                    );
    
                    events.add(event);
                }
                catch (java.time.format.DateTimeParseException dtpe) {
                    System.err.println("bad date: " + dateString);
                    continue;
                }
            }
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
            System.exit(-1);
        }

        // If we are still here, we might have some events
        // in the list. Print them out:
        for (Event event: events) {
            System.out.println(event);
        }
    }

    // Split the line into columns by commas.
    // Note that this will *not* work if the lines
    // have columns with embedded commas in them.
    // For proper CSV file processing, see OpenCSV,
    // https://opencsv.sourceforge.net/
    // and https://www.baeldung.com/opencsv
    private static List<String> getColumns(String line) {
        List<String> columns = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(line, ",");
        while (tokenizer.hasMoreElements()) {
            columns.add(tokenizer.nextToken());
        }
        return columns;
    }
}
