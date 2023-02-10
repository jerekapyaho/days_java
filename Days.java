import java.time.LocalDate;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.io.IOException;
import java.time.format.DateTimeParseException;
import java.time.Period;
import java.util.Map;
import java.util.HashMap;
import com.opencsv.CSVReaderHeaderAware;
import com.opencsv.exceptions.CsvValidationException;
import java.io.Reader;

import java.io.FileNotFoundException;
import java.io.FileReader;

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

        // Read all the lines from the events file using 
        // our helper based on opencsv.
        try {
            List<Map<String, String>> lineMaps = Days.readLineByLine(eventsPath);
            //System.out.println(lineMaps);

            for (Map<String, String> map : lineMaps) {
                LocalDate date = null;
                String dateString = map.get("date");
                try {
                    date = LocalDate.parse(dateString);
    
                    Event event = new Event(
                        date,
                        map.get("category"),
                        map.get("description")
                    );
    
                    events.add(event);
                }
                catch (DateTimeParseException dtpe) {
                    System.err.println("bad date: " + dateString);
                    continue;
                }
            }
        }
        catch (CsvValidationException cve) {
            cve.printStackTrace();
            System.exit(-1);
        }
        catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            System.exit(-1);
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
            System.exit(-1);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }

        // If we are still here, we might have some events
        // in the list. Print them out:
        for (Event event: events) {
            System.out.print(event + " -- ");

            Period difference = Period.between(event.getDate(), today);
            //System.out.println("diff = " + diff);

            System.out.println(Event.getDifferenceString(difference));
        }
    }

    // Read the CSV file one line at a time using a header-aware
    // CSV reader from the opencsv library. Discards invalid lines.
    public static List<Map<String, String>> readLineByLine(Path filePath)
            throws Exception {
        List<Map<String, String>> list = new ArrayList<>();
        try (Reader reader = Files.newBufferedReader(filePath)) {
            try (CSVReaderHeaderAware csvReader = new CSVReaderHeaderAware(reader)) {
                Map<String, String> map;
                while ((map = csvReader.readMap()) != null) {
                    list.add(map);
                }
            }
        }
        return list;
    }
}
